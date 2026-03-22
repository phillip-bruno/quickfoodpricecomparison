import csv

from quickfoodpricecomparison.models import FoodDensity
from quickfoodpricecomparison.platform_utils import get_csv_path


def load_food_densities() -> list[FoodDensity]:
    csv_file_path = get_csv_path()
    results = []
    with open(csv_file_path, "r", encoding="utf-8-sig") as csv_file:
        reader = csv.DictReader(csv_file)
        for row in reader:
            try:
                sg_raw = row.get("specific_gravity", "")
                try:
                    specific_gravity = float(sg_raw) if sg_raw else None
                except ValueError:
                    specific_gravity = None
                results.append(FoodDensity(
                    food_name=row["food_name"],
                    g_ml=float(row["g_ml"]),
                    specific_gravity=specific_gravity,
                    biblio_id=row.get("biblio_id", ""),
                    category=row.get("category", ""),
                ))
            except (ValueError, KeyError):
                continue
    return results


def get_food_categories(items: list[FoodDensity]) -> list[str]:
    """Extract sorted unique category names from food density data."""
    categories = sorted({item.category for item in items if item.category})
    return categories
