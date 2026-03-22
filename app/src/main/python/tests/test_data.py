from quickfoodpricecomparison.data import get_food_categories, load_food_densities


def test_load_food_densities_count():
    items = load_food_densities()
    assert len(items) == 534


def test_load_food_densities_first_item():
    items = load_food_densities()
    first = items[0]
    assert first.food_name == "alcohol, ethyl"
    assert first.g_ml == 0.789
    assert isinstance(first.g_ml, float)
    assert first.category == "Beverages, alcoholic"


def test_load_food_densities_has_food_name_attr():
    items = load_food_densities()
    assert hasattr(items[0], "food_name")


def test_get_food_categories():
    items = load_food_densities()
    categories = get_food_categories(items)
    assert len(categories) > 0
    assert "Beverages, alcoholic" in categories
    assert categories == sorted(categories)
