from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.sql import func
from app.database.database import Base

class AIConversation(Base):
    __tablename__ = "AIConversations"
    
    id = Column("id", Integer, primary_key=True, index=True)
    farmer_id = Column("farmer_id", Integer, ForeignKey("users.id"), nullable=True)
    session_id = Column("session_id", String(100), index=True)
    message = Column("message", Text, nullable=False)
    response = Column("response", Text, nullable=False)
    model = Column("model", String(50), default="gemini-1.5-flash")
    metadata = Column("metadata", JSONB)
    created_at = Column("created_at", DateTime(timezone=True), server_default=func.now())