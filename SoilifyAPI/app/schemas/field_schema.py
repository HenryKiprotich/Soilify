from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class FieldCreate(BaseModel):
    """Schema for creating a new field (farmer_id extracted from token)"""
    field_name: str
    soil_type: Optional[str] = None
    crop_type: Optional[str] = None
    size_hectares: Optional[float] = None

class FieldUpdate(BaseModel):
    """Schema for updating an existing field"""
    field_name: Optional[str] = None
    soil_type: Optional[str] = None
    crop_type: Optional[str] = None
    size_hectares: Optional[float] = None

class FieldRead(BaseModel):
    """Schema for reading field data"""
    id: int
    farmer_id: int
    field_name: str
    soil_type: Optional[str] = None
    crop_type: Optional[str] = None
    size_hectares: Optional[float] = None
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True
