import { describe, expect, it } from 'vitest';
import { hasWebGL } from '../webgl';

describe('webgl', () => {
  it('detects availability', () => {
    expect(typeof hasWebGL()).toBe('boolean');
  });
});
