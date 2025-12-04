"""
Home/Dashboard Schemas for Soilify API
Pydantic models for home dashboard responses
"""

from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import datetime


class UserWelcome(BaseModel):
    """Welcome message with user details"""
    user_id: int
    first_name: str
    full_name: str
    location: Optional[str]
    greeting_message: str = Field(description="Personalized greeting message")


class RecentAlert(BaseModel):
    """Recent alert for dashboard"""
    id: int
    message: str
    field_id: Optional[int]
    field_name: Optional[str]
    created_at: datetime
    time_ago: str = Field(description="Human-readable time (e.g., '2 hours ago')")


class QuickStats(BaseModel):
    """Quick statistics for dashboard"""
    total_fields: int = Field(description="Total number of fields")
    pending_alerts: int = Field(description="Alerts from last 7 days")
    last_fertilizer_date: Optional[str] = Field(description="Last fertilizer application date")
    weather_records_today: int = Field(description="Weather records added today")


class DashboardResponse(BaseModel):
    """Complete dashboard/home response"""
    welcome: UserWelcome
    quick_stats: QuickStats
    recent_alerts: List[RecentAlert]
    has_fields: bool = Field(description="Whether user has any fields created")
    has_activity: bool = Field(description="Whether user has any recorded activity")


class FieldSummaryCard(BaseModel):
    """Summary card for a field on dashboard"""
    field_id: int
    field_name: str
    crop_type: Optional[str]
    size_hectares: Optional[float]
    last_activity: Optional[datetime]
    alert_count: int = Field(description="Number of alerts for this field")


class DashboardWithFields(BaseModel):
    """Dashboard with field summary cards"""
    welcome: UserWelcome
    quick_stats: QuickStats
    recent_alerts: List[RecentAlert]
    field_cards: List[FieldSummaryCard]
