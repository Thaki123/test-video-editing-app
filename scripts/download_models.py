#!/usr/bin/env python3
"""Download small pre-trained ONNX models for local testing.

Models:
- AnimeGANv2 (Shinkai)

The script downloads each model into the directory specified by the
``MODEL_DIR`` environment variable (defaults to ``models/``).  URLs point to
public repositories and may need updates if those projects relocate files.
"""
import os
import pathlib
import urllib.request

MODELS = {
    # Model converted by project author
    "animeganv2.onnx": (
        "https://raw.githubusercontent.com/TachibanaYoshino/AnimeGANv2/master/"
        "pb_and_onnx_model/Shinkai_53.onnx"
    ),
}

def download(url: str, dest: pathlib.Path) -> None:
    dest.parent.mkdir(parents=True, exist_ok=True)
    if dest.exists():
        print(f"{dest.name} already exists, skipping")
        return
    print(f"Downloading {dest.name}...")
    try:
        with urllib.request.urlopen(url) as resp, open(dest, "wb") as f:
            f.write(resp.read())
    except Exception as e:
        print(f"Failed to download {dest.name}: {e}")
        return
    print(f"Saved {dest}")


def main() -> None:
    model_dir = pathlib.Path(os.getenv("MODEL_DIR", "models"))
    for name, url in MODELS.items():
        download(url, model_dir / name)


if __name__ == "__main__":
    main()
