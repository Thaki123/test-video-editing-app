// Dynamically import ffmpeg only when needed
export async function loadFFmpeg() {
  const mod = await import('@ffmpeg/ffmpeg');
  return mod;
}

export async function convertToWebM(data: Uint8Array, name: string) {
  const { FFmpeg } = await loadFFmpeg();
  const ffmpeg = new FFmpeg({ log: false });
  await ffmpeg.load();
  await ffmpeg.writeFile(name, data);
  await ffmpeg.exec(['-i', name, '-c:v', 'libvpx', '-f', 'webm', 'out.webm']);
  return ffmpeg.readFile('out.webm');
}
