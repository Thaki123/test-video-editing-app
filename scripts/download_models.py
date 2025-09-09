#!/usr/bin/env python3
"""Download small pre-trained ONNX models for local testing.

Models:
- AnimeGANv2 (Shinkai)
- Fast Neural Style "Candy" (manga)
- Fast Neural Style "Rain Princess" (watercolor)
- Fast Neural Style "Udnie" (pencil sketch)

The script downloads each model into the directory specified by the
``MODEL_DIR`` environment variable (defaults to ``models/``).  URLs point to
public repositories and may need updates if those projects relocate files.
"""
import os
import pathlib
import urllib.request

MODELS = {
    # Anime style transfer
    "anime.onnx": (
        "https://raw.githubusercontent.com/TachibanaYoshino/AnimeGANv2/master/"
        "pb_and_onnx_model/Shinkai_53.onnx"
    ),
    # Additional style presets from the ONNX model zoo
    "manga.onnx": (
        "https://github.com/onnx/models/raw/main/vision/style_transfer/fast_neural_style/model/candy-9.onnx"
    ),
    "watercolor.onnx": (
        "https://github.com/onnx/models/raw/main/vision/style_transfer/fast_neural_style/model/rain-princess-9.onnx"
    ),
    "pencil.onnx": (
        "https://github.com/onnx/models/raw/main/vision/style_transfer/fast_neural_style/model/udnie-9.onnx"
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
