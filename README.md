# Test Video Editing App

Anime video editing stack that stylizes frames with AnimeGANv2 and related models.

## Overview

This repository contains a full-stack sample project that:

- serves a Vite/TypeScript **frontend** for job submission,
- exposes a Spring Boot **API** for authentication and job management,
- runs a Spring Boot **worker** that processes queued video jobs, and
- relies on **Redis** for messaging and **MinIO** for object storage.

## Architecture

```
frontend → API → Redis queue → worker → MinIO
```

The frontend obtains a JWT token from the API, uploads video files to MinIO using
presigned URLs, and creates jobs. Jobs are enqueued in Redis and consumed by the
worker, which applies style transfer, optical-flow warping, and optional upscaling
before writing results back to MinIO.

## Environment variables

| Variable                | Description                                  | Default                |
| ----------------------- | -------------------------------------------- | ---------------------- |
| `MODEL_DIR`             | Directory where ONNX models are stored       | `models/`              |
| `EXAMPLE_VIDEO`         | Path to input video for local testing        | `examples/sample_1080p.mp4` |
| `REDIS_HOST`            | Redis hostname used by API and worker        | `redis`                |
| `S3_ENDPOINT`           | MinIO/S3 endpoint                            | `http://minio:9000`    |
| `AWS_ACCESS_KEY_ID`     | MinIO access key                             | `minio`                |
| `AWS_SECRET_ACCESS_KEY` | MinIO secret key                             | `minio123`             |
| `MINIO_ROOT_USER`       | MinIO root username                          | `minio`                |
| `MINIO_ROOT_PASSWORD`   | MinIO root password                          | `minio123`             |

## CLI commands

| Command                                 | Purpose                                   |
| --------------------------------------- | ----------------------------------------- |
| `python scripts/download_models.py`     | Fetch small pre-trained ONNX models       |
| `python scripts/create_sample_video.py` | Generate a synthetic 1080p MP4 clip       |
| `./dev.sh`                              | Build and start the Docker stack          |
| `./e2e.sh`                              | Start stack and request a JWT token       |
| `./render.sh`                           | Run the worker container for one-off jobs |

## Running the Docker stack

```bash
./dev.sh
```

The script runs `docker compose up --build` and launches the frontend on port
`3000` and the API on `8080`.

## Running tests

- Frontend: `npm test` inside the `frontend/` folder.
- Backend API: `mvn test` inside `backend/api` *(requires internet access for Maven dependencies)*.
- Backend worker: `mvn test` inside `backend/worker` *(requires internet access)*.
- End-to-end API check: `./e2e.sh`.

## Sample render

1. Download models: `python scripts/download_models.py`.
2. Generate test clip: `python scripts/create_sample_video.py`.
3. Start the stack: `./dev.sh`.
4. Obtain a token: `curl -X POST http://localhost:8080/v1/auth/token -H 'Content-Type: application/json' -d '{"username":"test"}'`.
5. Initialize an upload to get a presigned URL: `curl -H "Authorization: Bearer $TOKEN" -X POST http://localhost:8080/v1/uploads/init -d '{"filename":"sample_1080p.mp4"}'`.
6. Upload the video to the returned URL, then create a job:
   `curl -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -X POST http://localhost:8080/v1/jobs -d '{"objectKey":"sample_1080p.mp4","superResolution":false}'`.
7. Monitor progress via `ws://localhost:8080/v1/jobs/{id}/ws`.

## Security and GPU requirements

- **Authentication**: All endpoints except `/v1/auth/token` require a valid JWT token. The signing secret in `SecurityConfig` should be replaced with a strong value in production.
- **Credentials**: MinIO credentials in `docker-compose.yml` are for local use only; change them for deployments.
- **GPU**: The worker checks for NVENC support and uses it when available. Running the stack with GPU acceleration requires an NVIDIA GPU, recent drivers, and the [NVIDIA Container Toolkit](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/overview.html).


