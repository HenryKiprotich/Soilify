from fastapi import APIRouter, HTTPException, Depends, status
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text

from app.schemas.field_schema import FieldCreate, FieldUpdate, FieldRead
from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie

router = APIRouter(prefix="", tags=["fields"])

@router.get("", response_model=List[FieldRead])
async def get_all_fields(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all fields for the authenticated farmer.
    Farmer ID is extracted from the JWT token.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            id, 
            farmer_id, 
            field_name, 
            soil_type, 
            crop_type, 
            size_hectares, 
            created_at
        FROM fields
        WHERE farmer_id = :farmer_id
        ORDER BY created_at DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    fields = []
    for row in rows:
        fields.append({
            "id": row[0],
            "farmer_id": row[1],
            "field_name": row[2],
            "soil_type": row[3],
            "crop_type": row[4],
            "size_hectares": row[5],
            "created_at": row[6],
        })
    
    return fields

@router.get("/{field_id}", response_model=FieldRead)
async def get_field(
    field_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get a specific field by ID.
    Only returns the field if it belongs to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            id, 
            farmer_id, 
            field_name, 
            soil_type, 
            crop_type, 
            size_hectares, 
            created_at
        FROM fields
        WHERE id = :field_id AND farmer_id = :farmer_id
    """)
    
    result = await db.execute(query, {"field_id": field_id, "farmer_id": farmer_id})
    row = result.fetchone()
    
    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Field not found or you don't have permission to access it"
        )
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "field_name": row[2],
        "soil_type": row[3],
        "crop_type": row[4],
        "size_hectares": row[5],
        "created_at": row[6],
    }

@router.post("", response_model=FieldRead, status_code=status.HTTP_201_CREATED)
async def create_field(
    field: FieldCreate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Create a new field for the authenticated farmer.
    Farmer ID is automatically extracted from the JWT token.
    """
    farmer_id = current_user["user_id"]
    
    # Verify that the farmer exists
    check_user_query = text("SELECT id FROM users WHERE id = :farmer_id")
    user_result = await db.execute(check_user_query, {"farmer_id": farmer_id})
    if not user_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    
    # Insert the new field
    insert_query = text("""
        INSERT INTO fields (farmer_id, field_name, soil_type, crop_type, size_hectares)
        VALUES (:farmer_id, :field_name, :soil_type, :crop_type, :size_hectares)
        RETURNING id, farmer_id, field_name, soil_type, crop_type, size_hectares, created_at
    """)
    
    result = await db.execute(
        insert_query,
        {
            "farmer_id": farmer_id,
            "field_name": field.field_name,
            "soil_type": field.soil_type,
            "crop_type": field.crop_type,
            "size_hectares": field.size_hectares
        }
    )
    await db.commit()
    
    row = result.fetchone()
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "field_name": row[2],
        "soil_type": row[3],
        "crop_type": row[4],
        "size_hectares": row[5],
        "created_at": row[6],
    }

@router.put("/{field_id}", response_model=FieldRead)
async def update_field(
    field_id: int,
    field_update: FieldUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Update an existing field.
    Only the farmer who owns the field can update it.
    """
    farmer_id = current_user["user_id"]
    
    # Check if field exists and belongs to the farmer
    check_query = text("""
        SELECT id FROM fields 
        WHERE id = :field_id AND farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"field_id": field_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Field not found or you don't have permission to update it"
        )
    
    # Build dynamic UPDATE query based on provided fields
    update_fields = []
    params = {"field_id": field_id, "farmer_id": farmer_id}
    
    if field_update.field_name is not None:
        update_fields.append("field_name = :field_name")
        params["field_name"] = field_update.field_name
    
    if field_update.soil_type is not None:
        update_fields.append("soil_type = :soil_type")
        params["soil_type"] = field_update.soil_type
    
    if field_update.crop_type is not None:
        update_fields.append("crop_type = :crop_type")
        params["crop_type"] = field_update.crop_type
    
    if field_update.size_hectares is not None:
        update_fields.append("size_hectares = :size_hectares")
        params["size_hectares"] = field_update.size_hectares
    
    if not update_fields:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No fields to update"
        )
    
    # Execute update
    update_query = text(f"""
        UPDATE fields 
        SET {', '.join(update_fields)}
        WHERE id = :field_id AND farmer_id = :farmer_id
        RETURNING id, farmer_id, field_name, soil_type, crop_type, size_hectares, created_at
    """)
    
    result = await db.execute(update_query, params)
    await db.commit()
    
    row = result.fetchone()
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "field_name": row[2],
        "soil_type": row[3],
        "crop_type": row[4],
        "size_hectares": row[5],
        "created_at": row[6],
    }

@router.delete("/{field_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_field(
    field_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Delete a field.
    Only the farmer who owns the field can delete it.
    """
    farmer_id = current_user["user_id"]
    
    # Check if field exists and belongs to the farmer
    check_query = text("""
        SELECT id FROM fields 
        WHERE id = :field_id AND farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"field_id": field_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Field not found or you don't have permission to delete it"
        )
    
    # Delete the field
    delete_query = text("""
        DELETE FROM fields 
        WHERE id = :field_id AND farmer_id = :farmer_id
    """)
    
    await db.execute(delete_query, {"field_id": field_id, "farmer_id": farmer_id})
    await db.commit()
    
    return None
