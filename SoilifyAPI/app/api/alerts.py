from fastapi import APIRouter, HTTPException, Depends, status, Query
from typing import List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from datetime import datetime, timedelta

from app.schemas.alerts_schema import (
    AlertCreate, 
    AlertUpdate, 
    AlertRead,
    AlertSummary
)
from app.schemas.field_schema import FieldOption
from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie

router = APIRouter(prefix="", tags=["alerts"])

@router.get("/fields-dropdown", response_model=List[FieldOption])
async def get_fields_for_dropdown(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all fields belonging to the authenticated farmer for dropdown selection.
    This endpoint is used to populate the field dropdown when creating field-specific alerts.
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

@router.get("", response_model=List[AlertRead])
async def get_all_alerts(
    field_id: Optional[int] = Query(None, description="Filter by field ID"),
    limit: Optional[int] = Query(50, description="Maximum number of alerts to return"),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get all alerts for the authenticated farmer.
    Optionally filter by field_id.
    Includes farmer name and field name via JOINs.
    """
    farmer_id = current_user["user_id"]
    
    # Build query with optional field_id filter
    if field_id:
        query = text("""
            SELECT 
                a.id,
                a.farmer_id,
                u.first_name || ' ' || COALESCE(u.other_name, '') AS farmer_name,
                a.field_id,
                f.field_name,
                a.message,
                a.created_at
            FROM alerts a
            INNER JOIN users u ON a.farmer_id = u.id
            LEFT JOIN fields f ON a.field_id = f.id
            WHERE a.farmer_id = :farmer_id AND a.field_id = :field_id
            ORDER BY a.created_at DESC
            LIMIT :limit
        """)
        result = await db.execute(query, {"farmer_id": farmer_id, "field_id": field_id, "limit": limit})
    else:
        query = text("""
            SELECT 
                a.id,
                a.farmer_id,
                u.first_name || ' ' || COALESCE(u.other_name, '') AS farmer_name,
                a.field_id,
                f.field_name,
                a.message,
                a.created_at
            FROM alerts a
            INNER JOIN users u ON a.farmer_id = u.id
            LEFT JOIN fields f ON a.field_id = f.id
            WHERE a.farmer_id = :farmer_id
            ORDER BY a.created_at DESC
            LIMIT :limit
        """)
        result = await db.execute(query, {"farmer_id": farmer_id, "limit": limit})
    
    rows = result.fetchall()
    
    alerts = []
    for row in rows:
        alerts.append({
            "id": row[0],
            "farmer_id": row[1],
            "farmer_name": row[2],
            "field_id": row[3],
            "field_name": row[4],
            "message": row[5],
            "created_at": row[6],
        })
    
    return alerts

@router.get("/summary", response_model=AlertSummary)
async def get_alerts_summary(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get summary statistics of alerts for the authenticated farmer.
    Returns total alerts, alerts today, alerts this week, and breakdown by field.
    """
    farmer_id = current_user["user_id"]
    
    # Get total alerts
    total_query = text("""
        SELECT COUNT(*) FROM alerts WHERE farmer_id = :farmer_id
    """)
    total_result = await db.execute(total_query, {"farmer_id": farmer_id})
    total_alerts = total_result.fetchone()[0]
    
    # Get alerts today
    today_query = text("""
        SELECT COUNT(*) FROM alerts 
        WHERE farmer_id = :farmer_id 
        AND DATE(created_at) = CURRENT_DATE
    """)
    today_result = await db.execute(today_query, {"farmer_id": farmer_id})
    alerts_today = today_result.fetchone()[0]
    
    # Get alerts this week
    week_query = text("""
        SELECT COUNT(*) FROM alerts 
        WHERE farmer_id = :farmer_id 
        AND created_at >= CURRENT_DATE - INTERVAL '7 days'
    """)
    week_result = await db.execute(week_query, {"farmer_id": farmer_id})
    alerts_this_week = week_result.fetchone()[0]
    
    # Get alerts by field
    field_query = text("""
        SELECT 
            COALESCE(f.field_name, 'General') AS field_name,
            COUNT(a.id) AS alert_count
        FROM alerts a
        LEFT JOIN fields f ON a.field_id = f.id
        WHERE a.farmer_id = :farmer_id
        GROUP BY f.field_name
        ORDER BY alert_count DESC
    """)
    field_result = await db.execute(field_query, {"farmer_id": farmer_id})
    field_rows = field_result.fetchall()
    
    alerts_by_field = {row[0]: row[1] for row in field_rows}
    
    return {
        "total_alerts": total_alerts,
        "alerts_today": alerts_today,
        "alerts_this_week": alerts_this_week,
        "alerts_by_field": alerts_by_field
    }

@router.get("/{alert_id}", response_model=AlertRead)
async def get_alert(
    alert_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get a specific alert by ID.
    Only returns the alert if it belongs to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            a.id,
            a.farmer_id,
            u.first_name || ' ' || COALESCE(u.other_name, '') AS farmer_name,
            a.field_id,
            f.field_name,
            a.message,
            a.created_at
        FROM alerts a
        INNER JOIN users u ON a.farmer_id = u.id
        LEFT JOIN fields f ON a.field_id = f.id
        WHERE a.id = :alert_id AND a.farmer_id = :farmer_id
    """)
    
    result = await db.execute(query, {"alert_id": alert_id, "farmer_id": farmer_id})
    row = result.fetchone()
    
    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Alert not found or you don't have permission to access it"
        )
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "farmer_name": row[2],
        "field_id": row[3],
        "field_name": row[4],
        "message": row[5],
        "created_at": row[6],
    }

@router.post("", response_model=AlertRead, status_code=status.HTTP_201_CREATED)
async def create_alert(
    alert: AlertCreate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Create a new alert for the authenticated farmer.
    Farmer ID is automatically extracted from the JWT token.
    The field_id is optional - if provided, it must belong to the farmer.
    """
    farmer_id = current_user["user_id"]
    
    # If field_id is provided, verify that the field belongs to the farmer
    field_name = None
    if alert.field_id:
        check_field_query = text("""
            SELECT id, field_name FROM fields 
            WHERE id = :field_id AND farmer_id = :farmer_id
        """)
        field_result = await db.execute(
            check_field_query, 
            {"field_id": alert.field_id, "farmer_id": farmer_id}
        )
        field_row = field_result.fetchone()
        
        if not field_row:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Field not found or you don't have permission to create alerts for it"
            )
        field_name = field_row[1]
    
    # Get farmer name
    farmer_query = text("""
        SELECT first_name || ' ' || COALESCE(other_name, '') AS farmer_name 
        FROM users WHERE id = :farmer_id
    """)
    farmer_result = await db.execute(farmer_query, {"farmer_id": farmer_id})
    farmer_name = farmer_result.fetchone()[0]
    
    # Insert the new alert
    insert_query = text("""
        INSERT INTO alerts (farmer_id, field_id, message)
        VALUES (:farmer_id, :field_id, :message)
        RETURNING id, farmer_id, field_id, message, created_at
    """)
    
    result = await db.execute(
        insert_query,
        {
            "farmer_id": farmer_id,
            "field_id": alert.field_id,
            "message": alert.message
        }
    )
    await db.commit()
    
    row = result.fetchone()
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "farmer_name": farmer_name,
        "field_id": row[2],
        "field_name": field_name,
        "message": row[3],
        "created_at": row[4],
    }

@router.put("/{alert_id}", response_model=AlertRead)
async def update_alert(
    alert_id: int,
    alert_update: AlertUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Update an existing alert.
    Only the farmer who owns the alert can update it.
    If field_id is being updated, it must belong to the farmer.
    """
    farmer_id = current_user["user_id"]
    
    # Check if alert exists and belongs to the farmer
    check_query = text("""
        SELECT id FROM alerts 
        WHERE id = :alert_id AND farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"alert_id": alert_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Alert not found or you don't have permission to update it"
        )
    
    # If field_id is being updated, verify it belongs to the farmer
    if alert_update.field_id is not None:
        check_field_query = text("""
            SELECT id FROM fields 
            WHERE id = :field_id AND farmer_id = :farmer_id
        """)
        field_result = await db.execute(
            check_field_query, 
            {"field_id": alert_update.field_id, "farmer_id": farmer_id}
        )
        if not field_result.fetchone():
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Field not found or you don't have permission to use it"
            )
    
    # Build dynamic UPDATE query based on provided fields
    update_fields = []
    params = {"alert_id": alert_id, "farmer_id": farmer_id}
    
    if alert_update.field_id is not None:
        update_fields.append("field_id = :field_id")
        params["field_id"] = alert_update.field_id
    
    if alert_update.message is not None:
        update_fields.append("message = :message")
        params["message"] = alert_update.message
    
    if not update_fields:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No fields to update"
        )
    
    # Execute update and return updated record
    update_query = text(f"""
        UPDATE alerts 
        SET {', '.join(update_fields)}
        WHERE id = :alert_id AND farmer_id = :farmer_id
        RETURNING id, farmer_id, field_id, message, created_at
    """)
    
    result = await db.execute(update_query, params)
    await db.commit()
    
    row = result.fetchone()
    
    # Get farmer name and field name
    details_query = text("""
        SELECT 
            u.first_name || ' ' || COALESCE(u.other_name, '') AS farmer_name,
            f.field_name
        FROM users u
        LEFT JOIN fields f ON f.id = :field_id
        WHERE u.id = :farmer_id
    """)
    details_result = await db.execute(details_query, {"farmer_id": row[1], "field_id": row[2]})
    details_row = details_result.fetchone()
    
    return {
        "id": row[0],
        "farmer_id": row[1],
        "farmer_name": details_row[0] if details_row else None,
        "field_id": row[2],
        "field_name": details_row[1] if details_row else None,
        "message": row[3],
        "created_at": row[4],
    }

@router.delete("/{alert_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_alert(
    alert_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Delete an alert.
    Only the farmer who owns the alert can delete it.
    """
    farmer_id = current_user["user_id"]
    
    # Check if alert exists and belongs to the farmer
    check_query = text("""
        SELECT id FROM alerts 
        WHERE id = :alert_id AND farmer_id = :farmer_id
    """)
    check_result = await db.execute(check_query, {"alert_id": alert_id, "farmer_id": farmer_id})
    
    if not check_result.fetchone():
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Alert not found or you don't have permission to delete it"
        )
    
    # Delete the alert
    delete_query = text("""
        DELETE FROM alerts 
        WHERE id = :alert_id AND farmer_id = :farmer_id
    """)
    
    await db.execute(delete_query, {"alert_id": alert_id, "farmer_id": farmer_id})
    await db.commit()
    
    return None

@router.delete("/bulk/delete", status_code=status.HTTP_204_NO_CONTENT)
async def bulk_delete_alerts(
    alert_ids: List[int],
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Delete multiple alerts at once.
    Only deletes alerts that belong to the authenticated farmer.
    """
    farmer_id = current_user["user_id"]
    
    if not alert_ids:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No alert IDs provided"
        )
    
    # Convert alert_ids to a format suitable for SQL IN clause
    ids_placeholder = ','.join([str(id) for id in alert_ids])
    
    # Delete alerts that belong to the farmer
    delete_query = text(f"""
        DELETE FROM alerts 
        WHERE id IN ({ids_placeholder}) AND farmer_id = :farmer_id
    """)
    
    result = await db.execute(delete_query, {"farmer_id": farmer_id})
    await db.commit()
    
    return None
