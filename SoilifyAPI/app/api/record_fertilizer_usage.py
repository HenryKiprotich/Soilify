from fastapi import APIRouter, HTTPException, Depends, status
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text

from app.schemas.fertilizer_usage_schema import (
    FertiliserUsageCreate, 
    FertiliserUsageUpdate, 
    FertiliserUsageRead,
    FieldOption
)
from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie

router = APIRouter(prefix="", tags=["fertiliser-usage"])

@router.get("/fields-dropdown", response_model=List[FieldOption])
async def get_fields_for_dropdown(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all fields belonging to the authenticated farmer for dropdown selection.
    This endpoint is used to populate the field dropdown when creating fertiliser usage records.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            id, 
            field_name, 
            soil_type, 
            crop_type, 
            size_hectares
        FROM fields
        WHERE farmer_id = :farmer_id
        ORDER BY field_name ASC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    fields = []
    for row in rows:
        fields.append({
            "id": row[0],
            "field_name": row[1],
            "soil_type": row[2],
            "crop_type": row[3],
            "size_hectares": row[4],
        })
    
    return fields

@router.get("", response_model=List[FertiliserUsageRead])
async def get_all_fertiliser_usage(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all fertiliser usage records for the authenticated farmer.
    Includes field name via JOIN.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            fu.id,
            fu.farmer_id,
            fu.field_id,
            f.field_name,
            fu.fertiliser_type,
            fu.amount_kg,
            fu.weather,
            fu.notes,
            fu.date,
            fu.created_at
        FROM fertiliserusage fu
        LEFT JOIN fields f ON fu.field_id = f.id
        WHERE fu.farmer_id = :farmer_id
        ORDER BY fu.date DESC, fu.created_at DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    records = []
    for row in rows:
        records.append({
            "id": row[0],
            "farmer_id": row[1],
            "field_id": row[2],
            "field_name": row[3],
            "fertiliser_type": row[4],
            "amount_kg": row[5],
            "weather": row[6],
            "notes": row[7],
            "date": row[8],
            "created_at": row[9],
        })
    
    return records

@router.get("/{usage_id}", response_model=FertiliserUsageRead)
async def get_fertiliser_usage(
    usage_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get a specific fertiliser usage record by ID.
    Only returns the record if it belongs to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            fu.id,
            fu.farmer_id,
            fu.field_id,
            f.field_name,
            fu.fertiliser_type,
            fu.amount_kg,
            fu.weather,
            fu.notes,
            fu.date,
            fu.created_at
        FROM fertiliserusage fu
        LEFT JOIN fields f ON fu.field_id = f.id
        WHERE fu.id = :usage_id AND fu.farmer_id = :farmer_id
    """)
    
    result = await db.execute(query, {"usage_id": usage_id, "farmer_id": farmer_id})
    row = result.fetchone()
    
    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Fertiliser usage record not found or you don't have permission to access it"
        )
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "field_id": row[2],
        "field_name": row[3],
        "fertiliser_type": row[4],
        "amount_kg": row[5],
        "weather": row[6],
        "notes": row[7],
        "date": row[8],
        "created_at": row[9],
    }

@router.post("", response_model=FertiliserUsageRead, status_code=status.HTTP_201_CREATED)
async def create_fertiliser_usage(
    usage: FertiliserUsageCreate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Create a new fertiliser usage record for the authenticated farmer.
    Farmer ID is automatically extracted from the JWT token.
    The field_id must belong to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    # Verify that the field belongs to the farmer
    check_field_query = text("""
        SELECT id, field_name FROM fields 
        WHERE id = :field_id AND farmer_id = :farmer_id
    """)
    field_result = await db.execute(
        check_field_query, 
        {"field_id": usage.field_id, "farmer_id": farmer_id}
    )
    field_row = field_result.fetchone()
    
    if not field_row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Field not found or you don't have permission to use it"
        )
    
    # Insert the new fertiliser usage record
    insert_query = text("""
        INSERT INTO fertiliserusage 
            (farmer_id, field_id, fertiliser_type, amount_kg, weather, notes, date)
        VALUES 
            (:farmer_id, :field_id, :fertiliser_type, :amount_kg, :weather, :notes, :date)
        RETURNING id, farmer_id, field_id, fertiliser_type, amount_kg, weather, notes, date, created_at
    """)
    
    result = await db.execute(
        insert_query,
        {
            "farmer_id": farmer_id,
            "field_id": usage.field_id,
            "fertiliser_type": usage.fertiliser_type,
            "amount_kg": usage.amount_kg,
            "weather": usage.weather,
            "notes": usage.notes,
            "date": usage.date
        }
    )
    await db.commit()
    
    row = result.fetchone()
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "field_id": row[2],
        "field_name": field_row[1],  # From the field check query
        "fertiliser_type": row[3],
        "amount_kg": row[4],
        "weather": row[5],
        "notes": row[6],
        "date": row[7],
        "created_at": row[8],
    }

@router.put("/{usage_id}", response_model=FertiliserUsageRead)
async def update_fertiliser_usage(
    usage_id: int,
    usage_update: FertiliserUsageUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Update an existing fertiliser usage record.
    Only the farmer who owns the record can update it.
    If field_id is being updated, it must belong to the farmer.
    """
    farmer_id = current_user["user_id"]
    
    # Check if record exists and belongs to the farmer
    check_query = text("""
        SELECT id FROM fertiliserusage 
        WHERE id = :usage_id AND farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"usage_id": usage_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Fertiliser usage record not found or you don't have permission to update it"
        )
    
    # If field_id is being updated, verify it belongs to the farmer
    if usage_update.field_id is not None:
        check_field_query = text("""
            SELECT id FROM fields 
            WHERE id = :field_id AND farmer_id = :farmer_id
        """)
        field_result = await db.execute(
            check_field_query, 
            {"field_id": usage_update.field_id, "farmer_id": farmer_id}
        )
        if not field_result.fetchone():
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Field not found or you don't have permission to use it"
            )
    
    # Build dynamic UPDATE query based on provided fields
    update_fields = []
    params = {"usage_id": usage_id, "farmer_id": farmer_id}
    
    if usage_update.field_id is not None:
        update_fields.append("field_id = :field_id")
        params["field_id"] = usage_update.field_id
    
    if usage_update.fertiliser_type is not None:
        update_fields.append("fertiliser_type = :fertiliser_type")
        params["fertiliser_type"] = usage_update.fertiliser_type
    
    if usage_update.amount_kg is not None:
        update_fields.append("amount_kg = :amount_kg")
        params["amount_kg"] = usage_update.amount_kg
    
    if usage_update.weather is not None:
        update_fields.append("weather = :weather")
        params["weather"] = usage_update.weather
    
    if usage_update.notes is not None:
        update_fields.append("notes = :notes")
        params["notes"] = usage_update.notes
    
    if usage_update.date is not None:
        update_fields.append("date = :date")
        params["date"] = usage_update.date
    
    if not update_fields:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No fields to update"
        )
    
    # Execute update and return updated record with field name
    update_query = text(f"""
        UPDATE fertiliserusage 
        SET {', '.join(update_fields)}
        WHERE id = :usage_id AND farmer_id = :farmer_id
        RETURNING id, farmer_id, field_id, fertiliser_type, amount_kg, weather, notes, date, created_at
    """)
    
    result = await db.execute(update_query, params)
    await db.commit()
    
    row = result.fetchone()
    
    # Get field name
    field_query = text("SELECT field_name FROM fields WHERE id = :field_id")
    field_result = await db.execute(field_query, {"field_id": row[2]})
    field_row = field_result.fetchone()
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "field_id": row[2],
        "field_name": field_row[0] if field_row else None,
        "fertiliser_type": row[3],
        "amount_kg": row[4],
        "weather": row[5],
        "notes": row[6],
        "date": row[7],
        "created_at": row[8],
    }

@router.delete("/{usage_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_fertiliser_usage(
    usage_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Delete a fertiliser usage record.
    Only the farmer who owns the record can delete it.
    """
    farmer_id = current_user["user_id"]
    
    # Check if record exists and belongs to the farmer
    check_query = text("""
        SELECT id FROM fertiliserusage 
        WHERE id = :usage_id AND farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"usage_id": usage_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Fertiliser usage record not found or you don't have permission to delete it"
        )
    
    # Delete the record
    delete_query = text("""
        DELETE FROM fertiliserusage 
        WHERE id = :usage_id AND farmer_id = :farmer_id
    """)
    
    await db.execute(delete_query, {"usage_id": usage_id, "farmer_id": farmer_id})
    await db.commit()
    
    return None
