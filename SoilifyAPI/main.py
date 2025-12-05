"""
Soilify API - Farming Management System
Main FastAPI application with all endpoints for Android app
"""

from dotenv import load_dotenv

# Load environment variables from .env file FIRST before any other imports
load_dotenv()

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
import logging
import time

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Also configure uvicorn loggers to show access logs
logging.getLogger("uvicorn.access").setLevel(logging.INFO)
logging.getLogger("uvicorn.error").setLevel(logging.INFO)

# Import all routers
from app.api.auth import router as auth_router
from app.api.home import router as home_router
from app.api.users import router as users_router
from app.api.fields import router as fields_router
from app.api.record_fertilizer_usage import router as fertilizer_router
from app.api.weather_data import router as weather_router
from app.api.alerts import router as alerts_router
from app.api.analytics import router as analytics_router
from app.api.chat_withAI import router as ai_router

# Create FastAPI app
app = FastAPI(
    title="Soilify API",
    description="Farming Management System API for Android App",
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc"
)

# CORS configuration for Android app
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:3000",      # React web (if any)
        "http://localhost:8081",      # Expo development
        "http://10.0.2.2:8000",       # Android emulator
        "http://192.168.100.46:8000", # Your local network IP
        "*"                           # Allow all for development (restrict in production)
    ],
    allow_credentials=False,          # Set to False when using "*" origin
    allow_methods=["*"],
    allow_headers=["*"],
)

# Request logging middleware
@app.middleware("http")
async def log_requests(request: Request, call_next):
    """Log all incoming requests and their response times"""
    start_time = time.time()
    
    # Log the incoming request
    logger.info(f"➡️  {request.method} {request.url.path}")
    
    # Process the request
    response = await call_next(request)
    
    # Calculate processing time
    process_time = time.time() - start_time
    
    # Log the response
    logger.info(f"⬅️  {request.method} {request.url.path} - Status: {response.status_code} - Time: {process_time:.3f}s")
    
    return response

# Include all routers
app.include_router(auth_router, prefix="/api/auth", tags=["Authentication"])
app.include_router(home_router, prefix="/api/home", tags=["Home/Dashboard"])
app.include_router(users_router, prefix="/api/users", tags=["Users"])
app.include_router(fields_router, prefix="/api/fields", tags=["Fields"])
app.include_router(fertilizer_router, prefix="/api/fertilizer-usage", tags=["Fertilizer Usage"])
app.include_router(weather_router, prefix="/api/weather-data", tags=["Weather Data"])
app.include_router(alerts_router, prefix="/api/alerts", tags=["Alerts"])
app.include_router(analytics_router, prefix="/api/analytics", tags=["Analytics"])
app.include_router(ai_router, tags=["AI Chat"])  # AI router already has /api/ai prefix

# Root endpoint
@app.get("/")
async def root():
    """Root endpoint - API health check"""
    return {
        "message": "Soilify API is running",
        "version": "1.0.0",
        "status": "healthy",
        "docs": "/api/docs"
    }

# Health check endpoint
@app.get("/health")
async def health_check():
    """Health check endpoint for monitoring"""
    return {
        "status": "healthy",
        "service": "Soilify API"
    }

# Startup event
@app.on_event("startup")
async def startup_event():
    logger.info("=" * 60)
    logger.info("Soilify API Starting Up")
    logger.info("=" * 60)
    logger.info("API Documentation available at: /api/docs")
    logger.info("Available endpoints:")
    logger.info("  - POST /api/auth/signup")
    logger.info("  - POST /api/auth/signin")
    logger.info("  - GET  /api/home/dashboard")
    logger.info("  - GET  /api/users")
    logger.info("  - GET  /api/fields")
    logger.info("  - POST /api/fields")
    logger.info("  - GET  /api/fertilizer-usage")
    logger.info("  - POST /api/fertilizer-usage")
    logger.info("  - GET  /api/weather-data")
    logger.info("  - POST /api/weather-data")
    logger.info("  - GET  /api/alerts")
    logger.info("  - POST /api/alerts")
    logger.info("  - GET  /api/analytics/overview")
    logger.info("  - GET  /api/analytics/fertilizer/by-type")
    logger.info("  - GET  /api/analytics/weather/trends")
    logger.info("  - POST /api/ai/chat")
    logger.info("  - POST /api/ai/ask-sql")
    logger.info("  - POST /api/ai/analyze-field/{field_id}")
    logger.info("=" * 60)

# Shutdown event
@app.on_event("shutdown")
async def shutdown_event():
    logger.info("Soilify API shutting down...")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info",
        access_log=True  # Ensure access logs are enabled
    )