import os
from typing import (
    Any, Dict, List, Tuple, Optional
)

USE_COLOR = False
try:
    import colorama  # noqa
    from colorama import Fore, Style  # noqa
    USE_COLOR = True
except ImportError:
    pass

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
SK_DIR = os.path.join(
    os.path.dirname(SCRIPT_DIR),
    "plugins", "Skript", "scripts"
)
SK_EXTENSION = ".sk"

CHANGED_FILES_KEY = "changed_files"
CHANGED_LINES_KEY = "changed_lines"


class Formatter:
    def __init__(self):
        self.stats: Optional[Dict[str, Dict[str, Any]]] = None
        self.total_stats: Optional[Dict[str, Dict[str, Any]]] = None

    @staticmethod
    def _get_sk_tuples() -> List[Tuple[str, str]]:
        return [
            (_file, os.path.join(SK_DIR, _file))
            for _file in sorted(os.listdir(SK_DIR))
            if _file.endswith(SK_EXTENSION)
        ]

    def _format_sk_file(self, sk_file: str, sk_path: str) -> None:
        with open(sk_path) as _file:
            lines = _file.read().splitlines()

        new_lines, changed_file = [], False
        for line in lines:
            stripped_line = line.rstrip()
            new_lines.append(stripped_line)
            if line != stripped_line:
                self.stats[sk_file][CHANGED_LINES_KEY] += 1
                self.total_stats[CHANGED_LINES_KEY] += 1
                changed_file = True

        with open(sk_path, "w") as _file:
            _file.write("\n".join(new_lines) + "\n")

        if changed_file:
            self.total_stats[CHANGED_FILES_KEY] += 1

    def _format_sk_files(self, sk_tuples: List[Tuple[str, str]]) -> None:
        for sk_file, sk_path in sk_tuples:
            self._format_sk_file(sk_file, sk_path)

    @staticmethod
    def _value_color(value: Any) -> str:
        try:
            value = int(value)
            value = (Fore.YELLOW if value else Fore.GREEN) + str(value) + Style.RESET_ALL
        except ValueError:
            pass
        return value

    def _print_sk_files_stats(self) -> None:
        lines = []
        for sk_file, _dict in self.stats.items():
            lines.append(f"  {sk_file}")
            for key, value in _dict.items():
                key = " ".join(key.split("_"))
                lines.append(f"  ∙ {key}: {value}")
            lines.append("")

        greatest_length = max(len(line) for line in lines)
        bracket = "=" * (greatest_length + 2)

        for i, line in enumerate(lines):
            is_empty = line == ""
            if i == 0 or is_empty:
                _bracket = Style.DIM + bracket + Style.RESET_ALL if USE_COLOR else bracket
                print(_bracket)
                if is_empty:
                    continue

            if not USE_COLOR:
                print(line)
                continue

            # Using colorama...
            if line.endswith(SK_EXTENSION):
                line = Style.BRIGHT + Fore.YELLOW + line + Style.RESET_ALL

            elif "∙" in line:
                split_line = line.rsplit(":", 1)
                if len(split_line) != 2:
                    raise RuntimeError("Unknown issue!")
                key, value = split_line
                value = self._value_color(value)
                line = f"{key}: {value}"

            print(line)

    def _print_total_stats(self) -> None:
        for key, value in self.total_stats.items():
            key = " ".join(["Total"] + key.split("_"))
            value = self._value_color(value) if USE_COLOR else value
            print(f"✧ {key}: {value}")

    def format(self) -> None:
        sk_tuples = self._get_sk_tuples()
        if not sk_tuples:
            return

        self.stats = {
            sk_file: {
                CHANGED_LINES_KEY: 0
                # Expand if needed...
            }
            for (sk_file, _) in sk_tuples
        }

        self.total_stats = {
            CHANGED_FILES_KEY: 0,
            CHANGED_LINES_KEY: 0
        }

        self._format_sk_files(sk_tuples)
        self._print_sk_files_stats()
        print()
        self._print_total_stats()
        print()


def main() -> None:
    formatter = Formatter()
    formatter.format()


if __name__ == "__main__":
    main()
