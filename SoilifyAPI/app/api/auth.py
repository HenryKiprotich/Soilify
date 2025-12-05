from fastapi import APIRouter, HTTPException, Depends, status, Cookie
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi.responses import JSONResponse
from app.schemas.user_schema import UserCreate, UserLogin, Token, UserEmail, ResetPasswordRequest
from app.database.database import get_db
from app.services.auth_service import (
    hash_password,
    verify_password,
    create_access_token,
    decode_access_token,    
    create_reset_token,
    verify_reset_token
)
from sqlalchemy import text

router = APIRouter(prefix="")

# Endpoint for user signup
@router.post("/signup", response_model=Token)
async def signup(user: UserCreate, db: AsyncSession = Depends(get_db)):
    # Check if user already exists
    check_query = text("""
        SELECT id FROM "Users" 
        WHERE phone_number = :phone_number OR email_adress = :email_adress
    """)
    result = await db.execute(
        check_query, 
        {"phone_number": user.phone_number, "email_adress": user.email_adress}
    )
    existing_user = result.fetchone()
    
    if existing_user:
        raise HTTPException(status_code=400, detail="Phone number or email already registered")
    
    # Insert new user
    insert_query = text("""
        INSERT INTO "Users" (first_name, other_name, phone_number, email_adress, password_hash, location)
        VALUES (:first_name, :other_name, :phone_number, :email_adress, :password_hash, :location)
        RETURNING id
    """)
    
    result = await db.execute(
        insert_query,
        {
            "first_name": user.first_name,
            "other_name": user.other_name,
            "phone_number": user.phone_number,
            "email_adress": user.email_adress,
            "password_hash": hash_password(user.password),
            "location": user.location
        }
    )
    await db.commit()
    
    new_user_id = result.fetchone()[0]
    access_token = create_access_token({"user_id": new_user_id})
    return {"access_token": access_token, "token_type": "bearer"}

# Endpoint for user sign-in
@router.post("/signin")
async def signin(user: UserLogin, db: AsyncSession = Depends(get_db)):
    if not user.email_adress and not user.phone_number:
        raise HTTPException(status_code=400, detail="Email or phone number required")
    
    # Query user by email or phone number
    if user.email_adress:
        query = text("SELECT id, password_hash FROM \"Users\" WHERE email_adress = :email_adress")
        result = await db.execute(query, {"email_adress": user.email_adress})
    else:
        query = text("SELECT id, password_hash FROM \"Users\" WHERE phone_number = :phone_number")
        result = await db.execute(query, {"phone_number": user.phone_number})
    
    db_user = result.fetchone()
    
    if not db_user:
        raise HTTPException(status_code=401, detail="Invalid credentials")
    
    user_id, password_hash = db_user
    
    if not verify_password(user.password, password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    
    # Create JWT token
    access_token = create_access_token({"user_id": user_id})
    response = JSONResponse(content={
        "message": "Login successful",
        "access_token": access_token,
        "token_type": "bearer"
    })
    response.set_cookie(
        key="token", 
        value=access_token, 
        httponly=True,
        secure=False,   # Set to True in production with HTTPS
        samesite="none",  # Changed from "lax" to "none" for cross-origin requests
        max_age=3600
        # Removed domain parameter - cookies will be sent to the server that set them
    )
    return response

# Dependency to extract token from cookie
def get_token_from_cookie(token: str = Cookie(None)):
    if token is None:
        raise HTTPException(status_code=401, detail="Not authenticated: token cookie missing")
    return token

@router.get("/verify")
async def verify(token: str = Depends(get_token_from_cookie)):
    payload = decode_access_token(token)
    return {"detail": f"Token valid for user: {payload.get('sub')}"}

# --- Password Reset Endpoints ---
@router.post("/forgot-password")
async def forgot_password(user_email: UserEmail, db: AsyncSession = Depends(get_db)):
    """
    Endpoint to initiate password reset.
    Checks if the user exists and generates a reset token.
    """
    query = text("SELECT id FROM \"Users\" WHERE email_adress = :email_adress")
    result = await db.execute(query, {"email_adress": user_email.email_adress})
    user = result.fetchone()
    
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    user_id = user[0]
    reset_token = create_reset_token(user_id)
    # In production, send the reset token via email
    return {"message": "Password reset link sent.", "reset_token": reset_token}

@router.post("/reset-password")
async def reset_password(reset_data: ResetPasswordRequest, db: AsyncSession = Depends(get_db)):
    """
    Endpoint to reset the password.
    Verifies the reset token, then updates the user's password.
    """
    user_id = verify_reset_token(reset_data.token)
    
    # Check if user exists
    check_query = text("SELECT id FROM \"Users\" WHERE id = :user_id")
    result = await db.execute(check_query, {"user_id": user_id})
    user = result.fetchone()
    
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    
    # Update password
    update_query = text("""
        UPDATE "Users" 
        SET password_hash = :password_hash 
        WHERE id = :user_id
    """)
    await db.execute(
        update_query, 
        {"password_hash": hash_password(reset_data.new_password), "user_id": user_id}
    )
    await db.commit()
    
    return {"message": "Password updated successfully."}
