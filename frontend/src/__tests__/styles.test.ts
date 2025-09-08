import { describe, expect, it } from 'vitest';
import { STYLES } from '../presets';

describe('styles', () => {
  it('includes anime style', () => {
    expect(STYLES.find(s => s.id === 'anime')).toBeTruthy();
  });
});
