from quickfoodpricecomparison.models import HistoryEntry
from quickfoodpricecomparison.persistence import (
    cleanup_history,
    deduplicate_entries,
    history_to_bytes,
    load_history,
    save_history,
)


def test_save_and_load_roundtrip(tmp_path):
    path = tmp_path / "history.pickle"
    entries = [
        HistoryEntry("Price per kg", "5.0", "milk", "test"),
        HistoryEntry("Price per oz", "3.0", "water", ""),
    ]
    save_history(entries, path)
    loaded = load_history(path)
    assert len(loaded) == 2
    assert loaded[0].unit_selection == "Price per kg"
    assert loaded[1].density_selection == "water"


def test_load_missing_file(tmp_path):
    result = load_history(tmp_path / "nonexistent.pickle")
    assert result == []


def test_load_corrupt_file(tmp_path):
    path = tmp_path / "corrupt.pickle"
    path.write_bytes(b"not a pickle")
    result = load_history(path)
    assert result == []


def test_deduplicate_entries():
    entries = [
        HistoryEntry("A", "1", "milk", ""),
        HistoryEntry("A", "1", "milk", ""),
        HistoryEntry("B", "2", "water", ""),
    ]
    result = deduplicate_entries(entries)
    assert len(result) == 2


def test_history_to_bytes():
    entries = [HistoryEntry("A", "1", "milk", "")]
    data = history_to_bytes(entries)
    assert isinstance(data, bytes)
    assert len(data) > 0


def test_cleanup_history_removes_empty():
    entries = [
        HistoryEntry(),
        HistoryEntry("A", "1", "milk", ""),
    ]
    result = cleanup_history(entries, "water", "B", "2")
    assert len(result) == 1
    assert result[0].unit_selection == "A"


def test_cleanup_history_removes_current():
    entries = [
        HistoryEntry("A", "1", "milk", ""),
        HistoryEntry("B", "2", "water", "note"),
    ]
    result = cleanup_history(entries, "milk", "A", "1")
    assert len(result) == 1
    assert result[0].unit_selection == "B"
