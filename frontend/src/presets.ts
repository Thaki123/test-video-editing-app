import * as ort from 'onnxruntime-web';

export interface Style {
  id: string;
  name: string;
}

// Available styling options driven by ML models
export const STYLES: Style[] = [
  { id: 'none', name: 'None' },
  { id: 'anime', name: 'AnimeGAN' }
];

let animeSession: ort.InferenceSession | null = null;

async function ensureAnimeSession() {
  if (!animeSession) {
    // Use CDN for wasm assets
    ort.env.wasm.wasmPaths = 'https://cdn.jsdelivr.net/npm/onnxruntime-web@1.19.0/dist/';
    animeSession = await ort.InferenceSession.create('/models/animeganv2.onnx');
  }
}

export async function applyStyle(
  src: HTMLVideoElement,
  canvas: HTMLCanvasElement,
  styleId: string
) {
  const ctx = canvas.getContext('2d');
  if (!ctx) return;
  const { videoWidth: vw, videoHeight: vh } = src;
  canvas.width = vw;
  canvas.height = vh;

  // Draw original frame first
  ctx.drawImage(src, 0, 0, vw, vh);
  if (styleId === 'none') {
    return;
  }

  await ensureAnimeSession();
  const imageData = ctx.getImageData(0, 0, vw, vh);
  const input = preprocess(imageData);
  const feeds: Record<string, ort.Tensor> = {};
  feeds[animeSession!.inputNames[0]] = new ort.Tensor('float32', input, [1, 3, vh, vw]);
  const results = await animeSession!.run(feeds);
  const output = results[animeSession!.outputNames[0]];
  const outData = postprocess(output.data as Float32Array, vw, vh);
  const outImageData = new ImageData(outData, vw, vh);
  ctx.putImageData(outImageData, 0, 0);
}

function preprocess(imageData: ImageData): Float32Array {
  const { data, width, height } = imageData;
  const float = new Float32Array(width * height * 3);
  for (let i = 0; i < width * height; i++) {
    const r = data[i * 4] / 255;
    const g = data[i * 4 + 1] / 255;
    const b = data[i * 4 + 2] / 255;
    float[i] = r;
    float[i + width * height] = g;
    float[i + 2 * width * height] = b;
  }
  return float;
}

function postprocess(data: Float32Array, width: number, height: number): Uint8ClampedArray {
  const out = new Uint8ClampedArray(width * height * 4);
  for (let i = 0; i < width * height; i++) {
    const r = Math.max(0, Math.min(255, data[i] * 255));
    const g = Math.max(0, Math.min(255, data[i + width * height] * 255));
    const b = Math.max(0, Math.min(255, data[i + 2 * width * height] * 255));
    out[i * 4] = r;
    out[i * 4 + 1] = g;
    out[i * 4 + 2] = b;
    out[i * 4 + 3] = 255;
  }
  return out;
}
