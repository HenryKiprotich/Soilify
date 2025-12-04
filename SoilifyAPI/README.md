# Soilify API - Farming Management System

A comprehensive FastAPI backend for the Soilify Android application, providing endpoints for farming management including fields, fertilizer usage tracking, weather data, alerts, and AI-powered farming advice.

## Features

- **User Authentication**: JWT-based authentication with HttpOnly cookies
- **Field Management**: CRUD operations for managing farm fields
- **Fertilizer Usage Tracking**: Record and track fertilizer applications
- **Weather Data**: Track weather conditions per field
- **Alerts System**: Create and manage farm alerts
- **AI Chatbot**: Google Gemini-powered farming advisor (NO LangChain)
  - General farming advice
  - Natural language to SQL queries
  - Field analysis and recommendations

## Tech Stack

- **Framework**: FastAPI
- **Database**: PostgreSQL with AsyncPG
- **Authentication**: JWT tokens with bcrypt
- **AI**: Google Gemini (gemini-1.5-flash)
- **ORM**: Direct SQL queries (no SQLAlchemy ORM)

## Installation

### 1. Clone the repository

```bash
git clone <repository-url>
cd SoilifyAPI
```

### 2. Create virtual environment

```bash
python -m venv venv

# Windows PowerShell
.\venv\Scripts\Activate.ps1

# Windows CMD
venv\Scripts\activate.bat

# Linux/Mac
source venv/bin/activate
```

### 3. Install dependencies

```bash
pip install -r requirements.txt
```

### 4. Configure environment variables

```bash
# Copy the example file
copy .env.example .env

# Edit .env with your actual values
```

**Required Environment Variables:**
- `DATABASE_URL`: PostgreSQL connection string
- `SECRET_KEY`: Secret key for JWT tokens
- `GOOGLE_API_KEY`: Your Google Gemini API key

### 5. Set up the database

Run the SQL scripts to create all tables:

```sql
-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    other_name VARCHAR(100),
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    email_adress VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Fields table
CREATE TABLE fields (
    id SERIAL PRIMARY KEY,
    farmer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    field_name VARCHAR(100) NOT NULL,
    soil_type VARCHAR(50),
    crop_type VARCHAR(50),
    size_hectares DECIMAL(10, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- FertiliserUsage table
CREATE TABLE fertiliserusage (
    id SERIAL PRIMARY KEY,
    farmer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    field_id INTEGER REFERENCES fields(id) ON DELETE CASCADE,
    fertiliser_type VARCHAR(100),
    amount_kg DECIMAL(10, 2),
    weather VARCHAR(50),
    notes TEXT,
    date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- WeatherData table
CREATE TABLE weatherdata (
    id SERIAL PRIMARY KEY,
    field_id INTEGER REFERENCES fields(id) ON DELETE CASCADE,
    temperature DECIMAL(5, 2),
    rainfall DECIMAL(10, 2),
    soil_moisture DECIMAL(5, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Alerts table
CREATE TABLE alerts (
    id SERIAL PRIMARY KEY,
    farmer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    field_id INTEGER REFERENCES fields(id) ON DELETE SET NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- AI Conversations table
CREATE TABLE ai_conversations (
    id SERIAL PRIMARY KEY,
    farmer_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(100),
    message TEXT NOT NULL,
    response TEXT NOT NULL,
    model VARCHAR(50) DEFAULT 'gemini-1.5-flash',
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes
CREATE INDEX idx_fields_farmer_id ON fields(farmer_id);
CREATE INDEX idx_fertiliserusage_farmer_id ON fertiliserusage(farmer_id);
CREATE INDEX idx_fertiliserusage_field_id ON fertiliserusage(field_id);
CREATE INDEX idx_weatherdata_field_id ON weatherdata(field_id);
CREATE INDEX idx_alerts_farmer_id ON alerts(farmer_id);
CREATE INDEX idx_ai_conversations_farmer_id ON ai_conversations(farmer_id);
CREATE INDEX idx_ai_conversations_session_id ON ai_conversations(session_id);
```

### 6. Run the API

```bash
# Development mode with auto-reload
uvicorn main:app --reload --host 0.0.0.0 --port 8000

# Or use the built-in runner
python main.py
```

The API will be available at:
- **API**: http://localhost:8000
- **Interactive Docs**: http://localhost:8000/api/docs
- **ReDoc**: http://localhost:8000/api/redoc

## API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/signup` - Register new farmer
- `POST /api/auth/signin` - Login (sets HttpOnly cookie)
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password with token
- `GET /api/auth/verify` - Verify JWT token

### Users (`/api/users`)
- `GET /api/users` - Get all users (admin)

### Fields (`/api/fields`)
- `GET /api/fields` - Get all farmer's fields
- `GET /api/fields/{field_id}` - Get specific field
- `POST /api/fields` - Create new field
- `PUT /api/fields/{field_id}` - Update field
- `DELETE /api/fields/{field_id}` - Delete field

### Fertilizer Usage (`/api/fertilizer-usage`)
- `GET /api/fertilizer-usage` - Get all usage records
- `GET /api/fertilizer-usage/fields-dropdown` - Get fields for dropdown
- `GET /api/fertilizer-usage/{usage_id}` - Get specific record
- `POST /api/fertilizer-usage` - Create usage record
- `PUT /api/fertilizer-usage/{usage_id}` - Update record
- `DELETE /api/fertilizer-usage/{usage_id}` - Delete record

### Weather Data (`/api/weather-data`)
- `GET /api/weather-data` - Get all weather records
- `GET /api/weather-data/stats` - Get weather statistics
- `GET /api/weather-data/fields-dropdown` - Get fields dropdown
- `GET /api/weather-data/{record_id}` - Get specific record
- `POST /api/weather-data` - Create weather record
- `PUT /api/weather-data/{record_id}` - Update record
- `DELETE /api/weather-data/{record_id}` - Delete record

### Alerts (`/api/alerts`)
- `GET /api/alerts` - Get all alerts
- `GET /api/alerts/summary` - Get alerts statistics
- `GET /api/alerts/fields-dropdown` - Get fields dropdown
- `GET /api/alerts/{alert_id}` - Get specific alert
- `POST /api/alerts` - Create alert
- `PUT /api/alerts/{alert_id}` - Update alert
- `DELETE /api/alerts/{alert_id}` - Delete alert
- `DELETE /api/alerts/bulk/delete` - Bulk delete alerts

### AI Chat (`/api/ai`)
- `POST /api/ai/chat` - General farming advice chat
- `POST /api/ai/ask-sql` - Ask questions about your data
- `POST /api/ai/analyze-field/{field_id}` - Analyze specific field
- `GET /api/ai/history/{session_id}` - Get conversation history
- `GET /api/ai/sessions` - Get all user sessions

## Authentication

All endpoints (except signup and signin) require authentication via JWT token stored in HttpOnly cookie.

**Android App Integration:**
1. Call `/api/auth/signin` with credentials
2. Cookie is automatically set by the server
3. Include cookie in subsequent requests
4. Cookie contains farmer_id for data filtering

## AI Chatbot Features

The AI chatbot uses **Google Gemini only** (no LangChain) and provides:

1. **General Farming Advice**: Ask any farming-related questions
2. **Data Queries**: Ask questions about your data in natural language
3. **Field Analysis**: Get comprehensive analysis of specific fields

**Example Prompts:**
- "What's the best fertilizer for clay soil?"
- "How much fertilizer did I use last week?"
- "Analyze my corn field"
- "What are the optimal weather conditions for wheat?"

## Android Studio Integration

### Retrofit Setup

```kotlin
// ApiService.kt
interface ApiService {
    @POST("api/auth/signin")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>
    
    @GET("api/fields")
    suspend fun getFields(): Response<List<Field>>
    
    @POST("api/ai/chat")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>
}

// Retrofit instance
val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8000/")  // For Android emulator
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiService::class.java)
```

### Cookie Management

The API uses HttpOnly cookies for authentication. Configure OkHttp with cookie jar:

```kotlin
val cookieJar = JavaNetCookieJar(CookieManager())

val client = OkHttpClient.Builder()
    .cookieJar(cookieJar)
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8000/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

## Development

### Project Structure

```
SoilifyAPI/
├── app/
│   ├── AI/
│   │   ├── llm.py              # Google Gemini manager
│   │   └── sql_executor.py     # Safe SQL executor
│   ├── api/
│   │   ├── auth.py             # Authentication endpoints
│   │   ├── users.py            # User management
│   │   ├── fields.py           # Field CRUD
│   │   ├── record_fertilizer_usage.py
│   │   ├── weather_data.py
│   │   ├── alerts.py
│   │   └── chat_withAI.py      # AI chatbot endpoints
│   ├── config/
│   │   └── settings.py
│   ├── database/
│   │   └── database.py         # Database connection
│   ├── models/                 # Database models
│   ├── schemas/                # Pydantic schemas
│   ├── services/
│   │   └── auth_service.py     # JWT & password utilities
│   └── prompts/                # AI prompt templates
│       ├── farming_advice.txt
│       ├── nl_to_sql.txt
│       ├── sql_response.txt
│       └── field_analysis.txt
├── main.py                     # FastAPI app
├── requirements.txt
└── .env
```

### Adding New Endpoints

1. Create router in `app/api/`
2. Import and register in `main.py`
3. Use `get_current_user_from_cookie` for authentication
4. Filter all queries by `farmer_id`

## Security

- ✅ JWT tokens in HttpOnly cookies
- ✅ Password hashing with bcrypt
- ✅ Row-level security (farmer_id filtering)
- ✅ SQL injection prevention (parameterized queries)
- ✅ Safe SQL execution (SELECT only for AI queries)
- ✅ CORS configuration for mobile apps

## License

[Your License Here]

## Support

For issues or questions, contact: [Your Contact]
