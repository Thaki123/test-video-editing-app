import './style.css';
import { STYLES, applyStyle } from './presets';
import { convertToWebM } from './ffmpeg';
import { hasWebGL, applyShader } from './webgl';

const uploadInput = document.getElementById('upload') as HTMLInputElement;
const original = document.getElementById('original') as HTMLVideoElement;
const styled = document.getElementById('styled') as HTMLCanvasElement;
const styleSelect = document.getElementById('style') as HTMLSelectElement;
const startInput = document.getElementById('start') as HTMLInputElement;
const endInput = document.getElementById('end') as HTMLInputElement;
const renderBtn = document.getElementById('render') as HTMLButtonElement;
const progress = document.getElementById('progress') as HTMLProgressElement;

// Populate style options
for (const p of STYLES) {
  const opt = document.createElement('option');
  opt.value = p.id;
  opt.textContent = p.name;
  styleSelect.appendChild(opt);
}

uploadInput.addEventListener('change', () => {
  const file = uploadInput.files?.[0];
  if (!file) return;
  original.src = URL.createObjectURL(file);
  original.onloadedmetadata = () => {
    endInput.value = Math.floor(original.duration).toString();
    if (hasWebGL()) {
      applyShader(original, styled);
    }
  };
});

async function renderFrame() {
  await applyStyle(original, styled, styleSelect.value);
  progress.value = (original.currentTime / original.duration) * 100;
  if (!original.paused && !original.ended) {
    requestAnimationFrame(renderFrame);
  }
}

original.addEventListener('play', () => {
  requestAnimationFrame(renderFrame);
});
styleSelect.addEventListener('change', () => {
  if (!original.paused) {
    requestAnimationFrame(renderFrame);
  } else {
    applyStyle(original, styled, styleSelect.value);
  }
});

renderBtn.addEventListener('click', async () => {
  const file = uploadInput.files?.[0];
  if (!file) return;
  // Convert to WebM in browser
  const webm = await convertToWebM(new Uint8Array(await file.arrayBuffer()), file.name);
  const blob = new Blob([webm.buffer], { type: 'video/webm' });

  // request presigned URL
  const res = await fetch(`/api/upload-url?name=${encodeURIComponent(file.name)}`);
  const { url } = await res.json();
  await fetch(url, { method: 'PUT', body: blob });

  // create job
  const jobRes = await fetch('/api/jobs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      input: url,
      preset: styleSelect.value,
      start: Number(startInput.value),
      end: Number(endInput.value)
    })
  });
  const { ws } = await jobRes.json();
  const socket = new WebSocket(ws);
  socket.onmessage = (ev) => {
    const data = JSON.parse(ev.data);
    progress.value = data.progress;
  };
});
