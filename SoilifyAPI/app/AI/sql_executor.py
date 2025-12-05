"""
SQL Executor for safely executing read-only SQL queries
Only allows SELECT statements for security
"""

import asyncpg
import logging

from app.config.settings import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class SQLExecutor:
    """Execute read-only SQL queries safely"""
    
    def __init__(self, db_url: str = None):
        """Initialize with database URL"""
        self.db_url = db_url or settings.DATABASE_URL
        if not self.db_url:
            raise ValueError("DATABASE_URL not provided or set in settings")
        logger.info("SQLExecutor initialized")
    
    def _validate_query(self, sql: str) -> None:
        """Validate that query is safe (SELECT only)"""
        sql_lower = sql.strip().lower()
        
        # Must start with SELECT
        if not sql_lower.startswith("select"):
            raise ValueError("Only SELECT queries are allowed")
        
        # Block dangerous keywords
        dangerous_keywords = [
            "insert", "update", "delete", "drop", "create", "alter",
            "truncate", "grant", "revoke", "exec", "execute"
        ]
        
        for keyword in dangerous_keywords:
            if keyword in sql_lower:
                raise ValueError(f"Query contains forbidden keyword: {keyword}")
    
    async def execute_sql(self, sql: str) -> dict:
        """
        Execute a read-only SQL SELECT query
        
        Args:
            sql: The SQL SELECT query to execute
            
        Returns:
            dict with 'columns' (list of column names) and 'rows' (list of tuples)
        """
        try:
            # Validate query is safe
            self._validate_query(sql)
            
            # Connect and execute
            conn = await asyncpg.connect(self.db_url)
            try:
                # Prepare and execute query
                stmt = await conn.prepare(sql)
                rows = await stmt.fetch()
                
                # Get column names
                columns = [attr.name for attr in stmt.get_attributes()]
                
                # Convert rows to list of tuples
                rows_list = [tuple(row) for row in rows]
                
                logger.info(f"Query executed successfully: {len(rows_list)} rows returned")
                
                return {
                    "columns": columns,
                    "rows": rows_list
                }
                
            finally:
                await conn.close()
                
        except ValueError as e:
            # Security validation error
            logger.error(f"Query validation failed: {e}")
            raise
            
        except Exception as e:
            # Database execution error
            logger.error(f"Error executing query: {e}")
            raise RuntimeError(f"Database query failed: {str(e)}")