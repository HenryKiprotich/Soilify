from pydantic_settings import BaseSettings
from typing import Optional
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class Settings(BaseSettings):
    # Environment
    DEBUG: bool = True
    ENVIRONMENT: str = "development"
    
    # Database Configuration
    DATABASE_URL: str  # For asyncpg (raw SQL)
    SQLALCHEMY_DATABASE_URL: str  # For SQLAlchemy ORM
    
    # JWT Configuration
    SECRET_KEY: str
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 10080
    
    # Google Gemini API (Required for AI chatbot)
    GOOGLE_API_KEY: str
    
    # Optional API Keys (not used in Soilify, but kept for compatibility)
    HUGGINGFACEHUB_API_TOKEN: Optional[str] = None
    DEEPSEEK_API_KEY: Optional[str] = None
    DEEPSEEK_API_BASE: str = "https://api.deepseek.com"
    OPENAI_API_KEY: Optional[str] = None
    OPENAI_API_BASE: str = "https://api.openai.com/v1"
    
    # CORS Configuration
    ALLOWED_ORIGINS: str = "http://localhost:3000,http://localhost:8081,http://10.0.2.2:8000"

    class Config:
        env_file = ".env"
        case_sensitive = False  # Allow lowercase env vars

try:
    settings = Settings()
    logger.info("Settings loaded successfully.")
    logger.info(f"Environment: {settings.ENVIRONMENT}")
    logger.info(f"Database: {settings.SQLALCHEMY_DATABASE_URL.split('@')[1] if '@' in settings.SQLALCHEMY_DATABASE_URL else 'configured'}")
    logger.info(f"Google API Key: {'✓ Set' if settings.GOOGLE_API_KEY else '✗ Missing'}")
except Exception as e:
    logger.exception(f"Error loading settings: {e}")
    raise