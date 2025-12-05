"""
AI Chat Endpoints for Farming Advice
Uses Google Gemini only (no LangChain)
"""

import logging
import uuid
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, status

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text

from app.database.database import get_db
from app.services.auth_service import get_current_user_from_cookie
from app.schemas.ai_schema import (
    ChatRequest,
    ChatResponse,
    NLToSQLRequest,
    NLToSQLResponse,
    ConversationHistory
)
from app.AI.llm import LLMManager

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/ai", tags=["AI Chat"])

# Initialize LLM Manager
llm_manager = LLMManager()


@router.post("/chat", response_model=ChatResponse)
async def chat_with_ai(
    request: ChatRequest,
    current_user: dict = Depends(get_current_user_from_cookie),
    db: AsyncSession = Depends(get_db)
):
    """
    General farming advice chat endpoint
    
    - **message**: The farmer's question or message
    - **session_id**: Optional session ID for conversation continuity
    - **context**: Optional additional context for the conversation
    """
    try:
        farmer_id = current_user["user_id"]
        logger.info(f"Chat request received from farmer_id: {farmer_id}")
        logger.info(f"Message: {request.message}")
        
        # Generate session_id if not provided
        session_id = request.session_id or str(uuid.uuid4())

        # Call the LLM manager
        result = await llm_manager.chat_about_farming(
            db=db,
            farmer_id=farmer_id,
            message=request.message,
            session_id=session_id,
            context=request.context
        )

        return ChatResponse(
            response=result["response"],
            conversation_id=result["conversation_id"],
            session_id=result["session_id"],
            model=result["model"]
        )

    except Exception as e:
        logger.exception(f"Error in chat endpoint: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to process chat request: {str(e)}"
        )


@router.post("/ask-sql", response_model=NLToSQLResponse)
async def ask_with_sql(
    request: NLToSQLRequest,
    current_user: dict = Depends(get_current_user_from_cookie),
    db: AsyncSession = Depends(get_db)
):
    """
    Natural language to SQL query endpoint
    
    Converts a natural language question to a SQL query,
    executes it safely, and returns the results in natural language.
    
    - **question**: The farmer's question about their data
    - **session_id**: Optional session ID for tracking
    """
    try:
        farmer_id = current_user["user_id"]
        logger.info(f"SQL query request from farmer_id: {farmer_id}")
        
        # Generate session_id if not provided
        session_id = request.session_id or str(uuid.uuid4())

        # Call the LLM manager
        result = await llm_manager.natural_language_to_sql(
            db=db,
            farmer_id=farmer_id,
            question=request.question,
            session_id=session_id
        )

        return NLToSQLResponse(
            question=result["question"],
            sql_query=result["sql_query"],
            results=result["results"],
            natural_response=result["natural_response"],
            conversation_id=result["conversation_id"]
        )

    except ValueError as e:
        # SQL validation error
        logger.warning(f"SQL validation error: {e}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid SQL query generated: {str(e)}"
        )
    except Exception as e:
        logger.error(f"Error in ask-sql endpoint: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to process SQL question: {str(e)}"
        )


@router.post("/analyze-field/{field_id}")
async def analyze_field(
    field_id: int,
    session_id: Optional[str] = None,
    current_user: dict = Depends(get_current_user_from_cookie),
    db: AsyncSession = Depends(get_db)
):
    """
    Analyze a specific field based on its data
    
    Retrieves field data including fertilizer usage, weather data, and alerts,
    then provides AI-powered analysis and recommendations.
    
    - **field_id**: The ID of the field to analyze
    - **session_id**: Optional session ID for tracking
    """
    try:
        farmer_id = current_user["user_id"]
        # Generate session_id if not provided
        session_id = session_id or str(uuid.uuid4())

        # Call the LLM manager
        result = await llm_manager.analyze_field(
            db=db,
            farmer_id=farmer_id,
            field_id=field_id,
            session_id=session_id
        )

        return {
            "analysis": result["analysis"],
            "conversation_id": result["conversation_id"],
            "field_name": result["field_name"],
            "model": result["model"]
        }

    except ValueError as e:
        # Field not found or permission error
        logger.warning(f"Field access error: {e}")
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"Error in analyze-field endpoint: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to analyze field: {str(e)}"
        )


@router.get("/history/{session_id}", response_model=ConversationHistory)
async def get_conversation_history(
    session_id: str,
    current_user: dict = Depends(get_current_user_from_cookie),
    db: AsyncSession = Depends(get_db)
):
    """
    Retrieve conversation history for a specific session
    
    - **session_id**: The session ID to retrieve history for
    """
    try:
        farmer_id = current_user["user_id"]
        # Query conversation history
        query = text("""
            SELECT 
                id,
                message,
                response,
                model,
                metadata,
                created_at
            FROM "AIConversations"
            WHERE farmer_id = :farmer_id AND session_id = :session_id
            ORDER BY created_at ASC
        """)

        result = await db.execute(query, {
            "farmer_id": farmer_id,
            "session_id": session_id
        })
        rows = result.fetchall()

        if not rows:
            return ConversationHistory(
                session_id=session_id,
                conversations=[],
                total=0
            )

        # Build conversation list
        conversations = []
        for row in rows:
            conversations.append({
                "id": row[0],
                "message": row[1],
                "response": row[2],
                "model": row[3],
                "metadata": row[4],
                "created_at": row[5]
            })

        return ConversationHistory(
            session_id=session_id,
            conversations=conversations,
            total=len(conversations)
        )

    except Exception as e:
        logger.error(f"Error retrieving conversation history: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to retrieve conversation history: {str(e)}"
        )


@router.get("/sessions")
async def get_user_sessions(
    current_user: dict = Depends(get_current_user_from_cookie),
    db: AsyncSession = Depends(get_db)
):
    """
    Get all conversation sessions for the authenticated farmer
    """
    try:
        farmer_id = current_user["user_id"]
        query = text("""
            SELECT 
                session_id,
                COUNT(*) as message_count,
                MAX(created_at) as last_message,
                MIN(created_at) as first_message
            FROM "AIConversations"
            WHERE farmer_id = :farmer_id
            GROUP BY session_id
            ORDER BY MAX(created_at) DESC
        """)

        result = await db.execute(query, {"farmer_id": farmer_id})
        rows = result.fetchall()

        sessions = []
        for row in rows:
            sessions.append({
                "session_id": row[0],
                "message_count": row[1],
                "last_message": row[2],
                "first_message": row[3]
            })

        return {
            "sessions": sessions,
            "total_sessions": len(sessions)
        }

    except Exception as e:
        logger.error(f"Error retrieving user sessions: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to retrieve sessions: {str(e)}"
        )
