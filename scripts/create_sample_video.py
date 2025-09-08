#!/usr/bin/env python3
"""Generate a short synthetic 1080p MP4 clip for local testing.

The output path is controlled by the ``EXAMPLE_VIDEO`` environment variable
and defaults to ``examples/sample_1080p.mp4``.  The script requires ``numpy``
and ``imageio[ffmpeg]``.
"""
import os
import numpy as np
import imageio

FPS = 24
DURATION = 5  # seconds
HEIGHT = 1080
WIDTH = 1920


def main() -> None:
    path = os.getenv("EXAMPLE_VIDEO", "examples/sample_1080p.mp4")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with imageio.get_writer(path, fps=FPS, codec="libx264") as writer:
        for i in range(FPS * DURATION):
            frame = np.zeros((HEIGHT, WIDTH, 3), dtype=np.uint8)
            frame[..., 0] = (i * 5) % 256
            frame[..., 1] = (i * 2) % 256
            frame[..., 2] = (i * 3) % 256
            writer.append_data(frame)
    print(f"Created {path}")


if __name__ == "__main__":
    main()
