from sqlalchemy import Column, Integer, String, Float, ForeignKey, DateTime
from sqlalchemy.sql import func
from app.database.database import Base

class Field(Base):
    __tablename__ = "Fields"

    id = Column("id", Integer, primary_key=True, index=True)
    farmer_id = Column("farmer_id", Integer, ForeignKey("users.id"), nullable=False)
    field_name = Column("field_name", String(255), nullable=False)
    soil_type = Column("soil_type", String(100))
    crop_type = Column("crop_type", String(100))
    size_hectares = Column("size_hectares", Float)
    created_at = Column("created_at", DateTime(timezone=True), server_default=func.now())
