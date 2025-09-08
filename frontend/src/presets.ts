export interface Preset {
  id: string;
  name: string;
  filter: string;
}

// Simple CSS filter based presets
export const PRESETS: Preset[] = [
  { id: 'none', name: 'None', filter: 'none' },
  { id: 'bright', name: 'Bright', filter: 'brightness(1.2)' },
  { id: 'contrast', name: 'Contrast', filter: 'contrast(1.4)' },
  { id: 'sepia', name: 'Sepia', filter: 'sepia(1)' },
  { id: 'invert', name: 'Invert', filter: 'invert(1)' },
  { id: 'saturate', name: 'Saturate', filter: 'saturate(2)' },
  { id: 'huerotate', name: 'Hue Rotate', filter: 'hue-rotate(90deg)' },
  { id: 'blur', name: 'Blur', filter: 'blur(2px)' },
  { id: 'grayscale', name: 'Gray', filter: 'grayscale(1)' },
];

export function applyPreset(element: HTMLElement, presetId: string) {
  const preset = PRESETS.find(p => p.id === presetId);
  element.style.filter = preset ? preset.filter : 'none';
}
