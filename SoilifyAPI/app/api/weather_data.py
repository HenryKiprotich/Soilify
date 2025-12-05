from fastapi import APIRouter, HTTPException, Depends, status, Query
from typing import List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text

from app.schemas.weather_data_schema import (
    WeatherDataCreate, 
    WeatherDataUpdate, 
    WeatherDataRead,
    WeatherDataStats
)
from app.schemas.field_schema import FieldOption
from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie

router = APIRouter(prefix="", tags=["weather-data"])

@router.get("/fields-dropdown", response_model=List[FieldOption])
async def get_fields_for_dropdown(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all fields belonging to the authenticated farmer for dropdown selection.
    This endpoint is used to populate the field dropdown when recording weather data.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            id, 
            field_name, 
            soil_type, 
            crop_type, 
            size_hectares
        FROM "Fields"
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

@router.get("", response_model=List[WeatherDataRead])
async def get_all_weather_data(
    field_id: Optional[int] = Query(None, description="Filter by field ID"),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all weather data for the authenticated farmer's fields.
    Optionally filter by field_id.
    Includes field name via JOIN.
    """
    farmer_id = current_user["user_id"]
    
    # Build query with optional field_id filter
    if field_id:
        query = text("""
            SELECT 
                wd.id,
                wd.field_id,
                f.field_name,
                wd.temperature,
                wd.rainfall,
                wd.soil_moisture,
                wd.created_at
            FROM "WeatherData" wd
            INNER JOIN "Fields" f ON wd.field_id = f.id
            WHERE f.farmer_id = :farmer_id AND wd.field_id = :field_id
            ORDER BY wd.created_at DESC
        """)
        result = await db.execute(query, {"farmer_id": farmer_id, "field_id": field_id})
    else:
        query = text("""
            SELECT 
                wd.id,
                wd.field_id,
                f.field_name,
                wd.temperature,
                wd.rainfall,
                wd.soil_moisture,
                wd.created_at
            FROM "WeatherData" wd
            INNER JOIN "Fields" f ON wd.field_id = f.id
            WHERE f.farmer_id = :farmer_id
            ORDER BY wd.created_at DESC
        """)
        result = await db.execute(query, {"farmer_id": farmer_id})
    
    rows = result.fetchall()
    
    records = []
    for row in rows:
        records.append({
            "id": row[0],
            "field_id": row[1],
            "field_name": row[2],
            "temperature": row[3],
            "rainfall": row[4],
            "soil_moisture": row[5],
            "created_at": row[6],
        })
    
    return records

@router.get("/stats", response_model=List[WeatherDataStats])
async def get_weather_statistics(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get weather statistics aggregated by field for the authenticated farmer.
    Returns average temperature, rainfall, soil moisture, and total readings per field.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            f.id AS field_id,
            f.field_name,
            AVG(wd.temperature) AS avg_temperature,
            AVG(wd.rainfall) AS avg_rainfall,
            AVG(wd.soil_moisture) AS avg_soil_moisture,
            COUNT(wd.id) AS total_readings,
            MAX(wd.created_at) AS latest_reading_date
        FROM "Fields" f
        LEFT JOIN "WeatherData" wd ON f.id = wd.field_id
        WHERE f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name
        ORDER BY f.field_name ASC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    stats = []
    for row in rows:
        stats.append({
            "field_id": row[0],
            "field_name": row[1],
            "avg_temperature": row[2],
            "avg_rainfall": row[3],
            "avg_soil_moisture": row[4],
            "total_readings": row[5],
            "latest_reading_date": row[6],
        })
    
    return stats

@router.get("/{weather_id}", response_model=WeatherDataRead)
async def get_weather_data(
    weather_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get a specific weather data record by ID.
    Only returns the record if the associated field belongs to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            wd.id,
            wd.field_id,
            f.field_name,
            wd.temperature,
            wd.rainfall,
            wd.soil_moisture,
            wd.created_at
        FROM "WeatherData" wd
        INNER JOIN "Fields" f ON wd.field_id = f.id
        WHERE wd.id = :weather_id AND f.farmer_id = :farmer_id
    """)
    
    result = await db.execute(query, {"weather_id": weather_id, "farmer_id": farmer_id})
    row = result.fetchone()
    
    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Weather data record not found or you don't have permission to access it"
        )
    
    return {
        "id": row[0],
        "field_id": row[1],
        "field_name": row[2],
        "temperature": row[3],
        "rainfall": row[4],
        "soil_moisture": row[5],
        "created_at": row[6],
    }

@router.post("", response_model=WeatherDataRead, status_code=status.HTTP_201_CREATED)
async def create_weather_data(
    weather: WeatherDataCreate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Create a new weather data record for a field.
    The field must belong to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    # Verify that the field belongs to the farmer
    check_field_query = text("""
        SELECT id, field_name FROM "Fields" 
        WHERE id = :field_id AND farmer_id = :farmer_id
    """)
    field_result = await db.execute(
        check_field_query, 
        {"field_id": weather.field_id, "farmer_id": farmer_id}
    )
    field_row = field_result.fetchone()
    
    if not field_row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Field not found or you don't have permission to add weather data to it"
        )
    
    # Insert the new weather data record
    insert_query = text("""
        INSERT INTO "WeatherData" 
            (field_id, temperature, rainfall, soil_moisture)
        VALUES 
            (:field_id, :temperature, :rainfall, :soil_moisture)
        RETURNING id, field_id, temperature, rainfall, soil_moisture, created_at
    """)
    
    result = await db.execute(
        insert_query,
        {
            "field_id": weather.field_id,
            "temperature": weather.temperature,
            "rainfall": weather.rainfall,
            "soil_moisture": weather.soil_moisture
        }
    )
    await db.commit()
    
    row = result.fetchone()
    
    return {
        "id": row[0],
        "field_id": row[1],
        "field_name": field_row[1],  # From the field check query
        "temperature": row[2],
        "rainfall": row[3],
        "soil_moisture": row[4],
        "created_at": row[5],
    }

@router.put("/{weather_id}", response_model=WeatherDataRead)
async def update_weather_data(
    weather_id: int,
    weather_update: WeatherDataUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Update an existing weather data record.
    Only the farmer who owns the associated field can update it.
    If field_id is being updated, it must belong to the farmer.
    """
    farmer_id = current_user["user_id"]
    
    # Check if record exists and the field belongs to the farmer
    check_query = text("""
        SELECT wd.id 
        FROM "WeatherData" wd
        INNER JOIN "Fields" f ON wd.field_id = f.id
        WHERE wd.id = :weather_id AND f.farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"weather_id": weather_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Weather data record not found or you don't have permission to update it"
        )
    
    # If field_id is being updated, verify it belongs to the farmer
    if weather_update.field_id is not None:
        check_field_query = text("""
            SELECT id FROM "Fields" 
            WHERE id = :field_id AND farmer_id = :farmer_id
        """)
        field_result = await db.execute(
            check_field_query, 
            {"field_id": weather_update.field_id, "farmer_id": farmer_id}
        )
        if not field_result.fetchone():
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Field not found or you don't have permission to use it"
            )
    
    # Build dynamic UPDATE query based on provided fields
    update_fields = []
    params = {"weather_id": weather_id}
    
    if weather_update.field_id is not None:
        update_fields.append("field_id = :field_id")
        params["field_id"] = weather_update.field_id
    
    if weather_update.temperature is not None:
        update_fields.append("temperature = :temperature")
        params["temperature"] = weather_update.temperature
    
    if weather_update.rainfall is not None:
        update_fields.append("rainfall = :rainfall")
        params["rainfall"] = weather_update.rainfall
    
    if weather_update.soil_moisture is not None:
        update_fields.append("soil_moisture = :soil_moisture")
        params["soil_moisture"] = weather_update.soil_moisture
    
    if not update_fields:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No fields to update"
        )
    
    # Execute update and return updated record with field name
    update_query = text(f"""
        UPDATE "WeatherData" 
        SET {', '.join(update_fields)}
        WHERE id = :weather_id
        RETURNING id, field_id, temperature, rainfall, soil_moisture, created_at
    """)
    
    result = await db.execute(update_query, params)
    await db.commit()
    
    row = result.fetchone()
    
    # Get field name
    field_query = text("SELECT field_name FROM \"Fields\" WHERE id = :field_id")
    field_result = await db.execute(field_query, {"field_id": row[1]})
    field_row = field_result.fetchone()
    
    return {
        "id": row[0],
        "field_id": row[1],
        "field_name": field_row[0] if field_row else None,
        "temperature": row[2],
        "rainfall": row[3],
        "soil_moisture": row[4],
        "created_at": row[5],
    }

@router.delete("/{weather_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_weather_data(
    weather_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Delete a weather data record.
    Only the farmer who owns the associated field can delete it.
    """
    farmer_id = current_user["user_id"]
    
    # Check if record exists and the field belongs to the farmer
    check_query = text("""
        SELECT wd.id 
        FROM "WeatherData" wd
        INNER JOIN "Fields" f ON wd.field_id = f.id
        WHERE wd.id = :weather_id AND f.farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"weather_id": weather_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Weather data record not found or you don't have permission to delete it"
        )
    
    # Delete the record
    delete_query = text("""
        DELETE FROM "WeatherData" 
        WHERE id = :weather_id
    """)
    
    await db.execute(delete_query, {"weather_id": weather_id})
    await db.commit()
    
    return None
