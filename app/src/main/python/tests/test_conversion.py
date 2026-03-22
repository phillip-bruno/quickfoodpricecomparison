import pytest

from quickfoodpricecomparison.constants import ureg
from quickfoodpricecomparison.conversion import (
    compute_price_per_unit,
    convert_all_prices,
)
from quickfoodpricecomparison.models import ConversionUnit, UnitCategory


def test_compute_price_per_unit():
    result = compute_price_per_unit(10.0, ureg.kilogram * 1)
    assert round(result.m, 5) == 10.0


def test_convert_all_prices_mass():
    units = (ConversionUnit("kg", 1, UnitCategory.METRIC_MASS),)
    results = convert_all_prices(10.0, ureg.kilogram * 1, 1.0, units)
    assert results["kg_1"] == 10.0


def test_convert_all_prices_gram_conversion():
    units = (ConversionUnit("g", 100, UnitCategory.METRIC_MASS),)
    results = convert_all_prices(10.0, ureg.kilogram * 1, 1.0, units)
    assert results["g_100"] == 1.0


def test_convert_all_prices_volume():
    units = (ConversionUnit("l", 1, UnitCategory.METRIC_VOLUME),)
    # density 1.0 g/ml means 1kg = 1L
    results = convert_all_prices(10.0, ureg.kilogram * 1, 1.0, units)
    assert results["l_1"] == 10.0


def test_convert_all_prices_volume_with_density():
    units = (ConversionUnit("ml", 100, UnitCategory.METRIC_VOLUME),)
    # density 0.789 g/ml (ethanol): 1kg of ethanol = 1000/0.789 ml
    results = convert_all_prices(10.0, ureg.kilogram * 1, 0.789, units)
    # price per 100ml = 10 / (1000/0.789) * 100 = 10 * 0.789 / 10 = 0.789
    assert results["ml_100"] == 0.789


def test_convert_all_prices_negative_price():
    units = (ConversionUnit("kg", 1, UnitCategory.METRIC_MASS),)
    with pytest.raises(ValueError, match="non-negative"):
        convert_all_prices(-1.0, ureg.kilogram * 1, 1.0, units)


def test_convert_all_prices_zero_density():
    units = (ConversionUnit("kg", 1, UnitCategory.METRIC_MASS),)
    with pytest.raises(ValueError, match="positive"):
        convert_all_prices(10.0, ureg.kilogram * 1, 0, units)


def test_convert_all_prices_zero_price():
    units = (ConversionUnit("kg", 1, UnitCategory.METRIC_MASS),)
    results = convert_all_prices(0.0, ureg.kilogram * 1, 1.0, units)
    assert results["kg_1"] == 0.0
