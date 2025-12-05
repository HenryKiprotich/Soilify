"""
Simple LLM Manager using only Google Gemini AI
No LangChain dependencies - direct API calls
"""

import os
import json
import logging
import uuid
from typing import Optional, Dict, Any
from datetime import datetime

import google.generativeai as genai
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

from app.config.settings import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class LLMManager:
    """Simple LLM manager using only Google Gemini"""

    def __init__(self):
        """Initialize Google Gemini API"""
        api_key = settings.GOOGLE_API_KEY
        if not api_key:
            raise ValueError("GOOGLE_API_KEY environment variable not set")

        genai.configure(api_key=api_key)
        self.model_name = "gemini-1.5-flash"
        self.model = genai.GenerativeModel(self.model_name)

        # Load prompt templates
        self.prompts = self._load_prompts()

        logger.info(f"LLM Manager initialized with model: {self.model_name}")

    def _load_prompts(self) -> Dict[str, str]:
        """Load all prompt templates from the prompts folder"""
        prompts = {}
        prompts_dir = os.path.join(os.path.dirname(__file__), "..", "prompts")

        prompt_files = {
            "farming_advice": "farming_advice.txt",
            "nl_to_sql": "nl_to_sql.txt",
            "sql_response": "sql_response.txt",
            "field_analysis": "field_analysis.txt"
        }

        for key, filename in prompt_files.items():
            filepath = os.path.join(prompts_dir, filename)
            try:
                if os.path.exists(filepath):
                    with open(filepath, "r", encoding="utf-8") as f:
                        prompts[key] = f.read()
                else:
                    logger.warning(f"Prompt file not found: {filepath}")
                    prompts[key] = ""
            except Exception as e:
                logger.error(f"Error loading prompt {filename}: {e}")
                prompts[key] = ""

        return prompts

    async def generate_response(self, prompt: str) -> str:
        """Generate response from Gemini model"""
        try:
            response = self.model.generate_content(prompt)
            return response.text
        except Exception as e:
            logger.error(f"Error generating response: {e}")
            raise

    async def chat_about_farming(
        self,
        db: AsyncSession,
        farmer_id: int,
        message: str,
        session_id: str,
        context: Optional[str] = None
    ) -> Dict[str, Any]:
        """Chat with farmer about farming topics"""
        try:
            # Get conversation history for context
            history_query = text("""
                SELECT message, response 
                FROM "AIConversations"
                WHERE farmer_id = :farmer_id AND session_id = :session_id
                ORDER BY created_at DESC 
                LIMIT 5
            """)
            result = await db.execute(history_query, {
                "farmer_id": farmer_id, 
                "session_id": session_id
            })
            history_rows = result.fetchall()

            # Build conversation history
            conversation_history = ""
            if history_rows:
                conversation_history = "\n\nPrevious conversation:\n"
                for row in reversed(list(history_rows)):
                    conversation_history += f"Farmer: {row[0]}\nAssistant: {row[1]}\n"

            # Build the prompt
            prompt_template = self.prompts.get("farming_advice", """
You are an expert agricultural advisor helping farmers improve their crop yields and farm management.
Provide practical, actionable advice based on best farming practices.

{context}
{history}

Farmer's question: {message}

Provide a helpful, clear response:
""")

            context_text = f"\nAdditional context: {context}\n" if context else ""

            prompt = prompt_template.format(
                context=context_text,
                history=conversation_history,
                message=message
            )

            # Generate response
            response_text = await self.generate_response(prompt)

            # Store conversation
            insert_query = text("""
                INSERT INTO "AIConversations" 
                    (farmer_id, session_id, message, response, model, metadata)
                VALUES 
                    (:farmer_id, :session_id, :message, :response, :model, :metadata)
                RETURNING id
            """)

            metadata = {
                "context": context,
                "has_history": len(history_rows) > 0
            }

            result = await db.execute(insert_query, {
                "farmer_id": farmer_id,
                "session_id": session_id,
                "message": message,
                "response": response_text,
                "model": self.model_name,
                "metadata": json.dumps(metadata)
            })
            await db.commit()

            conversation_id = result.fetchone()[0]

            return {
                "response": response_text,
                "conversation_id": conversation_id,
                "session_id": session_id,
                "model": self.model_name
            }

        except Exception as e:
            logger.error(f"Error in chat_about_farming: {e}")
            raise

    async def natural_language_to_sql(
        self,
        db: AsyncSession,
        farmer_id: int,
        question: str,
        session_id: str
    ) -> Dict[str, Any]:
        """Convert natural language question to SQL query and execute it"""
        try:
            # Get database schema relevant to the farmer
            schema = f"""
Database Schema:
- users: id, first_name, other_name, phone_number, email_adress, location, created_at
- fields: id, farmer_id, field_name, soil_type, crop_type, size_hectares, created_at
- fertiliserusage: id, farmer_id, field_id, fertiliser_type, amount_kg, weather, notes, date, created_at
- weatherdata: id, field_id, temperature, rainfall, soil_moisture, created_at
- alerts: id, farmer_id, field_id, message, created_at

Important: All queries MUST filter by farmer_id = {farmer_id} to ensure data security.
"""

            # Build prompt for SQL generation
            prompt_template = self.prompts.get("nl_to_sql", """
You are an expert SQL query generator for a farming database.

{schema}

Generate a SQL SELECT query to answer this question: {question}

Rules:
1. ALWAYS include WHERE farmer_id = {farmer_id} in queries involving farmer-specific tables
2. Use proper JOINs when needed
3. Return only the SQL query, no explanations
4. Use PostgreSQL syntax
5. The query should be safe (no DELETE, UPDATE, DROP, etc.)

SQL Query:
""")

            prompt = prompt_template.format(
                schema=schema,
                question=question,
                farmer_id=farmer_id
            )

            # Generate SQL query
            sql_query = await self.generate_response(prompt)

            # Clean up the SQL query
            sql_query = sql_query.strip()
            if sql_query.startswith("```sql"):
                sql_query = sql_query[6:]
            if sql_query.startswith("```"):
                sql_query = sql_query[3:]
            if sql_query.endswith("```"):
                sql_query = sql_query[:-3]
            sql_query = sql_query.strip()

            # Execute the SQL query (read-only)
            from app.AI.sql_executor import SQLExecutor
            executor = SQLExecutor()
            results = await executor.execute_sql(sql_query)

            # Generate natural language response
            response_prompt_template = self.prompts.get("sql_response", """
The farmer asked: {question}

The SQL query executed was:
{sql_query}

The results are:
Columns: {columns}
Rows: {rows}

Provide a clear, natural language answer to the farmer's question based on these results:
""")

            response_prompt = response_prompt_template.format(
                question=question,
                sql_query=sql_query,
                columns=results.get("columns", []),
                rows=results.get("rows", [])
            )

            natural_response = await self.generate_response(response_prompt)

            # Store conversation
            insert_query = text("""
                INSERT INTO "AIConversations" 
                    (farmer_id, session_id, message, response, model, metadata)
                VALUES 
                    (:farmer_id, :session_id, :message, :response, :model, :metadata)
                RETURNING id
            """)

            metadata = {
                "type": "nl_to_sql",
                "sql_query": sql_query,
                "results_count": len(results.get("rows", []))
            }

            result = await db.execute(insert_query, {
                "farmer_id": farmer_id,
                "session_id": session_id,
                "message": question,
                "response": natural_response,
                "model": self.model_name,
                "metadata": json.dumps(metadata)
            })
            await db.commit()

            conversation_id = result.fetchone()[0]

            return {
                "question": question,
                "sql_query": sql_query,
                "results": results,
                "natural_response": natural_response,
                "conversation_id": conversation_id
            }

        except Exception as e:
            logger.error(f"Error in natural_language_to_sql: {e}")
            raise

    async def analyze_field(
        self,
        db: AsyncSession,
        farmer_id: int,
        field_id: int,
        session_id: str
    ) -> Dict[str, Any]:
        """Analyze a specific field based on its data"""
        try:
            # Get field data
            field_query = text("""
                SELECT 
                    f.field_name,
                    f.soil_type,
                    f.crop_type,
                    f.size_hectares,
                    COUNT(DISTINCT fu.id) as fertilizer_applications,
                    AVG(wd.temperature) as avg_temperature,
                    AVG(wd.rainfall) as avg_rainfall,
                    AVG(wd.soil_moisture) as avg_soil_moisture,
                    COUNT(DISTINCT a.id) as alerts_count
                FROM "Fields" f
                LEFT JOIN "FertiliserUsage" fu ON f.id = fu.field_id
                LEFT JOIN "WeatherData" wd ON f.id = wd.field_id
                LEFT JOIN "Alerts" a ON f.id = a.field_id
                WHERE f.id = :field_id AND f.farmer_id = :farmer_id
                GROUP BY f.id, f.field_name, f.soil_type, f.crop_type, f.size_hectares
            """)

            result = await db.execute(field_query, {
                "field_id": field_id,
                "farmer_id": farmer_id
            })
            field_data = result.fetchone()

            if not field_data:
                raise ValueError("Field not found or you don't have permission to access it")

            # Build analysis prompt
            prompt_template = self.prompts.get("field_analysis", """
Analyze the following field data and provide recommendations:

Field Name: {field_name}
Soil Type: {soil_type}
Crop Type: {crop_type}
Size: {size_hectares} hectares
Fertilizer Applications: {fertilizer_applications}
Average Temperature: {avg_temperature}°C
Average Rainfall: {avg_rainfall}mm
Average Soil Moisture: {avg_soil_moisture}%
Number of Alerts: {alerts_count}

Provide:
1. Overall field health assessment
2. Recommendations for improvement
3. Potential concerns or issues
4. Suggested next actions

Analysis:
""")

            prompt = prompt_template.format(
                field_name=field_data[0] or "Unknown",
                soil_type=field_data[1] or "Unknown",
                crop_type=field_data[2] or "Unknown",
                size_hectares=field_data[3] or 0,
                fertilizer_applications=field_data[4] or 0,
                avg_temperature=f"{field_data[5]:.1f}" if field_data[5] else "N/A",
                avg_rainfall=f"{field_data[6]:.1f}" if field_data[6] else "N/A",
                avg_soil_moisture=f"{field_data[7]:.1f}" if field_data[7] else "N/A",
                alerts_count=field_data[8] or 0
            )

            # Generate analysis
            analysis = await self.generate_response(prompt)

            # Store conversation
            insert_query = text("""
                INSERT INTO "AIConversations" 
                    (farmer_id, session_id, message, response, model, metadata)
                VALUES 
                    (:farmer_id, :session_id, :message, :response, :model, :metadata)
                RETURNING id
            """)

            metadata = {
                "type": "field_analysis",
                "field_id": field_id,
                "field_name": field_data[0]
            }

            result = await db.execute(insert_query, {
                "farmer_id": farmer_id,
                "session_id": session_id,
                "message": f"Analyze field: {field_data[0]}",
                "response": analysis,
                "model": self.model_name,
                "metadata": json.dumps(metadata)
            })
            await db.commit()

            conversation_id = result.fetchone()[0]

            return {
                "analysis": analysis,
                "conversation_id": conversation_id,
                "field_name": field_data[0],
                "model": self.model_name
            }

        except Exception as e:
            logger.error(f"Error in analyze_field: {e}")
            raise
