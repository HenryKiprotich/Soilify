from sqlalchemy import Column, Integer, String, ForeignKey, DateTime, Text
from sqlalchemy.sql import func
from app.database.database import Base

class Alert(Base):
    __tablename__ = "Alerts"

    id = Column("id", Integer, primary_key=True, index=True)
    farmer_id = Column("farmer_id", Integer, ForeignKey("users.id"), nullable=False)
    field_id = Column("field_id", Integer, ForeignKey("fields.id"), nullable=True)
    message = Column("message", Text, nullable=False)
    created_at = Column("created_at", DateTime(timezone=True), server_default=func.now())
