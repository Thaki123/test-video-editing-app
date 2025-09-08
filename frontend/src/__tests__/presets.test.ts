import { describe, expect, it } from 'vitest';
import { PRESETS } from '../presets';

describe('presets', () => {
  it('includes at least nine presets', () => {
    expect(PRESETS.length).toBeGreaterThanOrEqual(9);
  });
});
