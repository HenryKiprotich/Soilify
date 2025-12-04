from sqlalchemy import Column, Integer, String, DateTime, Text
from sqlalchemy.sql import func
from app.database.database import Base

class User(Base):
    __tablename__ = "Users"

    id = Column("id", Integer, primary_key=True, index=True)
    first_name = Column("first_name", String(255))
    other_name = Column("other_name", String(255))
    phone_number = Column("phone_number", String(20), unique=True)
    email_adress = Column("email_adress", String(255), unique=True, nullable=False)
    password_hash = Column("password_hash", Text, nullable=False)
    location = Column("location", String(255))
    created_at = Column("created_at", DateTime(timezone=True), server_default=func.now())