# Test Video Editing App

Anime Video editing program that changes the style of the anime style to a new one.

## Pre-trained models

Run `python scripts/download_models.py` to fetch a small AnimeGANv2 ONNX model for local experiments.

The model is saved under `models/` by default. Set the `MODEL_DIR` environment variable to change the download location.

## Example video

Generate a short synthetic 1080p clip by running `python scripts/create_sample_video.py`.
The script requires the `numpy` and `imageio[ffmpeg]` Python packages.
The video is saved as `examples/sample_1080p.mp4` by default; set `EXAMPLE_VIDEO`
to change the output path or to point to a different input file.

## Environment variables

| Variable       | Description                                 | Default                     |
| -------------- | ------------------------------------------- | --------------------------- |
| `MODEL_DIR`    | Directory where ONNX models are stored      | `models/`                   |
| `EXAMPLE_VIDEO`| Path to input video for local testing       | `examples/sample_1080p.mp4` |

