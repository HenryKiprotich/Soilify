from pydantic import BaseModel
from typing import Optional, Dict, Any, List
from datetime import datetime

class ChatRequest(BaseModel):
    """Schema for chat request"""
    message: str
    session_id: Optional[str] = None
    context: Optional[str] = None  # Additional context (e.g., field_id, weather concerns)

class ChatResponse(BaseModel):
    """Schema for chat response"""
    response: str
    conversation_id: int
    session_id: str
    model: str = "gemini-1.5-flash"

class ConversationRead(BaseModel):
    """Schema for reading a conversation"""
    id: int
    farmer_id: Optional[int] = None
    session_id: str
    message: str
    response: str
    model: str
    metadata: Optional[Dict[str, Any]] = None
    created_at: datetime

    class Config:
        from_attributes = True

class ConversationHistory(BaseModel):
    """Schema for conversation history"""
    conversations: List[ConversationRead]
    total: int
    session_id: str

class NLToSQLRequest(BaseModel):
    """Schema for natural language to SQL conversion"""
    question: str
    session_id: Optional[str] = None

class NLToSQLResponse(BaseModel):
    """Schema for SQL query response"""
    question: str
    sql_query: str
    results: Optional[Dict[str, Any]] = None
    natural_response: str
    conversation_id: int