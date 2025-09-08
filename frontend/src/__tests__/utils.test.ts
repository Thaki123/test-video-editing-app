import { describe, expect, it } from 'vitest';
import { formatTime, getExtension } from '../utils';

describe('utils', () => {
  it('formats time', () => {
    expect(formatTime(65)).toBe('01:05');
    expect(formatTime(3661)).toBe('01:01:01');
  });
  it('parses extension', () => {
    expect(getExtension('video.MP4')).toBe('mp4');
    expect(getExtension('noext')).toBe('');
  });
});
