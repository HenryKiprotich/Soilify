from typing import List, Optional, Dict, Any
from pydantic import BaseModel
from datetime import datetime

# LLM Schemas
class LLMInfo(BaseModel):
    name: str
    description: Optional[str] = None

class LLMListResponse(BaseModel):
    llms: List[LLMInfo]

# Chat Message Schemas
class ChatMessage(BaseModel):
    id: Optional[int] = None
    message: str
    response: str
    model: Optional[str] = None
    timestamp: Optional[str] = None

class ChatResponse(BaseModel):
    response: str
    conversation_id: Optional[int] = None
    session_id: str
    model_used: str
    # Fields for animal-related information
    animal_id: Optional[int] = None
    animal_name: Optional[str] = None
    has_health_history: Optional[bool] = None

class ConversationHistoryResponse(BaseModel):
    messages: List[ChatMessage]
    session_id: str
    total: int

# Feedback Schema
class FeedbackResponse(BaseModel):
    status: str = "success"
    message: str = "Feedback submitted successfully"