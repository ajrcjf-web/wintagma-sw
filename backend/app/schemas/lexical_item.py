# backend/app/schemas/lexical_item.py
from pydantic import BaseModel


class LexicalItemSchema(BaseModel):
    lexical_item_id: int
    category_id: int
    text: str

    class Config:
        orm_mode = True


class LexicalItemListResponse(BaseModel):
    items: list[LexicalItemSchema]
