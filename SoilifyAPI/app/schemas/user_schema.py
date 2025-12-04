from pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime

class UserCreate(BaseModel):
    first_name: str
    other_name: Optional[str] = None
    phone_number: str
    email_adress: EmailStr
    password: str
    location: Optional[str] = None

class UserLogin(BaseModel):
    email_adress: Optional[EmailStr] = None
    phone_number: Optional[str] = None
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    phone_number: Optional[str] = None

class UserEmail(BaseModel):
    email_adress: EmailStr

class ResetPasswordRequest(BaseModel):
    token: str
    new_password: str

class UserRead(BaseModel):
    id: int
    first_name: str
    other_name: Optional[str] = None
    phone_number: str
    email_adress: EmailStr
    location: Optional[str] = None
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True