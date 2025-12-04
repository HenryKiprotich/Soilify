"""
Analytics Schemas for Soilify API
Pydantic models for analytics responses
"""

from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import date, datetime
from decimal import Decimal


class FertilizerUsageSummary(BaseModel):
    """Summary of fertilizer usage"""
    total_applications: int = Field(description="Total number of fertilizer applications")
    total_amount_kg: float = Field(description="Total fertilizer used in kg")
    average_amount_per_application: float = Field(description="Average amount per application")
    most_used_fertilizer: Optional[str] = Field(description="Most frequently used fertilizer type")
    fields_fertilized: int = Field(description="Number of fields with fertilizer applications")


class FertilizerByType(BaseModel):
    """Fertilizer usage grouped by type"""
    fertiliser_type: str
    total_amount_kg: float
    application_count: int
    percentage_of_total: float


class FertilizerByField(BaseModel):
    """Fertilizer usage grouped by field"""
    field_id: int
    field_name: str
    total_amount_kg: float
    application_count: int
    most_used_type: Optional[str]


class FertilizerByMonth(BaseModel):
    """Fertilizer usage grouped by month"""
    month: str
    year: int
    total_amount_kg: float
    application_count: int


class WeatherSummary(BaseModel):
    """Summary of weather data"""
    total_records: int = Field(description="Total weather records")
    average_temperature: Optional[float] = Field(description="Average temperature in °C")
    average_rainfall: Optional[float] = Field(description="Average rainfall in mm")
    average_soil_moisture: Optional[float] = Field(description="Average soil moisture %")
    max_temperature: Optional[float] = Field(description="Maximum temperature recorded")
    max_rainfall: Optional[float] = Field(description="Maximum rainfall recorded")
    min_temperature: Optional[float] = Field(description="Minimum temperature recorded")
    fields_monitored: int = Field(description="Number of fields with weather data")


class WeatherByField(BaseModel):
    """Weather data grouped by field"""
    field_id: int
    field_name: str
    average_temperature: Optional[float]
    average_rainfall: Optional[float]
    average_soil_moisture: Optional[float]
    record_count: int


class WeatherTrend(BaseModel):
    """Weather trends over time"""
    date: date
    average_temperature: Optional[float]
    average_rainfall: Optional[float]
    average_soil_moisture: Optional[float]


class FieldAnalytics(BaseModel):
    """Analytics for a specific field"""
    field_id: int
    field_name: str
    soil_type: Optional[str]
    crop_type: Optional[str]
    size_hectares: Optional[float]
    
    # Fertilizer stats
    total_fertilizer_kg: float
    fertilizer_applications: int
    last_fertilizer_date: Optional[date]
    
    # Weather stats
    weather_records: int
    avg_temperature: Optional[float]
    avg_rainfall: Optional[float]
    avg_soil_moisture: Optional[float]
    
    # Alerts
    total_alerts: int
    recent_alerts: int  # Last 7 days


class FarmOverview(BaseModel):
    """Complete farm overview analytics"""
    # Farm summary
    total_fields: int
    total_area_hectares: float
    
    # Fertilizer overview
    fertilizer_summary: FertilizerUsageSummary
    
    # Weather overview
    weather_summary: WeatherSummary
    
    # Alerts overview
    total_alerts: int
    alerts_this_week: int
    alerts_today: int
    
    # Recent activity
    last_fertilizer_application: Optional[date]
    last_weather_record: Optional[datetime]
    last_alert: Optional[datetime]


class DateRangeStats(BaseModel):
    """Statistics for a specific date range"""
    start_date: date
    end_date: date
    
    # Fertilizer stats
    fertilizer_applications: int
    total_fertilizer_kg: float
    
    # Weather stats
    weather_records: int
    avg_temperature: Optional[float]
    avg_rainfall: Optional[float]
    
    # Alerts
    alerts_count: int


class TopFields(BaseModel):
    """Top fields by various metrics"""
    most_fertilized: List[FertilizerByField]
    largest_fields: List[Dict[str, Any]]
    most_alerts: List[Dict[str, Any]]


class ComparisonAnalytics(BaseModel):
    """Compare current period vs previous period"""
    current_period: DateRangeStats
    previous_period: DateRangeStats
    
    # Growth percentages
    fertilizer_growth_percent: Optional[float]
    weather_records_growth_percent: Optional[float]
    alerts_growth_percent: Optional[float]


class FertilizerEfficiencyMetrics(BaseModel):
    """Fertilizer efficiency and usage metrics"""
    total_area_hectares: float
    total_fertilizer_kg: float
    fertilizer_per_hectare: float
    fields_analyzed: int
    recommendation: str = Field(description="AI recommendation based on usage patterns")


class CropTypeAnalytics(BaseModel):
    """Analytics grouped by crop type"""
    crop_type: str
    fields_count: int
    total_area_hectares: float
    total_fertilizer_kg: float
    avg_fertilizer_per_hectare: float
    avg_temperature: Optional[float]
    avg_rainfall: Optional[float]


class SoilTypeAnalytics(BaseModel):
    """Analytics grouped by soil type"""
    soil_type: str
    fields_count: int
    total_area_hectares: float
    total_fertilizer_kg: float
    avg_fertilizer_per_hectare: float
    most_common_crop: Optional[str]
