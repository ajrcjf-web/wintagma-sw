from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql


# Revisiones
revision = "0002"
down_revision = "0001"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.create_table(
        "exercise",
        sa.Column("exercise_id", sa.Integer(), primary_key=True, autoincrement=True),
        sa.Column("category_id", sa.Integer(), sa.ForeignKey("category.category_id"), nullable=False),
        sa.Column("lexical_item_id", sa.Integer(), sa.ForeignKey("lexical_item.lexical_item_id"), nullable=False),
        # option_order: [1..5] almacenado como ARRAY de enteros en PostgreSQL
        sa.Column("option_order", postgresql.ARRAY(sa.Integer()), nullable=False),
    )

    op.create_table(
        "exercise_option",
        sa.Column("exercise_id", sa.Integer(), sa.ForeignKey("exercise.exercise_id"), primary_key=True),
        sa.Column("option_id", sa.Integer(), primary_key=True),
        sa.Column("text", sa.String(), nullable=False),
        sa.Column("is_correct", sa.Boolean(), nullable=False),
    )


def downgrade() -> None:
    op.drop_table("exercise_option")
    op.drop_table("exercise")
