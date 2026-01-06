import json
import os
import shlex
import shutil
import subprocess
from typing import (
    Any, Callable, Dict, List
)

CUSTOM = "custom"

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
INPUT_SOUNDS_DIR = os.path.join(SCRIPT_DIR, "sounds")

ROOT_DIR = os.path.dirname(SCRIPT_DIR)
PACK_DIR = os.path.join(ROOT_DIR, "pack")
MINECRAFT_DIR = os.path.join(PACK_DIR, "assets", "minecraft")
OUTPUT_SOUNDS_DIR = os.path.join(MINECRAFT_DIR, "sounds")

SEPERATOR = os.path.sep
METADATA_JSON = "metadata.json"

OGG_EXTENSION = ".ogg"
AUDIO_EXTENSIONS = [".mp3", ".wav", OGG_EXTENSION]

MCMETA = {
  "pack": {
    "pack_format": 64,  # https://minecraft.wiki/w/pack_format
    "description": "Custom audio for Fetch!"
  }
}

SANITY_CHECK = "ffmpeg version"
RESAMPLE_CMD = 'ffmpeg -i "{input}" -vn -c:a libvorbis -ar 44100 "{output}" -y'  # noqa


def dumps(_input: Any, **kwargs) -> str:
    json_str = json.dumps(_input, indent=2, ensure_ascii=False)
    return json_str.replace(r"\\", "/")


def get_metadata_paths(_dir: str = INPUT_SOUNDS_DIR) -> List[str]:
    return _get_paths(
        _dir, lambda _file: _file == METADATA_JSON
    )


def get_audio_paths(_dir: str = INPUT_SOUNDS_DIR) -> List[str]:
    return _get_paths(
        _dir, lambda _file: os.path.splitext(_file)[1] in AUDIO_EXTENSIONS
    )


def _get_paths(_dir: str, predicate: Callable[[str], bool]) -> List[str]:
    paths = []
    for i, (root, _, files) in enumerate(os.walk(_dir)):
        if i > 1:
            break
        for _file in files:
            if predicate(_file):
                paths.append(os.path.join(root, _file))
    return paths


def _run_command(command: str) -> str:
    process = subprocess.run(
        shlex.split(command),
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )
    stdout = process.stdout.decode().strip()
    stderr = process.stderr.decode().strip()
    return stdout or stderr


class DataPacker:
    def pack(self) -> None:
        metadata_dict = self._get_metadata_dict()
        audio_dict = self._get_audio_dict(metadata_dict)

        # Print `audio_dict`
        print(
            "Music/sounds: ...\n" +
            dumps(audio_dict, indent=2, ensure_ascii=False)
        )

        sounds_dict = {}
        for key, kwargs in audio_dict.items():
            _dir, file_name = key.split(SEPERATOR)
            file_name = os.path.splitext(file_name)[0] + OGG_EXTENSION
            _dir = os.path.join(_dir, CUSTOM, file_name)

            # Resample audio files
            self._resample(
                key,
                os.path.join(INPUT_SOUNDS_DIR, key),
                os.path.join(OUTPUT_SOUNDS_DIR, _dir)
            )

            # Create `sounds.json`
            _key = os.path.splitext(
                _dir.replace(SEPERATOR, ".")
            )[0]
            overrides_dict = audio_dict[key]
            is_music = _dir.startswith("music")
            sounds_dict[_key] = {
                "category": overrides_dict.get("category") or ("music" if is_music else "master"),
                "sounds": [{
                    "name": os.path.splitext(_dir)[0],
                    "volume": overrides_dict.get("volume", 1.0)
                }]
            }

        print()

        # Print `sounds.json`
        json_str = dumps(sounds_dict, indent=2, ensure_ascii=False)
        print(
            "Sounds.json: ...\n" +
            dumps(sounds_dict, indent=2, ensure_ascii=False)
        )

        with open(os.path.join(MINECRAFT_DIR, "sounds.json"), "w") as json_file:
            json_file.write(json_str)

    @staticmethod
    def _get_metadata_dict() -> Dict[str, Dict[str, Any]]:
        metadata_dict = {}
        for metadata_path in get_metadata_paths():
            print(metadata_path)
            type_dir = metadata_path.split(SEPERATOR)[-2]
            with open(metadata_path) as json_file:
                data = json.load(json_file)
                for key, value in data.items():
                    _key = os.path.join(type_dir, key)
                    metadata_dict[_key] = value

        return metadata_dict

    @staticmethod
    def _get_audio_dict(
            metadata_dict: Dict[str, Dict[str, Any]]
    ) -> Dict[str, Dict[str, Any]]:
        audio_dict = {}
        for audio_path in get_audio_paths():
            split_path = audio_path.rsplit(SEPERATOR, maxsplit=2)

            key = SEPERATOR.join(split_path[1:])
            metadata = metadata_dict.get(key, {})
            audio_dict[key] = metadata

        return audio_dict

    @staticmethod
    def _resample(identifier: str, _input: str, output: str) -> None:
        _dir = os.path.dirname(output)
        os.makedirs(_dir, exist_ok=True)

        cmd = RESAMPLE_CMD.format(
            input=_input,
            output=output
        )

        cmd_output = _run_command(cmd)
        if (
            not cmd_output.startswith(SANITY_CHECK) or "error" in cmd_output.lower()
        ):
            trace = cmd_output.splitlines()[-5:]
            raise RuntimeError(f"Failed to resample '{identifier}'! Trace: '{trace}'")


def main() -> None:
    output = _run_command("ffmpeg --version")
    if not output.startswith(SANITY_CHECK):
        raise RuntimeError("`ffmpeg` is not installed!")

    shutil.rmtree(PACK_DIR, ignore_errors=True)
    os.makedirs(PACK_DIR, exist_ok=True)

    # Create `mcmeta`
    mcmeta_path = os.path.join(PACK_DIR, "pack.mcmeta")
    with open(mcmeta_path, "w") as mcmeta_file:
        json.dump(
            MCMETA, mcmeta_file, indent=2, ensure_ascii=False
        )

    # Create `pack.png`
    icon_path = os.path.join(ROOT_DIR, "server-icon.png")
    if os.path.isfile(icon_path):
        shutil.copy(icon_path, os.path.join(PACK_DIR, "pack.png"))

    # Resample audio files & pack into a datapack...
    data_packer = DataPacker()
    data_packer.pack()

    # Create zip...
    shutil.make_archive("pack", "zip", PACK_DIR)


if __name__ == "__main__":
    main()
