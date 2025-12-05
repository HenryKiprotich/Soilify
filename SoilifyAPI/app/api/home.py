"""
Home/Dashboard Endpoints for Soilify API
Provides welcome message, quick stats, and recent alerts for dashboard
"""

from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from datetime import datetime, timedelta

from app.schemas.home_schema import (
    DashboardResponse,
    DashboardWithFields,
    UserWelcome,
    QuickStats,
    RecentAlert,
    FieldSummaryCard
)
from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie

router = APIRouter(prefix="", tags=["home"])


def get_time_ago(created_at: datetime) -> str:
    """Convert datetime to human-readable time ago string"""
    now = datetime.now(created_at.tzinfo) if created_at.tzinfo else datetime.now()
    diff = now - created_at
    
    seconds = diff.total_seconds()
    
    if seconds < 60:
        return "Just now"
    elif seconds < 3600:
        minutes = int(seconds / 60)
        return f"{minutes} minute{'s' if minutes != 1 else ''} ago"
    elif seconds < 86400:
        hours = int(seconds / 3600)
        return f"{hours} hour{'s' if hours != 1 else ''} ago"
    elif seconds < 604800:
        days = int(seconds / 86400)
        return f"{days} day{'s' if days != 1 else ''} ago"
    elif seconds < 2592000:
        weeks = int(seconds / 604800)
        return f"{weeks} week{'s' if weeks != 1 else ''} ago"
    else:
        months = int(seconds / 2592000)
        return f"{months} month{'s' if months != 1 else ''} ago"


def get_greeting_message(first_name: str) -> str:
    """Generate personalized greeting based on time of day"""
    current_hour = datetime.now().hour
    
    if 5 <= current_hour < 12:
        greeting = "Good morning"
    elif 12 <= current_hour < 17:
        greeting = "Good afternoon"
    elif 17 <= current_hour < 21:
        greeting = "Good evening"
    else:
        greeting = "Hello"
    
    return f"{greeting}, {first_name}! 🌾"


@router.get("/dashboard", response_model=DashboardResponse)
async def get_dashboard(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get dashboard/home screen data for Android app.
    
    Returns:
    - Welcome message with user's name
    - Quick statistics (fields, alerts, recent activity)
    - Latest alerts (up to 5 most recent)
    """
    farmer_id = current_user["user_id"]
    
    # Get user details
    user_query = text("""
        SELECT 
            id,
            first_name,
            other_name,
            location
        FROM "Users"
        WHERE id = :farmer_id
    """)
    user_result = await db.execute(user_query, {"farmer_id": farmer_id})
    user_row = user_result.fetchone()
    
    first_name = user_row[1]
    full_name = f"{user_row[1]} {user_row[2]}" if user_row[2] else user_row[1]
    location = user_row[3]
    
    # Get quick statistics
    stats_query = text("""
        SELECT 
            (SELECT COUNT(*) FROM "Fields" WHERE farmer_id = :farmer_id) as total_fields,
            (SELECT COUNT(*) FROM "Alerts" 
             WHERE farmer_id = :farmer_id 
             AND created_at >= CURRENT_DATE - INTERVAL '7 days') as pending_alerts,
            (SELECT MAX(date) FROM "FertiliserUsage" 
             WHERE farmer_id = :farmer_id) as last_fert_date,
            (SELECT COUNT(*) FROM "WeatherData" w
             INNER JOIN "Fields" f ON w.field_id = f.id
             WHERE f.farmer_id = :farmer_id
             AND DATE(w.created_at) = CURRENT_DATE) as weather_today
    """)
    stats_result = await db.execute(stats_query, {"farmer_id": farmer_id})
    stats_row = stats_result.fetchone()
    
    # Get recent alerts (last 5)
    alerts_query = text("""
        SELECT 
            a.id,
            a.message,
            a.field_id,
            f.field_name,
            a.created_at
        FROM "Alerts" a
        LEFT JOIN "Fields" f ON a.field_id = f.id
        WHERE a.farmer_id = :farmer_id
        ORDER BY a.created_at DESC
        LIMIT 5
    """)
    alerts_result = await db.execute(alerts_query, {"farmer_id": farmer_id})
    alerts_rows = alerts_result.fetchall()
    
    # Check if user has fields and activity
    has_fields = stats_row[0] > 0
    
    activity_query = text("""
        SELECT 
            EXISTS(SELECT 1 FROM "FertiliserUsage" WHERE farmer_id = :farmer_id) OR
            EXISTS(SELECT 1 FROM "WeatherData" w
                   INNER JOIN "Fields" f ON w.field_id = f.id
                   WHERE f.farmer_id = :farmer_id) OR
            EXISTS(SELECT 1 FROM "Alerts" WHERE farmer_id = :farmer_id)
        AS has_activity
    """)
    activity_result = await db.execute(activity_query, {"farmer_id": farmer_id})
    has_activity = activity_result.fetchone()[0]
    
    # Build response
    return {
        "welcome": {
            "user_id": farmer_id,
            "first_name": first_name,
            "full_name": full_name,
            "location": location,
            "greeting_message": get_greeting_message(first_name)
        },
        "quick_stats": {
            "total_fields": stats_row[0],
            "pending_alerts": stats_row[1],
            "last_fertilizer_date": stats_row[2].strftime("%Y-%m-%d") if stats_row[2] else None,
            "weather_records_today": stats_row[3]
        },
        "recent_alerts": [
            {
                "id": row[0],
                "message": row[1],
                "field_id": row[2],
                "field_name": row[3],
                "created_at": row[4],
                "time_ago": get_time_ago(row[4])
            }
            for row in alerts_rows
        ],
        "has_fields": has_fields,
        "has_activity": has_activity
    }


@router.get("/dashboard/with-fields", response_model=DashboardWithFields)
async def get_dashboard_with_fields(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get dashboard with field summary cards.
    
    Similar to /dashboard but also includes summary cards for each field,
    useful if you want to show field cards on the home screen.
    """
    farmer_id = current_user["user_id"]
    
    # Get user details
    user_query = text("""
        SELECT 
            id,
            first_name,
            other_name,
            location
        FROM "Users"
        WHERE id = :farmer_id
    """)
    user_result = await db.execute(user_query, {"farmer_id": farmer_id})
    user_row = user_result.fetchone()
    
    first_name = user_row[1]
    full_name = f"{user_row[1]} {user_row[2]}" if user_row[2] else user_row[1]
    location = user_row[3]
    
    # Get quick statistics
    stats_query = text("""
        SELECT 
            (SELECT COUNT(*) FROM "Fields" WHERE farmer_id = :farmer_id) as total_fields,
            (SELECT COUNT(*) FROM "Alerts" 
             WHERE farmer_id = :farmer_id 
             AND created_at >= CURRENT_DATE - INTERVAL '7 days') as pending_alerts,
            (SELECT MAX(date) FROM "FertiliserUsage" 
             WHERE farmer_id = :farmer_id) as last_fert_date,
            (SELECT COUNT(*) FROM "WeatherData" w
             INNER JOIN "Fields" f ON w.field_id = f.id
             WHERE f.farmer_id = :farmer_id
             AND DATE(w.created_at) = CURRENT_DATE) as weather_today
    """)
    stats_result = await db.execute(stats_query, {"farmer_id": farmer_id})
    stats_row = stats_result.fetchone()
    
    # Get recent alerts (last 5)
    alerts_query = text("""
        SELECT 
            a.id,
            a.message,
            a.field_id,
            f.field_name,
            a.created_at
        FROM "Alerts" a
        LEFT JOIN "Fields" f ON a.field_id = f.id
        WHERE a.farmer_id = :farmer_id
        ORDER BY a.created_at DESC
        LIMIT 5
    """)
    alerts_result = await db.execute(alerts_query, {"farmer_id": farmer_id})
    alerts_rows = alerts_result.fetchall()
    
    # Get field summary cards
    fields_query = text("""
        SELECT 
            f.id,
            f.field_name,
            f.crop_type,
            f.size_hectares,
            MAX(GREATEST(
                fu.created_at,
                w.created_at,
                a.created_at
            )) as last_activity,
            COUNT(DISTINCT a.id) as alert_count
        FROM "Fields" f
        LEFT JOIN "FertiliserUsage" fu ON f.id = fu.field_id
        LEFT JOIN "WeatherData" w ON f.id = w.field_id
        LEFT JOIN "Alerts" a ON f.id = a.field_id
        WHERE f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name, f.crop_type, f.size_hectares
        ORDER BY f.field_name ASC
    """)
    fields_result = await db.execute(fields_query, {"farmer_id": farmer_id})
    fields_rows = fields_result.fetchall()
    
    # Build response
    return {
        "welcome": {
            "user_id": farmer_id,
            "first_name": first_name,
            "full_name": full_name,
            "location": location,
            "greeting_message": get_greeting_message(first_name)
        },
        "quick_stats": {
            "total_fields": stats_row[0],
            "pending_alerts": stats_row[1],
            "last_fertilizer_date": stats_row[2].strftime("%Y-%m-%d") if stats_row[2] else None,
            "weather_records_today": stats_row[3]
        },
        "recent_alerts": [
            {
                "id": row[0],
                "message": row[1],
                "field_id": row[2],
                "field_name": row[3],
                "created_at": row[4],
                "time_ago": get_time_ago(row[4])
            }
            for row in alerts_rows
        ],
        "field_cards": [
            {
                "field_id": row[0],
                "field_name": row[1],
                "crop_type": row[2],
                "size_hectares": float(row[3]) if row[3] else None,
                "last_activity": row[4],
                "alert_count": row[5]
            }
            for row in fields_rows
        ]
    }


@router.get("/quick-stats")
async def get_quick_stats(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get only quick statistics (lightweight endpoint for refreshing stats).
    Useful for pull-to-refresh functionality.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            (SELECT COUNT(*) FROM "Fields" WHERE farmer_id = :farmer_id) as total_fields,
            (SELECT COUNT(*) FROM "Alerts"
             WHERE farmer_id = :farmer_id
             AND created_at >= CURRENT_DATE - INTERVAL '7 days') as pending_alerts,
            (SELECT COUNT(*) FROM "Alerts"
             WHERE farmer_id = :farmer_id 
             AND DATE(created_at) = CURRENT_DATE) as alerts_today,
            (SELECT MAX(date) FROM "FertiliserUsage" 
             WHERE farmer_id = :farmer_id) as last_fert_date,
            (SELECT COUNT(*) FROM "WeatherData" w
             INNER JOIN "Fields" f ON w.field_id = f.id
             WHERE f.farmer_id = :farmer_id
             AND DATE(w.created_at) = CURRENT_DATE) as weather_today
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    row = result.fetchone()
    
    return {
        "total_fields": row[0],
        "pending_alerts": row[1],
        "alerts_today": row[2],
        "last_fertilizer_date": row[3].strftime("%Y-%m-%d") if row[3] else None,
        "weather_records_today": row[4]
    }


@router.get("/welcome")
async def get_welcome_message(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get only welcome message (minimal endpoint).
    Useful for header/toolbar display.
    """
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT first_name, other_name, location
        FROM "Users"
        WHERE id = :farmer_id
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    row = result.fetchone()
    
    first_name = row[0]
    full_name = f"{row[0]} {row[1]}" if row[1] else row[0]
    
    return {
        "user_id": farmer_id,
        "first_name": first_name,
        "full_name": full_name,
        "location": row[2],
        "greeting_message": get_greeting_message(first_name)
    }
