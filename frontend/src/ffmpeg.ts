// Dynamically import ffmpeg only when needed
export async function loadFFmpeg() {
  const mod = await import('@ffmpeg/ffmpeg');
  return mod;
}

export async function convertToWebM(data: Uint8Array, name: string): Promise<Uint8Array> {
  const { FFmpeg } = await loadFFmpeg();
  const ffmpeg = new FFmpeg();
  await ffmpeg.load();
  await ffmpeg.writeFile(name, data);
  await ffmpeg.exec(['-i', name, '-c:v', 'libvpx', '-f', 'webm', 'out.webm']);
  return await ffmpeg.readFile('out.webm') as Uint8Array;
}
