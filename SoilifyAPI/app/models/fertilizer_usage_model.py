from sqlalchemy import Column, Integer, String, Float, ForeignKey, DateTime, Text, Date
from sqlalchemy.sql import func
from app.database.database import Base

class FertiliserUsage(Base):
    __tablename__ = "FertiliserUsage"

    id = Column("id", Integer, primary_key=True, index=True)
    farmer_id = Column("farmer_id", Integer, ForeignKey("users.id"), nullable=False)
    field_id = Column("field_id", Integer, ForeignKey("fields.id"), nullable=False)
    fertiliser_type = Column("fertiliser_type", String(100))
    amount_kg = Column("amount_kg", Float)
    weather = Column("weather", String(100))
    notes = Column("notes", Text)
    date = Column("date", Date, nullable=False)
    created_at = Column("created_at", DateTime(timezone=True), server_default=func.now())
