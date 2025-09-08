import { describe, expect, it } from 'vitest';
import { loadFFmpeg } from '../ffmpeg';

describe('ffmpeg', () => {
  it('dynamically imports module', async () => {
    const mod = await loadFFmpeg();
    expect(mod).toHaveProperty('FFmpeg');
  });
});
