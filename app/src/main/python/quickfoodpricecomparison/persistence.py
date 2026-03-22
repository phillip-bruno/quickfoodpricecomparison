import pickle
from pathlib import Path

from quickfoodpricecomparison.models import HistoryEntry


def ensure_data_directory(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)


def load_history(path: Path) -> list[HistoryEntry]:
    try:
        with open(path, "rb") as handle:
            data = pickle.load(handle)
        return [HistoryEntry(**item) for item in data]
    except (FileNotFoundError, pickle.UnpicklingError, EOFError, Exception):
        return []


def save_history(entries: list[HistoryEntry], path: Path) -> None:
    deduped = deduplicate_entries(entries)
    dicts = [e.to_dict() for e in deduped]
    with open(path, "wb+") as handle:
        pickle.dump(dicts, handle, protocol=pickle.HIGHEST_PROTOCOL)


def deduplicate_entries(entries: list[HistoryEntry]) -> list[HistoryEntry]:
    seen = set()
    result = []
    for entry in entries:
        key = (entry.unit_selection, entry.unit_value, entry.density_selection, entry.comment)
        if key not in seen:
            seen.add(key)
            result.append(entry)
    return result


def history_to_bytes(entries: list[HistoryEntry]) -> bytes:
    deduped = deduplicate_entries(entries)
    dicts = [e.to_dict() for e in deduped]
    return pickle.dumps(dicts, protocol=pickle.HIGHEST_PROTOCOL)


def cleanup_history(
    entries: list[HistoryEntry],
    current_density: str,
    current_unit: str,
    current_value,
) -> list[HistoryEntry]:
    """Return a new list with empty and duplicate-of-current entries removed."""
    result = []
    for entry in entries:
        if entry.is_empty():
            continue
        if (entry.density_selection == current_density
                and entry.unit_selection == current_unit
                and entry.unit_value == current_value):
            continue
        result.append(entry)
    return result
