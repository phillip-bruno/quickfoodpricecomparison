from quickfoodpricecomparison.models import (
    ConversionUnit, UnitCategory, HistoryEntry, FoodDensity,
)


def test_conversion_unit_key():
    unit = ConversionUnit("kg", 1, UnitCategory.METRIC_MASS)
    assert unit.key == "kg_1"


def test_conversion_unit_key_half():
    unit = ConversionUnit("gallon", 0.5, UnitCategory.IMPERIAL_VOLUME)
    assert unit.key == "gallon_half"


def test_conversion_unit_key_fluid_ounce():
    unit = ConversionUnit("fluid_ounce", 1, UnitCategory.IMPERIAL_VOLUME)
    assert unit.key == "fluidounce_1"


def test_conversion_unit_is_mass():
    unit = ConversionUnit("kg", 1, UnitCategory.METRIC_MASS)
    assert unit.is_mass is True
    assert unit.is_volume is False


def test_conversion_unit_is_volume():
    unit = ConversionUnit("l", 1, UnitCategory.METRIC_VOLUME)
    assert unit.is_mass is False
    assert unit.is_volume is True


def test_history_entry_to_dict():
    entry = HistoryEntry("Price per kg", "5.99", "milk", "test")
    assert entry.to_dict() == {
        "unit_selection": "Price per kg",
        "unit_value": "5.99",
        "density_selection": "milk",
        "comment": "test",
    }


def test_history_entry_is_empty():
    assert HistoryEntry().is_empty() is True
    assert HistoryEntry(unit_selection="x").is_empty() is False
    assert HistoryEntry(comment="note").is_empty() is True


def test_food_density():
    fd = FoodDensity("milk", 1.033, 1.033, "USDA", "Dairy")
    assert fd.food_name == "milk"
    assert fd.g_ml == 1.033
    assert isinstance(fd.g_ml, float)


def test_food_density_defaults():
    fd = FoodDensity("water", 1.0)
    assert fd.specific_gravity is None
    assert fd.biblio_id == ""
    assert fd.category == ""
