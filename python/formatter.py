import os
from typing import (
    Any, Dict, List, Optional, Tuple
)

USE_COLOR = False
try:
    import colorama  # noqa
    from colorama import Fore, Style  # noqa
    USE_COLOR = True
except ImportError:
    pass

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PARENT_DIR = os.path.dirname(SCRIPT_DIR)
PS1_EXTENSION = ".ps1"
SK_DIR = os.path.join(
    PARENT_DIR, "plugins", "Skript", "scripts"
)
SK_EXTENSION = ".sk"
JAVA_DIR = os.path.join(
    PARENT_DIR, "code", "SkriptUtils"
)
JAVA_EXTENSION = ".java"

UTF_8 = "utf-8"
BOM_CHAR = "\uFEFF"

CHANGED_FILES_KEY = "changed_files"
CHANGED_LINES_KEY = "changed_lines"


class Formatter:
    def __init__(self):
        self.stats: Optional[Dict[str, Dict[str, Any]]] = None
        self.total_stats: Optional[Dict[str, Dict[str, Any]]] = None

    # Not recursive...
    @staticmethod
    def _get_ps1_tuples() -> List[Tuple[str, str]]:
        return [
            (_file, os.path.join(PARENT_DIR, _file))
            for _file in sorted(os.listdir(PARENT_DIR))
            if _file.endswith(PS1_EXTENSION)
        ]

    # Recursive...
    @staticmethod
    def _get_tuples(_dir: str, extension: str) -> List[Tuple[str, str]]:
        return [
            (_file, os.path.join(root, _file))
            for root, _, files in os.walk(_dir)
            for _file in files
            if _file.endswith(extension)
        ]

    def _get_sk_tuples(self) -> List[Tuple[str, str]]:
        return self._get_tuples(SK_DIR, SK_EXTENSION)

    def _get_java_tuples(self) -> List[Tuple[str, str]]:
        return self._get_tuples(JAVA_DIR, JAVA_EXTENSION)

    def _format_file(self, some_file: str, some_path: str) -> None:
        with open(some_path, encoding=UTF_8) as _file:
            lines = _file.read().lstrip(BOM_CHAR).splitlines()

        new_lines, changed_file = [], False
        for line in lines:
            stripped_line = line.rstrip()
            new_lines.append(stripped_line)
            if line != stripped_line:
                self.stats[some_file][CHANGED_LINES_KEY] += 1
                self.total_stats[CHANGED_LINES_KEY] += 1
                changed_file = True

        with open(some_path, "w", encoding=UTF_8) as _file:
            _file.write("\n".join(new_lines) + "\n")

        if changed_file:
            self.total_stats[CHANGED_FILES_KEY] += 1

    def _format_files(self, tuples: List[Tuple[str, str]]) -> None:
        for some_file, some_path in tuples:
            self._format_file(some_file, some_path)

    @staticmethod
    def _value_color(value: Any) -> str:
        try:
            value = int(value)
            value = (Fore.YELLOW if value else Fore.GREEN) + str(value) + Style.RESET_ALL
        except ValueError:
            pass
        return value

    def _print_files_stats(self) -> None:
        lines = []
        for some_file, _dict in self.stats.items():
            lines.append(f"  {some_file}")
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
            extension = "." + line.rsplit(".", maxsplit=1)[-1]
            color = {
                PS1_EXTENSION: Fore.CYAN,
                SK_EXTENSION: Fore.YELLOW,
                JAVA_EXTENSION: Fore.MAGENTA
            }.get(extension)

            if color:
                line = Style.BRIGHT + color + line + Style.RESET_ALL
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
        ps1_tuples = self._get_ps1_tuples()
        sk_tuples = self._get_sk_tuples()
        java_tuples = self._get_java_tuples()
        tuples = ps1_tuples + sk_tuples + java_tuples
        if not tuples:
            return

        self.stats = {
            _file: {
                CHANGED_LINES_KEY: 0
                # Expand if needed...
            }
            for (_file, _) in tuples
        }

        self.total_stats = {
            CHANGED_FILES_KEY: 0,
            CHANGED_LINES_KEY: 0
        }

        self._format_files(tuples)
        self._print_files_stats()
        print()
        self._print_total_stats()
        print()


def main() -> None:
    formatter = Formatter()
    formatter.format()


if __name__ == "__main__":
    main()
