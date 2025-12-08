from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy import Integer, String, ForeignKey
from app.db.base import Base


class LexicalItem(Base):
    __tablename__ = "lexical_item"

    lexical_item_id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    category_id: Mapped[int] = mapped_column(ForeignKey("category.category_id"), nullable=False)
    text: Mapped[str] = mapped_column(String, nullable=False)

    category = relationship("Category", back_populates="lexical_items")
