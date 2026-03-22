from pathlib import Path

from toga.platform import current_platform

from quickfoodpricecomparison.constants import HISTORY_FILENAME


def is_android() -> bool:
    return current_platform == "android"


def get_csv_path() -> Path:
    if is_android():
        import pkg_resources
        return Path(pkg_resources.resource_filename("quickfoodpricecomparison", "food_density.csv"))
    return Path(__file__).parent / "food_density.csv"


def get_history_path(app_data_dir: Path) -> Path:
    return app_data_dir / HISTORY_FILENAME
