from quickfoodpricecomparison.constants import ureg
from quickfoodpricecomparison.models import ConversionUnit


def compute_price_per_unit(price: float, source_mass):
    """Compute the raw price-per-unit ratio."""
    return price / source_mass


def convert_price_for_unit(
    price: float,
    source_mass,
    target: ConversionUnit,
    density,
) -> float:
    if target.is_mass:
        return round(price / source_mass.to(target.unit).m * target.value, 5)
    else:
        return round(price / (source_mass / density).to(target.unit).m * target.value, 5)


def convert_all_prices(
    price: float,
    source_mass,
    density_g_per_ml: float,
    units: tuple[ConversionUnit, ...],
) -> dict[str, float]:
    """Convert price to all target units, returning {unit_key: rounded_price}."""
    if price < 0:
        raise ValueError("Price must be non-negative")
    if density_g_per_ml <= 0:
        raise ValueError("Density must be positive")

    density = (density_g_per_ml * ureg.gram) / (1 * ureg.ml)
    results = {}
    for unit in units:
        results[unit.key] = convert_price_for_unit(price, source_mass, unit, density)
    return results
