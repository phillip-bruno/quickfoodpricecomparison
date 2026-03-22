from pint import UnitRegistry
from toga.style import Pack
from toga.style.pack import COLUMN, ROW, RIGHT, CENTER

from quickfoodpricecomparison.models import ConversionUnit, UnitCategory

FORMAL_NAME = "Quick Food Price Comparison"
FULL_NAME = "com.drmantistobbogan.foodpricecomparison"

HISTORY_FILENAME = "conversion_history.pickle"

ureg = UnitRegistry()

SELECTION_LIST = [
    {"name": "Price per kilogram", "mass": ureg.kilogram * 1},
    {"name": "Price per 900 grams", "mass": ureg.gram * 900},
    {"name": "Price per 800 grams", "mass": ureg.gram * 800},
    {"name": "Price per 750 grams", "mass": ureg.gram * 750},
    {"name": "Price per 700 grams", "mass": ureg.gram * 700},
    {"name": "Price per 600 grams", "mass": ureg.gram * 600},
    {"name": "Price per 500 grams", "mass": ureg.gram * 500},
    {"name": "Price per 400 grams", "mass": ureg.gram * 400},
    {"name": "Price per 250 grams", "mass": ureg.gram * 250},
    {"name": "Price per 200 grams", "mass": ureg.gram * 200},
    {"name": "Price per 100 grams", "mass": ureg.gram * 100},
    {"name": "Price per gram", "mass": ureg.gram * 1},
    {"name": "Price per milligram", "mass": ureg.milligram * 1},
    {"name": "Price per pound", "mass": ureg.pound * 1},
    {"name": "Price per 12 oz", "mass": ureg.ounce * 12},
    {"name": "Price per 8 oz", "mass": ureg.ounce * 8},
    {"name": "Price per 6 oz", "mass": ureg.ounce * 6},
    {"name": "Price per 4 oz", "mass": ureg.ounce * 4},
    {"name": "Price per 2 oz", "mass": ureg.ounce * 2},
    {"name": "Price per oz", "mass": ureg.ounce * 1},
]

MASS_UNITS = {"kg", "g", "mg", "lbs", "oz"}
VOLUME_UNITS = {"l", "ml", "gallon", "quart", "pint", "fluid_ounce", "cup"}

CONVERSION_UNITS = (
    # Metric mass
    ConversionUnit("kg", 1, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 900, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 800, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 750, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 700, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 600, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 500, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 400, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 300, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 250, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 200, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 100, UnitCategory.METRIC_MASS),
    ConversionUnit("g", 1, UnitCategory.METRIC_MASS),
    ConversionUnit("mg", 1, UnitCategory.METRIC_MASS),
    # Imperial mass
    ConversionUnit("lbs", 3, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("lbs", 1, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 12, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 8, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 6, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 4, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 2, UnitCategory.IMPERIAL_MASS),
    ConversionUnit("oz", 1, UnitCategory.IMPERIAL_MASS),
    # Metric volume
    ConversionUnit("l", 1, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 900, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 800, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 700, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 600, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 500, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 400, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 300, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 200, UnitCategory.METRIC_VOLUME),
    ConversionUnit("ml", 100, UnitCategory.METRIC_VOLUME),
    # Imperial volume
    ConversionUnit("gallon", 1, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("gallon", 0.5, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("quart", 1, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("pint", 1, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("fluid_ounce", 1, UnitCategory.IMPERIAL_VOLUME),
    ConversionUnit("cup", 1, UnitCategory.IMPERIAL_VOLUME),
)

ALL_CATEGORIES_LABEL = "All Categories"

# Base styles (deprecated padding→margin)
TEXT_STYLE = Pack(margin=(0, 5), font_size=14)
BUTTON_STYLE = Pack(margin=(0, 5), font_size=14, flex=150)

# Section headings
SECTION_HEADING_STYLE = Pack(font_size=16, font_weight="bold", margin=(10, 5, 2, 5))

# Result display
RESULT_VALUE_STYLE = Pack(
    font_family="monospace", font_size=16, font_weight="bold",
    text_align=RIGHT, flex=1, margin=(0, 5),
)
RESULT_LABEL_STYLE = Pack(font_size=14, flex=2, margin=(0, 5))
RESULT_ROW_STYLE = Pack(direction=ROW, margin=(2, 0))

# Price per unit hero display
PPU_STYLE = Pack(
    font_family="monospace", font_size=20, font_weight="bold",
    text_align=CENTER, margin=(5, 5),
)

# Input section
INPUT_SECTION_STYLE = Pack(direction=COLUMN, margin=10, background_color="#f5f5f5")

# Food info subtitle
FOOD_INFO_STYLE = Pack(font_size=12, font_style="italic", color="#666666", margin=(0, 5, 5, 5))

# Empty state
EMPTY_STATE_STYLE = Pack(font_style="italic", color="#999999", margin=10)
