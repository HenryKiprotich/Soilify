from fastapi import APIRouter, Depends
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text

from app.schemas.user_schema import UserRead
from app.database.database import get_db

router = APIRouter(prefix="", tags=["users"])

@router.get("", response_model=List[UserRead])
async def get_users(db: AsyncSession = Depends(get_db)):
    """
    Retrieve all users from the simple users table.
    """
    sql = text("""
        SELECT 
            id, 
            first_name, 
            other_name, 
            phone_number, 
            email_adress, 
            location, 
            created_at
        FROM users
        ORDER BY created_at DESC
    """)
    
    result = await db.execute(sql)
    rows = result.fetchall()

    users = []
    for row in rows:
        users.append({
            "id": row[0],
            "first_name": row[1],
            "other_name": row[2],
            "phone_number": row[3],
            "email_adress": row[4],
            "location": row[5],
            "created_at": row[6],
        })

    return users

