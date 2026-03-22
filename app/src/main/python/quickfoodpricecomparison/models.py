from dataclasses import dataclass
from enum import Enum
from typing import Optional


class UnitCategory(Enum):
    METRIC_MASS = "metric_mass"
    IMPERIAL_MASS = "imperial_mass"
    METRIC_VOLUME = "metric_volume"
    IMPERIAL_VOLUME = "imperial_volume"


MASS_CATEGORIES = {UnitCategory.METRIC_MASS, UnitCategory.IMPERIAL_MASS}
VOLUME_CATEGORIES = {UnitCategory.METRIC_VOLUME, UnitCategory.IMPERIAL_VOLUME}


@dataclass(frozen=True)
class ConversionUnit:
    unit: str
    value: float
    category: UnitCategory

    @property
    def key(self) -> str:
        val_str = "half" if self.value == 0.5 else str(int(self.value))
        unit_str = self.unit.replace("_", "") if self.unit == "fluid_ounce" else self.unit
        return f"{unit_str}_{val_str}"

    @property
    def is_mass(self) -> bool:
        return self.category in MASS_CATEGORIES

    @property
    def is_volume(self) -> bool:
        return self.category in VOLUME_CATEGORIES


@dataclass
class HistoryEntry:
    unit_selection: Optional[str] = None
    unit_value: Optional[str] = None
    density_selection: Optional[str] = None
    comment: Optional[str] = None

    def to_dict(self) -> dict:
        return {
            "unit_selection": self.unit_selection,
            "unit_value": self.unit_value,
            "density_selection": self.density_selection,
            "comment": self.comment,
        }

    def is_empty(self) -> bool:
        return (
            self.density_selection is None
            and self.unit_selection is None
            and self.unit_value is None
        )


@dataclass(frozen=True)
class FoodDensity:
    food_name: str
    g_ml: float
    specific_gravity: Optional[float] = None
    biblio_id: str = ""
    category: str = ""
