from sqlalchemy import Column, Integer, Float, ForeignKey, DateTime
from sqlalchemy.sql import func
from app.database.database import Base

class WeatherData(Base):
    __tablename__ = "weatherdata"

    id = Column("id", Integer, primary_key=True, index=True)
    field_id = Column("field_id", Integer, ForeignKey("fields.id"), nullable=False)
    temperature = Column("temperature", Float)
    rainfall = Column("rainfall", Float)
    soil_moisture = Column("soil_moisture", Float)
    created_at = Column("created_at", DateTime(timezone=True), server_default=func.now())
