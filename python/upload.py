import os
import tempfile

from dotenv import load_dotenv  # noqa
import dropbox  # noqa


def main() -> None:
    load_dotenv()

    dbx = dropbox.Dropbox(
        os.environ["APP_ACCESS_TOKEN"]
    )

    # Upload file
    with tempfile.NamedTemporaryFile("w+") as temp_file:
        temp_file.write("Hello world\n")
        temp_file.flush()
        temp_file.seek(0)
        dbx.files_upload(temp_file.read().encode(), "/" + "test.txt")

    # Read files
    response = dbx.files_list_folder(path="")
    for entry in response.entries:
        print(entry.name)


if __name__ == "__main__":
    main()
