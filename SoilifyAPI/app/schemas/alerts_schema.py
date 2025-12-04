from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class AlertCreate(BaseModel):
    """Schema for creating an alert (farmer_id extracted from token)"""
    field_id: Optional[int] = None  # Optional - can be general alert or field-specific
    message: str

class AlertUpdate(BaseModel):
    """Schema for updating an alert"""
    field_id: Optional[int] = None
    message: Optional[str] = None

class AlertRead(BaseModel):
    """Schema for reading alert data with field and farmer details"""
    id: int
    farmer_id: int
    farmer_name: Optional[str] = None  # Joined from users table
    field_id: Optional[int] = None
    field_name: Optional[str] = None  # Joined from fields table
    message: str
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class AlertSummary(BaseModel):
    """Schema for alert statistics"""
    total_alerts: int
    alerts_today: int
    alerts_this_week: int
    alerts_by_field: dict  # field_name: count
