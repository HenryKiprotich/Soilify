from datetime import datetime, timedelta
from passlib.context import CryptContext
from jose import jwt, JWTError
from fastapi import Depends, HTTPException, status, Cookie

from app.config.settings import settings

# ✅ Password hashing context
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# ✅ Use settings from centralized configuration
SECRET_KEY = settings.SECRET_KEY
ALGORITHM = settings.ALGORITHM
ACCESS_TOKEN_EXPIRE_MINUTES = settings.ACCESS_TOKEN_EXPIRE_MINUTES

def hash_password(password: str) -> str:
    """Hash a password using bcrypt."""
    return pwd_context.hash(password)

def verify_password(plain_password: str, hashed_password: str) -> bool:
    """Verify a plain password against a hashed password."""
    return pwd_context.verify(plain_password, hashed_password)

def create_access_token(data: dict, expires_delta: timedelta = None) -> str:
    """
    Create a JWT access token.
    Args:
        data (dict): Data to include in the token (e.g., {"user_id": 1, ...}).
        expires_delta (timedelta, optional): Expiration duration.
    Returns:
        str: Encoded JWT token.
    """
    to_encode = data.copy()
    expire = datetime.utcnow() + (expires_delta or timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES))
    to_encode.update({"exp": expire})
    # If "user_id" is provided, move it into the "sub" claim as a string.
    if "user_id" in to_encode:
        to_encode["sub"] = str(to_encode.pop("user_id"))
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def decode_access_token(token: str):
    """
    Decode a JWT token.
    Args:
        token (str): The JWT token to decode.
    Returns:
        dict: Decoded token payload.
    Raises:
        HTTPException: If the token is invalid or expired.
    """
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except JWTError as e:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Invalid or expired token: {str(e)}",
            headers={"WWW-Authenticate": "Bearer"},
        )

async def get_current_user_from_cookie(token: str = Cookie(None)) -> dict:
    """
    Extracts user_id from the JWT stored in an HttpOnly cookie.
    Args:
        token (str): JWT token retrieved from the cookie.
    Returns:
        dict: A dictionary containing the user_id.
    Raises:
        HTTPException: If token is missing or invalid.
    """
    if token is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Not authenticated: token cookie missing"
        )
    payload = decode_access_token(token)
    user_id = payload.get("sub")
    if user_id is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token: user_id missing",
            headers={"WWW-Authenticate": "Bearer"},
        )
    return {"user_id": int(user_id)}

# --- New Functions for Password Reset ---

def create_reset_token(user_id: int, expires_delta: timedelta = None) -> str:
    to_encode = {"user_id": user_id}
    expire = datetime.utcnow() + (expires_delta or timedelta(minutes=30))  # Token valid for 30 minutes
    to_encode.update({"exp": expire})
    # Use a different claim name to avoid conflict if needed; here we use "sub" as well.
    to_encode["sub"] = str(user_id)
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def verify_reset_token(token: str) -> int:
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id = payload.get("sub")
        if user_id is None:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid reset token: user_id missing"
            )
        return int(user_id)
    except JWTError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid or expired reset token: {str(e)}"
        )
#email verification section   
def create_verification_token(user_id: int, expires_delta: timedelta = None) -> str:
    to_encode = {"user_id": user_id}
    expire = datetime.utcnow() + (expires_delta or timedelta(minutes=30))  # Valid for 30 minutes
    to_encode.update({"exp": expire})
    to_encode["sub"] = str(user_id)
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def verify_verification_token(token: str) -> int:
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id = payload.get("sub")
        if user_id is None:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid verification token: user_id missing"
            )
        return int(user_id)
    except JWTError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid or expired verification token: {str(e)}"
        )

