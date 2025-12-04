"""
Analytics Endpoints for Soilify API
Comprehensive analytics for fertilizer usage, weather data, and farm overview
"""

from fastapi import APIRouter, HTTPException, Depends, status, Query
from typing import List, Optional
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from datetime import datetime, date, timedelta
from decimal import Decimal

from app.schemas.analytics_schema import (
    FarmOverview,
    FertilizerUsageSummary,
    FertilizerByType,
    FertilizerByField,
    FertilizerByMonth,
    WeatherSummary,
    WeatherByField,
    WeatherTrend,
    FieldAnalytics,
    DateRangeStats,
    TopFields,
    ComparisonAnalytics,
    FertilizerEfficiencyMetrics,
    CropTypeAnalytics,
    SoilTypeAnalytics
)
from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie

router = APIRouter(prefix="", tags=["analytics"])


@router.get("/overview", response_model=FarmOverview)
async def get_farm_overview(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """
    Get complete farm overview with all analytics.
    Includes fertilizer usage, weather data, alerts, and recent activity.
    """
    farmer_id = current_user["user_id"]
    
    # Get total fields and area
    fields_query = text("""
        SELECT 
            COUNT(*) as total_fields,
            COALESCE(SUM(size_hectares), 0) as total_area
        FROM fields
        WHERE farmer_id = :farmer_id
    """)
    fields_result = await db.execute(fields_query, {"farmer_id": farmer_id})
    fields_row = fields_result.fetchone()
    
    # Get fertilizer summary
    fertilizer_query = text("""
        SELECT 
            COUNT(*) as total_applications,
            COALESCE(SUM(amount_kg), 0) as total_amount,
            COALESCE(AVG(amount_kg), 0) as avg_amount,
            COUNT(DISTINCT field_id) as fields_fertilized
        FROM fertiliserusage
        WHERE farmer_id = :farmer_id
    """)
    fert_result = await db.execute(fertilizer_query, {"farmer_id": farmer_id})
    fert_row = fert_result.fetchone()
    
    # Get most used fertilizer
    most_used_query = text("""
        SELECT fertiliser_type, COUNT(*) as count
        FROM fertiliserusage
        WHERE farmer_id = :farmer_id AND fertiliser_type IS NOT NULL
        GROUP BY fertiliser_type
        ORDER BY count DESC
        LIMIT 1
    """)
    most_used_result = await db.execute(most_used_query, {"farmer_id": farmer_id})
    most_used_row = most_used_result.fetchone()
    
    # Get weather summary
    weather_query = text("""
        SELECT 
            COUNT(*) as total_records,
            AVG(w.temperature) as avg_temp,
            AVG(w.rainfall) as avg_rain,
            AVG(w.soil_moisture) as avg_moisture,
            MAX(w.temperature) as max_temp,
            MAX(w.rainfall) as max_rain,
            MIN(w.temperature) as min_temp,
            COUNT(DISTINCT w.field_id) as fields_monitored
        FROM weatherdata w
        INNER JOIN fields f ON w.field_id = f.id
        WHERE f.farmer_id = :farmer_id
    """)
    weather_result = await db.execute(weather_query, {"farmer_id": farmer_id})
    weather_row = weather_result.fetchone()
    
    # Get alerts summary
    alerts_query = text("""
        SELECT 
            COUNT(*) as total_alerts,
            COUNT(CASE WHEN created_at >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as alerts_week,
            COUNT(CASE WHEN DATE(created_at) = CURRENT_DATE THEN 1 END) as alerts_today
        FROM alerts
        WHERE farmer_id = :farmer_id
    """)
    alerts_result = await db.execute(alerts_query, {"farmer_id": farmer_id})
    alerts_row = alerts_result.fetchone()
    
    # Get recent activity dates
    activity_query = text("""
        SELECT 
            (SELECT MAX(date) FROM fertiliserusage WHERE farmer_id = :farmer_id) as last_fert,
            (SELECT MAX(created_at) FROM weatherdata w 
             INNER JOIN fields f ON w.field_id = f.id 
             WHERE f.farmer_id = :farmer_id) as last_weather,
            (SELECT MAX(created_at) FROM alerts WHERE farmer_id = :farmer_id) as last_alert
    """)
    activity_result = await db.execute(activity_query, {"farmer_id": farmer_id})
    activity_row = activity_result.fetchone()
    
    return {
        "total_fields": fields_row[0],
        "total_area_hectares": float(fields_row[1]) if fields_row[1] else 0.0,
        "fertilizer_summary": {
            "total_applications": fert_row[0],
            "total_amount_kg": float(fert_row[1]) if fert_row[1] else 0.0,
            "average_amount_per_application": float(fert_row[2]) if fert_row[2] else 0.0,
            "most_used_fertilizer": most_used_row[0] if most_used_row else None,
            "fields_fertilized": fert_row[3]
        },
        "weather_summary": {
            "total_records": weather_row[0],
            "average_temperature": float(weather_row[1]) if weather_row[1] else None,
            "average_rainfall": float(weather_row[2]) if weather_row[2] else None,
            "average_soil_moisture": float(weather_row[3]) if weather_row[3] else None,
            "max_temperature": float(weather_row[4]) if weather_row[4] else None,
            "max_rainfall": float(weather_row[5]) if weather_row[5] else None,
            "min_temperature": float(weather_row[6]) if weather_row[6] else None,
            "fields_monitored": weather_row[7]
        },
        "total_alerts": alerts_row[0],
        "alerts_this_week": alerts_row[1],
        "alerts_today": alerts_row[2],
        "last_fertilizer_application": activity_row[0],
        "last_weather_record": activity_row[1],
        "last_alert": activity_row[2]
    }


@router.get("/fertilizer/by-type", response_model=List[FertilizerByType])
async def get_fertilizer_by_type(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get fertilizer usage grouped by fertilizer type"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        WITH totals AS (
            SELECT SUM(amount_kg) as grand_total
            FROM fertiliserusage
            WHERE farmer_id = :farmer_id
        )
        SELECT 
            fu.fertiliser_type,
            SUM(fu.amount_kg) as total_amount,
            COUNT(*) as application_count,
            (SUM(fu.amount_kg) / NULLIF(t.grand_total, 0) * 100) as percentage
        FROM fertiliserusage fu
        CROSS JOIN totals t
        WHERE fu.farmer_id = :farmer_id AND fu.fertiliser_type IS NOT NULL
        GROUP BY fu.fertiliser_type, t.grand_total
        ORDER BY total_amount DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    return [
        {
            "fertiliser_type": row[0],
            "total_amount_kg": float(row[1]) if row[1] else 0.0,
            "application_count": row[2],
            "percentage_of_total": float(row[3]) if row[3] else 0.0
        }
        for row in rows
    ]


@router.get("/fertilizer/by-field", response_model=List[FertilizerByField])
async def get_fertilizer_by_field(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get fertilizer usage grouped by field"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            f.id,
            f.field_name,
            COALESCE(SUM(fu.amount_kg), 0) as total_amount,
            COUNT(fu.id) as application_count,
            (
                SELECT fertiliser_type
                FROM fertiliserusage
                WHERE field_id = f.id AND fertiliser_type IS NOT NULL
                GROUP BY fertiliser_type
                ORDER BY COUNT(*) DESC
                LIMIT 1
            ) as most_used_type
        FROM fields f
        LEFT JOIN fertiliserusage fu ON f.id = fu.field_id
        WHERE f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name
        ORDER BY total_amount DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    return [
        {
            "field_id": row[0],
            "field_name": row[1],
            "total_amount_kg": float(row[2]) if row[2] else 0.0,
            "application_count": row[3],
            "most_used_type": row[4]
        }
        for row in rows
    ]


@router.get("/fertilizer/by-month", response_model=List[FertilizerByMonth])
async def get_fertilizer_by_month(
    months: int = Query(12, ge=1, le=24, description="Number of months to include"),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get fertilizer usage grouped by month for the last N months"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            TO_CHAR(date, 'Month') as month,
            EXTRACT(YEAR FROM date) as year,
            SUM(amount_kg) as total_amount,
            COUNT(*) as application_count
        FROM fertiliserusage
        WHERE farmer_id = :farmer_id 
        AND date >= CURRENT_DATE - INTERVAL ':months months'
        GROUP BY TO_CHAR(date, 'Month'), EXTRACT(YEAR FROM date), EXTRACT(MONTH FROM date)
        ORDER BY EXTRACT(YEAR FROM date) DESC, EXTRACT(MONTH FROM date) DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id, "months": months})
    rows = result.fetchall()
    
    return [
        {
            "month": row[0].strip(),
            "year": int(row[1]),
            "total_amount_kg": float(row[2]) if row[2] else 0.0,
            "application_count": row[3]
        }
        for row in rows
    ]


@router.get("/weather/by-field", response_model=List[WeatherByField])
async def get_weather_by_field(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get weather data grouped by field"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            f.id,
            f.field_name,
            AVG(w.temperature) as avg_temp,
            AVG(w.rainfall) as avg_rain,
            AVG(w.soil_moisture) as avg_moisture,
            COUNT(w.id) as record_count
        FROM fields f
        LEFT JOIN weatherdata w ON f.id = w.field_id
        WHERE f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name
        ORDER BY record_count DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    return [
        {
            "field_id": row[0],
            "field_name": row[1],
            "average_temperature": float(row[2]) if row[2] else None,
            "average_rainfall": float(row[3]) if row[3] else None,
            "average_soil_moisture": float(row[4]) if row[4] else None,
            "record_count": row[5]
        }
        for row in rows
    ]


@router.get("/weather/trends", response_model=List[WeatherTrend])
async def get_weather_trends(
    days: int = Query(30, ge=7, le=90, description="Number of days to include"),
    field_id: Optional[int] = Query(None, description="Filter by specific field"),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get weather trends over time (daily averages)"""
    farmer_id = current_user["user_id"]
    
    if field_id:
        # Verify field belongs to farmer
        check_query = text("""
            SELECT id FROM fields WHERE id = :field_id AND farmer_id = :farmer_id
        """)
        check_result = await db.execute(check_query, {"field_id": field_id, "farmer_id": farmer_id})
        if not check_result.fetchone():
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Field not found or you don't have permission"
            )
        
        query = text("""
            SELECT 
                DATE(w.created_at) as date,
                AVG(w.temperature) as avg_temp,
                AVG(w.rainfall) as avg_rain,
                AVG(w.soil_moisture) as avg_moisture
            FROM weatherdata w
            WHERE w.field_id = :field_id
            AND w.created_at >= CURRENT_DATE - INTERVAL ':days days'
            GROUP BY DATE(w.created_at)
            ORDER BY date DESC
        """)
        result = await db.execute(query, {"field_id": field_id, "days": days})
    else:
        query = text("""
            SELECT 
                DATE(w.created_at) as date,
                AVG(w.temperature) as avg_temp,
                AVG(w.rainfall) as avg_rain,
                AVG(w.soil_moisture) as avg_moisture
            FROM weatherdata w
            INNER JOIN fields f ON w.field_id = f.id
            WHERE f.farmer_id = :farmer_id
            AND w.created_at >= CURRENT_DATE - INTERVAL ':days days'
            GROUP BY DATE(w.created_at)
            ORDER BY date DESC
        """)
        result = await db.execute(query, {"farmer_id": farmer_id, "days": days})
    
    rows = result.fetchall()
    
    return [
        {
            "date": row[0],
            "average_temperature": float(row[1]) if row[1] else None,
            "average_rainfall": float(row[2]) if row[2] else None,
            "average_soil_moisture": float(row[3]) if row[3] else None
        }
        for row in rows
    ]


@router.get("/field/{field_id}", response_model=FieldAnalytics)
async def get_field_analytics(
    field_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get comprehensive analytics for a specific field"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            f.id,
            f.field_name,
            f.soil_type,
            f.crop_type,
            f.size_hectares,
            COALESCE(SUM(fu.amount_kg), 0) as total_fertilizer,
            COUNT(fu.id) as fertilizer_count,
            MAX(fu.date) as last_fert_date,
            COUNT(w.id) as weather_count,
            AVG(w.temperature) as avg_temp,
            AVG(w.rainfall) as avg_rain,
            AVG(w.soil_moisture) as avg_moisture,
            COUNT(a.id) as total_alerts,
            COUNT(CASE WHEN a.created_at >= CURRENT_DATE - INTERVAL '7 days' THEN 1 END) as recent_alerts
        FROM fields f
        LEFT JOIN fertiliserusage fu ON f.id = fu.field_id
        LEFT JOIN weatherdata w ON f.id = w.field_id
        LEFT JOIN alerts a ON f.id = a.field_id
        WHERE f.id = :field_id AND f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name, f.soil_type, f.crop_type, f.size_hectares
    """)
    
    result = await db.execute(query, {"field_id": field_id, "farmer_id": farmer_id})
    row = result.fetchone()
    
    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Field not found or you don't have permission"
        )
    
    return {
        "field_id": row[0],
        "field_name": row[1],
        "soil_type": row[2],
        "crop_type": row[3],
        "size_hectares": float(row[4]) if row[4] else None,
        "total_fertilizer_kg": float(row[5]) if row[5] else 0.0,
        "fertilizer_applications": row[6],
        "last_fertilizer_date": row[7],
        "weather_records": row[8],
        "avg_temperature": float(row[9]) if row[9] else None,
        "avg_rainfall": float(row[10]) if row[10] else None,
        "avg_soil_moisture": float(row[11]) if row[11] else None,
        "total_alerts": row[12],
        "recent_alerts": row[13]
    }


@router.get("/date-range", response_model=DateRangeStats)
async def get_date_range_stats(
    start_date: date = Query(..., description="Start date (YYYY-MM-DD)"),
    end_date: date = Query(..., description="End date (YYYY-MM-DD)"),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get statistics for a specific date range"""
    farmer_id = current_user["user_id"]
    
    if start_date > end_date:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Start date must be before end date"
        )
    
    query = text("""
        SELECT 
            COUNT(fu.id) as fert_count,
            COALESCE(SUM(fu.amount_kg), 0) as fert_total,
            COUNT(w.id) as weather_count,
            AVG(w.temperature) as avg_temp,
            AVG(w.rainfall) as avg_rain,
            COUNT(a.id) as alerts_count
        FROM (SELECT 1) dummy
        LEFT JOIN fertiliserusage fu ON fu.farmer_id = :farmer_id 
            AND fu.date BETWEEN :start_date AND :end_date
        LEFT JOIN weatherdata w ON w.field_id IN (
            SELECT id FROM fields WHERE farmer_id = :farmer_id
        ) AND DATE(w.created_at) BETWEEN :start_date AND :end_date
        LEFT JOIN alerts a ON a.farmer_id = :farmer_id 
            AND DATE(a.created_at) BETWEEN :start_date AND :end_date
    """)
    
    result = await db.execute(query, {
        "farmer_id": farmer_id,
        "start_date": start_date,
        "end_date": end_date
    })
    row = result.fetchone()
    
    return {
        "start_date": start_date,
        "end_date": end_date,
        "fertilizer_applications": row[0],
        "total_fertilizer_kg": float(row[1]) if row[1] else 0.0,
        "weather_records": row[2],
        "avg_temperature": float(row[3]) if row[3] else None,
        "avg_rainfall": float(row[4]) if row[4] else None,
        "alerts_count": row[5]
    }


@router.get("/top-fields", response_model=TopFields)
async def get_top_fields(
    limit: int = Query(5, ge=1, le=10, description="Number of top fields to return"),
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get top fields by various metrics"""
    farmer_id = current_user["user_id"]
    
    # Most fertilized fields
    fertilized_query = text("""
        SELECT 
            f.id,
            f.field_name,
            SUM(fu.amount_kg) as total_amount,
            COUNT(fu.id) as application_count,
            (
                SELECT fertiliser_type
                FROM fertiliserusage
                WHERE field_id = f.id AND fertiliser_type IS NOT NULL
                GROUP BY fertiliser_type
                ORDER BY COUNT(*) DESC
                LIMIT 1
            ) as most_used_type
        FROM fields f
        INNER JOIN fertiliserusage fu ON f.id = fu.field_id
        WHERE f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name
        ORDER BY total_amount DESC
        LIMIT :limit
    """)
    fert_result = await db.execute(fertilized_query, {"farmer_id": farmer_id, "limit": limit})
    fert_rows = fert_result.fetchall()
    
    # Largest fields
    largest_query = text("""
        SELECT id, field_name, size_hectares, crop_type
        FROM fields
        WHERE farmer_id = :farmer_id
        ORDER BY size_hectares DESC NULLS LAST
        LIMIT :limit
    """)
    largest_result = await db.execute(largest_query, {"farmer_id": farmer_id, "limit": limit})
    largest_rows = largest_result.fetchall()
    
    # Most alerts
    alerts_query = text("""
        SELECT 
            f.id,
            f.field_name,
            COUNT(a.id) as alert_count
        FROM fields f
        INNER JOIN alerts a ON f.id = a.field_id
        WHERE f.farmer_id = :farmer_id
        GROUP BY f.id, f.field_name
        ORDER BY alert_count DESC
        LIMIT :limit
    """)
    alerts_result = await db.execute(alerts_query, {"farmer_id": farmer_id, "limit": limit})
    alerts_rows = alerts_result.fetchall()
    
    return {
        "most_fertilized": [
            {
                "field_id": row[0],
                "field_name": row[1],
                "total_amount_kg": float(row[2]) if row[2] else 0.0,
                "application_count": row[3],
                "most_used_type": row[4]
            }
            for row in fert_rows
        ],
        "largest_fields": [
            {
                "field_id": row[0],
                "field_name": row[1],
                "size_hectares": float(row[2]) if row[2] else 0.0,
                "crop_type": row[3]
            }
            for row in largest_rows
        ],
        "most_alerts": [
            {
                "field_id": row[0],
                "field_name": row[1],
                "alert_count": row[2]
            }
            for row in alerts_rows
        ]
    }


@router.get("/efficiency-metrics", response_model=FertilizerEfficiencyMetrics)
async def get_efficiency_metrics(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Calculate fertilizer efficiency metrics"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            COALESCE(SUM(f.size_hectares), 0) as total_area,
            COALESCE(SUM(fu.amount_kg), 0) as total_fertilizer,
            COUNT(DISTINCT f.id) as fields_count
        FROM fields f
        LEFT JOIN fertiliserusage fu ON f.id = fu.field_id
        WHERE f.farmer_id = :farmer_id
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    row = result.fetchone()
    
    total_area = float(row[0]) if row[0] else 0.0
    total_fertilizer = float(row[1]) if row[1] else 0.0
    fertilizer_per_hectare = (total_fertilizer / total_area) if total_area > 0 else 0.0
    
    # Generate recommendation
    if fertilizer_per_hectare == 0:
        recommendation = "No fertilizer usage recorded yet. Start tracking to get recommendations."
    elif fertilizer_per_hectare < 50:
        recommendation = "Low fertilizer usage. Consider increasing application for better yields."
    elif fertilizer_per_hectare < 150:
        recommendation = "Good fertilizer usage. Monitor crop response and adjust as needed."
    elif fertilizer_per_hectare < 250:
        recommendation = "Moderate to high usage. Ensure proper timing and soil testing."
    else:
        recommendation = "High fertilizer usage detected. Consider soil testing to optimize usage."
    
    return {
        "total_area_hectares": total_area,
        "total_fertilizer_kg": total_fertilizer,
        "fertilizer_per_hectare": round(fertilizer_per_hectare, 2),
        "fields_analyzed": row[2],
        "recommendation": recommendation
    }


@router.get("/by-crop-type", response_model=List[CropTypeAnalytics])
async def get_analytics_by_crop_type(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get analytics grouped by crop type"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            f.crop_type,
            COUNT(f.id) as fields_count,
            COALESCE(SUM(f.size_hectares), 0) as total_area,
            COALESCE(SUM(fu.amount_kg), 0) as total_fertilizer,
            CASE 
                WHEN SUM(f.size_hectares) > 0 
                THEN SUM(fu.amount_kg) / SUM(f.size_hectares)
                ELSE 0 
            END as fertilizer_per_hectare,
            AVG(w.temperature) as avg_temp,
            AVG(w.rainfall) as avg_rain
        FROM fields f
        LEFT JOIN fertiliserusage fu ON f.id = fu.field_id
        LEFT JOIN weatherdata w ON f.id = w.field_id
        WHERE f.farmer_id = :farmer_id AND f.crop_type IS NOT NULL
        GROUP BY f.crop_type
        ORDER BY fields_count DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    return [
        {
            "crop_type": row[0],
            "fields_count": row[1],
            "total_area_hectares": float(row[2]) if row[2] else 0.0,
            "total_fertilizer_kg": float(row[3]) if row[3] else 0.0,
            "avg_fertilizer_per_hectare": float(row[4]) if row[4] else 0.0,
            "avg_temperature": float(row[5]) if row[5] else None,
            "avg_rainfall": float(row[6]) if row[6] else None
        }
        for row in rows
    ]


@router.get("/by-soil-type", response_model=List[SoilTypeAnalytics])
async def get_analytics_by_soil_type(
    db: AsyncSession = Depends(get_db),
    current_user: dict = Depends(get_current_user_from_cookie)
):
    """Get analytics grouped by soil type"""
    farmer_id = current_user["user_id"]
    
    query = text("""
        SELECT 
            f.soil_type,
            COUNT(f.id) as fields_count,
            COALESCE(SUM(f.size_hectares), 0) as total_area,
            COALESCE(SUM(fu.amount_kg), 0) as total_fertilizer,
            CASE 
                WHEN SUM(f.size_hectares) > 0 
                THEN SUM(fu.amount_kg) / SUM(f.size_hectares)
                ELSE 0 
            END as fertilizer_per_hectare,
            (
                SELECT crop_type
                FROM fields
                WHERE farmer_id = :farmer_id AND soil_type = f.soil_type AND crop_type IS NOT NULL
                GROUP BY crop_type
                ORDER BY COUNT(*) DESC
                LIMIT 1
            ) as most_common_crop
        FROM fields f
        LEFT JOIN fertiliserusage fu ON f.id = fu.field_id
        WHERE f.farmer_id = :farmer_id AND f.soil_type IS NOT NULL
        GROUP BY f.soil_type
        ORDER BY fields_count DESC
    """)
    
    result = await db.execute(query, {"farmer_id": farmer_id})
    rows = result.fetchall()
    
    return [
        {
            "soil_type": row[0],
            "fields_count": row[1],
            "total_area_hectares": float(row[2]) if row[2] else 0.0,
            "total_fertilizer_kg": float(row[3]) if row[3] else 0.0,
            "avg_fertilizer_per_hectare": float(row[4]) if row[4] else 0.0,
            "most_common_crop": row[5]
        }
        for row in rows
    ]
