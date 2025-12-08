from pydantic import BaseModel

class CategorySchema(BaseModel):
    category_id: int
    name: str

    class Config:
        orm_mode = True


class CategoryListResponse(BaseModel):
    categories: list[CategorySchema]
