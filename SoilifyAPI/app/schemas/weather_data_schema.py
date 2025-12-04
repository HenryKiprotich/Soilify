from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class WeatherDataCreate(BaseModel):
    """Schema for creating weather data (field must belong to authenticated farmer)"""
    field_id: int  # Selected from dropdown of farmer's fields
    temperature: Optional[float] = None  # Temperature in Celsius
    rainfall: Optional[float] = None  # Rainfall in mm
    soil_moisture: Optional[float] = None  # Soil moisture percentage

class WeatherDataUpdate(BaseModel):
    """Schema for updating weather data"""
    field_id: Optional[int] = None
    temperature: Optional[float] = None
    rainfall: Optional[float] = None
    soil_moisture: Optional[float] = None

class WeatherDataRead(BaseModel):
    """Schema for reading weather data with field details"""
    id: int
    field_id: int
    field_name: Optional[str] = None  # Joined from fields table
    temperature: Optional[float] = None
    rainfall: Optional[float] = None
    soil_moisture: Optional[float] = None
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class WeatherDataStats(BaseModel):
    """Schema for weather statistics by field"""
    field_id: int
    field_name: str
    avg_temperature: Optional[float] = None
    avg_rainfall: Optional[float] = None
    avg_soil_moisture: Optional[float] = None
    total_readings: int
    latest_reading_date: Optional[datetime] = None
