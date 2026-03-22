"""
This application helps quickly convert between food prices per a unit of mass or volume
for metric and imperial systems.
"""
import time

import toga
from toga.style import Pack
from toga.style.pack import COLUMN, ROW, CENTER

from quickfoodpricecomparison.constants import (
    ALL_CATEGORIES_LABEL,
    BUTTON_STYLE,
    CONVERSION_UNITS,
    EMPTY_STATE_STYLE,
    FOOD_INFO_STYLE,
    FORMAL_NAME,
    FULL_NAME,
    INPUT_SECTION_STYLE,
    PPU_STYLE,
    RESULT_LABEL_STYLE,
    RESULT_ROW_STYLE,
    RESULT_VALUE_STYLE,
    SECTION_HEADING_STYLE,
    SELECTION_LIST,
    TEXT_STYLE,
    ureg,
)
from quickfoodpricecomparison.conversion import convert_all_prices
from quickfoodpricecomparison.data import get_food_categories, load_food_densities
from quickfoodpricecomparison.models import HistoryEntry, UnitCategory
from quickfoodpricecomparison.persistence import (
    cleanup_history,
    ensure_data_directory,
    history_to_bytes,
    load_history,
    save_history,
)
from quickfoodpricecomparison.platform_utils import get_history_path, is_android

# Backward-compatible re-exports for existing tests
formal_name = FORMAL_NAME
full_name = FULL_NAME
selection_list = SELECTION_LIST
text_style = TEXT_STYLE
button_style = BUTTON_STYLE


def read_food_density() -> list:
    """Backward-compatible wrapper for existing tests."""
    from quickfoodpricecomparison.platform_utils import get_csv_path
    import csv
    csv_data = []
    with open(get_csv_path(), "r", encoding="utf-8-sig") as csv_file:
        reader = csv.DictReader(csv_file)
        for row in reader:
            csv_data.append(row)
    return csv_data


class MainApp(toga.App):

    def _create_result_labels(self) -> None:
        self._result_labels: dict[str, toga.Label] = {}
        self._result_name_labels: dict[str, toga.Label] = {}
        for conv_unit in CONVERSION_UNITS:
            self._result_labels[conv_unit.key] = toga.Label("", style=RESULT_VALUE_STYLE)
            unit_name = ureg(conv_unit.unit) * conv_unit.value
            self._result_name_labels[conv_unit.key] = toga.Label(
                f"Price per {unit_name}:", style=RESULT_LABEL_STYLE
            )

    def _build_category_boxes(self) -> dict[UnitCategory, toga.Box]:
        boxes = {}
        for cat in UnitCategory:
            children = []
            for conv_unit in CONVERSION_UNITS:
                if conv_unit.category == cat:
                    children.append(toga.Box(
                        style=RESULT_ROW_STYLE,
                        children=[
                            self._result_name_labels[conv_unit.key],
                            self._result_labels[conv_unit.key],
                        ],
                    ))
            boxes[cat] = toga.Box(
                style=Pack(direction=COLUMN, gap=4, margin=5), children=children
            )
        return boxes

    def _build_input_form(self) -> toga.Box:
        # Cost input row
        item_cost_input_label = toga.Label("Item Cost: ", style=TEXT_STYLE)
        self.currency_input = toga.TextInput(
            value="$", style=Pack(width=50, margin=(0, 5))
        )
        self.item_cost_input = toga.NumberInput(
            min=0, value=1, step=0.01, style=TEXT_STYLE
        )
        self.unit_selection = toga.Selection(
            items=SELECTION_LIST, accessor="name", style=Pack(margin=(0, 5), flex=50)
        )
        input_row = toga.Box(
            style=Pack(direction=ROW, flex=25),
            children=[
                item_cost_input_label,
                self.currency_input,
                self.item_cost_input,
                self.unit_selection,
            ],
        )

        # Category filter
        self.food_items = load_food_densities()
        self._food_categories = get_food_categories(self.food_items)
        category_label = toga.Label("Category: ", style=TEXT_STYLE)
        self.category_selection = toga.Selection(
            items=[ALL_CATEGORIES_LABEL] + self._food_categories,
            on_change=self._on_category_change,
            style=Pack(margin=(0, 5), flex=50),
        )
        category_row = toga.Box(children=[category_label, self.category_selection])

        # Food item dropdown
        item_selection_label = toga.Label("Food Item: ", style=TEXT_STYLE)
        self.density_item_selection = toga.Selection(
            items=self.food_items,
            accessor="food_name",
            on_change=self._on_food_select,
            style=Pack(margin=(0, 5), flex=50),
        )
        food_row = toga.Box(
            children=[item_selection_label, self.density_item_selection]
        )

        # Food info subtitle
        self._food_info_label = toga.Label("", style=FOOD_INFO_STYLE)
        self._update_food_info()

        # Comment
        comment_input_label = toga.Label("Conversion Comment:", style=TEXT_STYLE)
        self.comment_input = toga.TextInput(style=TEXT_STYLE)
        self.comment_input.style.flex = 200
        comment_row = toga.Box(children=[comment_input_label, self.comment_input])

        return toga.Box(
            style=INPUT_SECTION_STYLE,
            children=[
                toga.Label("Input", style=SECTION_HEADING_STYLE),
                input_row,
                category_row,
                food_row,
                self._food_info_label,
                comment_row,
            ],
        )

    def _build_history_panel(self) -> toga.Box:
        data = load_history(self._history_path)
        history_data = [entry.to_dict() for entry in data]
        self.history_item_selection = toga.Table(
            headings=["Unit", "Quantity", "Food Item", "Comment"],
            accessors=["unit_selection", "unit_value", "density_selection", "comment"],
            style=Pack(flex=150),
            multiple_select=False,
            on_select=self._on_history_select,
            data=history_data,
        )
        self._history_empty_label = toga.Label(
            "No conversions yet. Press Convert to get started.",
            style=EMPTY_STATE_STYLE,
        )
        if history_data:
            self._history_empty_label.style.display = "none"

        return toga.Box(
            children=[
                toga.Label("History", style=SECTION_HEADING_STYLE),
                self._history_empty_label,
                self.history_item_selection,
            ],
            style=Pack(direction=COLUMN, flex=150),
        )

    def _build_toolbar_commands(self) -> None:
        warning_icon = r"resources/warning.png"
        clear_history_command = toga.Command(
            self.clear_history,
            text="Clear Conversion History",
            icon=warning_icon,
            group=toga.Group.EDIT,
        )
        clear_input_command = toga.Command(
            self.reset_input,
            text="Clear Input",
            icon=warning_icon,
            group=toga.Group.EDIT,
        )
        self.commands.add(clear_history_command)
        self.commands.add(clear_input_command)
        if is_android():
            export_data_command = toga.Command(
                self.export_data,
                text="Export History",
                icon=warning_icon,
                group=toga.Group.FILE,
            )
            import_data_command = toga.Command(
                self.import_data,
                text="Import History",
                icon=warning_icon,
                group=toga.Group.FILE,
            )
            self.commands.add(import_data_command)
            self.commands.add(export_data_command)

    def startup(self) -> None:
        self._history_path = get_history_path(self.paths.data)
        ensure_data_directory(self._history_path)

        self._create_result_labels()
        category_boxes = self._build_category_boxes()
        input_section = self._build_input_form()

        # Price per unit hero display
        self._price_per_unit_label = toga.Label("", style=PPU_STYLE)
        ppu_section = toga.Box(
            style=Pack(direction=COLUMN, align_items=CENTER, margin=(5, 0)),
            children=[
                toga.Label("Price Per Unit", style=SECTION_HEADING_STYLE),
                self._price_per_unit_label,
            ],
        )

        mass_icon = r"resources/mass.png"
        volume_icon = r"resources/volume.png"
        clear_icon = r"resources/clear.png"

        option_box = toga.OptionContainer(
            style=Pack(flex=400),
            content=[
                toga.OptionItem("Metric Mass", category_boxes[UnitCategory.METRIC_MASS], icon=mass_icon),
                toga.OptionItem("American Mass", category_boxes[UnitCategory.IMPERIAL_MASS], icon=mass_icon),
                toga.OptionItem("Metric Volume", category_boxes[UnitCategory.METRIC_VOLUME], icon=volume_icon),
                toga.OptionItem("American Volume", category_boxes[UnitCategory.IMPERIAL_VOLUME], icon=volume_icon),
            ],
        )

        convert_button = toga.Button(
            "Convert", on_press=self.perform_conversion, style=BUTTON_STYLE
        )
        self.clear_button = toga.Button(
            icon=clear_icon, on_press=self.reset_input, style=BUTTON_STYLE
        )
        self.clear_button.style.width = 75
        self.clear_button.style.visibility = "hidden"
        action_row = toga.Box(children=[convert_button, self.clear_button])

        main_box = toga.Box(
            style=Pack(direction=COLUMN),
            children=[
                input_section,
                toga.Divider(),
                action_row,
                toga.Divider(),
                ppu_section,
                toga.Divider(),
                toga.Label("Results", style=SECTION_HEADING_STYLE),
                option_box,
                toga.Divider(),
                self._build_history_panel(),
            ],
        )

        scroll = toga.ScrollContainer(content=main_box, style=Pack(flex=1))
        self.main_window = toga.MainWindow(
            title="Food Price Comparison", size=(1024, 1024)
        )
        self.main_window.content = scroll
        self._build_toolbar_commands()

    # --- Event handlers ---

    def _on_category_change(self, widget) -> None:
        selected = self.category_selection.value
        if selected == ALL_CATEGORIES_LABEL:
            filtered = self.food_items
        else:
            filtered = [item for item in self.food_items if item.category == selected]
        self.density_item_selection.items = filtered

    def _on_food_select(self, widget) -> None:
        self._update_food_info()

    def _get_selected_food(self):
        """Look up the full FoodDensity object for the current selection."""
        sel = self.density_item_selection.value
        if not sel:
            return None
        name = str(sel.food_name) if hasattr(sel, "food_name") else str(sel)
        for item in self.food_items:
            if item.food_name == name:
                return item
        return None

    def _update_food_info(self) -> None:
        item = self._get_selected_food()
        if item:
            parts = []
            if item.category:
                parts.append(f"Category: {item.category}")
            parts.append(f"Density: {item.g_ml} g/ml")
            if item.biblio_id:
                parts.append(f"Source: {item.biblio_id}")
            self._food_info_label.text = " | ".join(parts)
        else:
            self._food_info_label.text = ""

    def perform_conversion(self, *args) -> None:
        try:
            price = float(self.item_cost_input.value)
            density_g_per_ml = float(self.density_item_selection.value.g_ml)
            mass = self.unit_selection.value.mass
        except (ValueError, TypeError, AttributeError):
            return

        self._save_current_to_history()

        currency = self.currency_input.value or ""
        results = convert_all_prices(price, mass, density_g_per_ml, CONVERSION_UNITS)
        price_per_unit = round(price / mass, 5)
        self._price_per_unit_label.text = f"{currency}{price_per_unit}"

        for conv_unit in CONVERSION_UNITS:
            self._result_labels[conv_unit.key].text = f"{currency}{results[conv_unit.key]}"

        self.clear_button.style.visibility = "visible"

    def _save_current_to_history(self) -> None:
        entries = self._get_history_entries()
        entries = cleanup_history(
            entries,
            current_density=self.density_item_selection.value.food_name,
            current_unit=self.unit_selection.value.name,
            current_value=self.item_cost_input.value,
        )
        entries.append(HistoryEntry(
            unit_selection=self.unit_selection.value.name,
            unit_value=self.item_cost_input.value,
            density_selection=self.density_item_selection.value.food_name,
            comment=self.comment_input.value,
        ))
        self._replace_history_table_data(entries)
        save_history(entries, self._history_path)

    def _get_history_entries(self) -> list[HistoryEntry]:
        entries = []
        for row in self.history_item_selection.data:
            entries.append(HistoryEntry(
                unit_selection=row.unit_selection,
                unit_value=row.unit_value,
                density_selection=row.density_selection,
                comment=row.comment,
            ))
        return entries

    def _replace_history_table_data(self, entries: list[HistoryEntry]) -> None:
        while self.history_item_selection.data:
            self.history_item_selection.data.remove(self.history_item_selection.data[0])
        for entry in entries:
            self.history_item_selection.data.append(entry.to_dict())
        if entries:
            self._history_empty_label.style.display = "none"

    def _clean_result_labels(self) -> None:
        for conv_unit in CONVERSION_UNITS:
            self._result_labels[conv_unit.key].text = ""

    def reset_input(self, button) -> None:
        self.item_cost_input.value = 1
        self.unit_selection.value = self.unit_selection.items[0]
        self.category_selection.value = ALL_CATEGORIES_LABEL
        self.density_item_selection.value = self.density_item_selection.items[0]
        self._clean_result_labels()
        self._price_per_unit_label.text = ""
        self.clear_button.style.visibility = "hidden"

    def clear_history(self, button) -> None:
        if self._history_path.is_file():
            self._history_path.unlink()
        while self.history_item_selection.data:
            self.history_item_selection.data.remove(self.history_item_selection.data[0])
        self._history_empty_label.style.display = "pack"

    def _on_history_select(self, *args) -> None:
        if not self.history_item_selection.selection:
            return
        if not self.history_item_selection.selection.unit_selection:
            return

        sel = self.history_item_selection.selection
        self.item_cost_input.value = sel.unit_value

        for index, value in enumerate(SELECTION_LIST):
            if value["name"] == str(sel.unit_selection):
                self.unit_selection.value = self.unit_selection.items[index]
                break

        # Reset category filter to show all items before restoring food selection
        self.category_selection.value = ALL_CATEGORIES_LABEL
        for index, item in enumerate(self.food_items):
            if item.food_name == str(sel.density_selection):
                self.density_item_selection.value = self.density_item_selection.items[index]
                break

        self.comment_input.value = sel.comment
        self.perform_conversion()

    async def import_data(self, widget) -> None:
        if not is_android():
            return
        from android.content import Intent

        file_chose = Intent(Intent.ACTION_GET_CONTENT)
        file_chose.addCategory(Intent.CATEGORY_OPENABLE)
        file_chose.setType("*/*")
        results = await self._impl.intent_result(
            Intent.createChooser(file_chose, "Choose a file")
        )
        data = results["resultData"].getData()
        context = self._impl.native

        import pickle
        raw_bytes = bytes(
            context.getContentResolver().openInputStream(data).readAllBytes()
        )
        items = pickle.loads(raw_bytes)
        for item in items:
            self.history_item_selection.data.append({
                "unit_selection": item["unit_selection"],
                "unit_value": item["unit_value"],
                "density_selection": item["density_selection"],
                "comment": item["comment"],
            })
        self._history_empty_label.style.display = "none"

    async def export_data(self, widget) -> None:
        if not is_android():
            return
        from android.content import Intent

        epoch_time = str(int(time.time()))
        context = self._impl.native
        default_file_name = f"quick_food_price_comparison_{epoch_time}.pickle"
        intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/octet-stream")
        intent.putExtra(Intent.EXTRA_TITLE, default_file_name)

        results = await self._impl.intent_result(intent)
        uri = results["resultData"].getData()

        output_stream = context.getContentResolver().openOutputStream(uri)
        exported_bytes = history_to_bytes(self._get_history_entries())
        output_stream.write(exported_bytes)
        output_stream.close()


# Backward compat alias
main_app = MainApp


def main():
    return MainApp(
        FORMAL_NAME, FULL_NAME, icon="resources/quickfoodpricecomparison.ico"
    )


if __name__ == "__main__":
    main().main_loop()
