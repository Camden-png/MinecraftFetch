from datetime import timezone
import os
import tempfile
from typing import Optional
from zoneinfo import ZoneInfo

from data_packer import PACK_DIR
from data_packer import main as data_packer_main

from dotenv import load_dotenv  # noqa
import dropbox  # noqa
from dropbox.exceptions import ApiError  # noqa

PACK_ZIP = "pack.zip"
SLASH_PACK_ZIP = f"/{PACK_ZIP}"


def main() -> None:
    load_dotenv()

    dbx = dropbox.Dropbox(
        os.environ["APP_ACCESS_TOKEN"]
    )

    data_packer_main()

    with open(f"{PACK_DIR}.zip", "rb") as pack_zip:
        dbx.files_upload(
            pack_zip.read(), SLASH_PACK_ZIP, mode=dropbox.files.WriteMode.overwrite
        )

    print()

    print(f"Updating `{PACK_ZIP}`...")

    # Read files
    response = dbx.files_list_folder(path="")
    for entry in response.entries:
        update_time = (
            entry.server_modified
            .replace(tzinfo=timezone.utc)
            .astimezone()
        )
        update_time = update_time.strftime("%m/%d/%Y %I:%M %p")

        url: Optional[str] = None
        if entry.name == PACK_ZIP:
            try:
                url = dbx.sharing_create_shared_link_with_settings(SLASH_PACK_ZIP).url
            except ApiError:
                url = dbx.sharing_list_shared_links(path=SLASH_PACK_ZIP).links[0].url
            except:  # noqa
                pass
            url = url[:-1] + "1" if url else url  # Make a direct download...

        print((
            f"* Found `{entry.name}` @ {update_time} " +
            f"with url {url}" if url else ""
        ).strip())


if __name__ == "__main__":
    main()
