from pydantic import BaseModel
from typing import Optional
from datetime import datetime, date

class FertiliserUsageCreate(BaseModel):
    """Schema for creating a new fertiliser usage record (farmer_id extracted from token)"""
    field_id: int  # Selected from dropdown of farmer's fields
    fertiliser_type: str
    amount_kg: float
    weather: Optional[str] = None
    notes: Optional[str] = None
    date: date  # Date of fertiliser application

class FertiliserUsageUpdate(BaseModel):
    """Schema for updating an existing fertiliser usage record"""
    field_id: Optional[int] = None
    fertiliser_type: Optional[str] = None
    amount_kg: Optional[float] = None
    weather: Optional[str] = None
    notes: Optional[str] = None
    date: Optional[date] = None

class FertiliserUsageRead(BaseModel):
    """Schema for reading fertiliser usage data with field details"""
    id: int
    farmer_id: int
    field_id: int
    field_name: Optional[str] = None  # Joined from fields table
    fertiliser_type: str
    amount_kg: float
    weather: Optional[str] = None
    notes: Optional[str] = None
    date: date
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class FieldOption(BaseModel):
    """Schema for field dropdown options"""
    id: int
    field_name: str
    soil_type: Optional[str] = None
    crop_type: Optional[str] = None
    size_hectares: Optional[float] = None
