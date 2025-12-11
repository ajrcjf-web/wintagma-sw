from alembic import op
import sqlalchemy as sa


revision = "0001"
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.create_table(
        "category",
        sa.Column("category_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("name", sa.String(), nullable=False),
    )

    op.create_table(
        "lexical_item",
        sa.Column("lexical_item_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("category_id", sa.Integer(), sa.ForeignKey("category.category_id"), nullable=False),
        sa.Column("text", sa.String(), nullable=False),
    )


def downgrade() -> None:
    op.drop_table("lexical_item")
    op.drop_table("category")
