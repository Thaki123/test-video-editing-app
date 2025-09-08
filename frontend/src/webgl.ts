export function hasWebGL(): boolean {
  if (typeof document === 'undefined') return false;
  const canvas = document.createElement('canvas');
  return !!(
    canvas.getContext('webgl') || canvas.getContext('experimental-webgl')
  );
}

export interface ShaderController {
  setUniforms(values: {
    edgeWeight?: number;
    posterize?: number;
    saturation?: number;
  }): void;
}

// Draw the video frame with additional stylization controls
export function applyShader(
  video: HTMLVideoElement,
  canvas: HTMLCanvasElement
): ShaderController | void {
  const maybeGl = canvas.getContext('webgl') as WebGLRenderingContext | null;
  if (!maybeGl) return;
  const gl: WebGLRenderingContext = maybeGl;
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;

  const program = gl.createProgram();
  if (!program) return;
  const vsrc = `
    attribute vec2 a;
    varying vec2 v;
    void main(){
      v = (a + 1.0) / 2.0;
      gl_Position = vec4(a,0,1);
    }
  `;
  const fsrc = `
    precision mediump float;
    uniform sampler2D t;
    uniform vec2 pixel;
    uniform float edgeWeight;
    uniform float posterize;
    uniform float saturation;
    varying vec2 v;
    void main(){
      vec3 luma = vec3(0.299, 0.587, 0.114);
      float tl = dot(texture2D(t, v + pixel * vec2(-1.0, -1.0)).rgb, luma);
      float tm = dot(texture2D(t, v + pixel * vec2( 0.0, -1.0)).rgb, luma);
      float tr = dot(texture2D(t, v + pixel * vec2( 1.0, -1.0)).rgb, luma);
      float ml = dot(texture2D(t, v + pixel * vec2(-1.0,  0.0)).rgb, luma);
      float mr = dot(texture2D(t, v + pixel * vec2( 1.0,  0.0)).rgb, luma);
      float bl = dot(texture2D(t, v + pixel * vec2(-1.0,  1.0)).rgb, luma);
      float bm = dot(texture2D(t, v + pixel * vec2( 0.0,  1.0)).rgb, luma);
      float br = dot(texture2D(t, v + pixel * vec2( 1.0,  1.0)).rgb, luma);

      float gx = -tl - 2.0 * ml - bl + tr + 2.0 * mr + br;
      float gy = -tl - 2.0 * tm - tr + bl + 2.0 * bm + br;
      float edge = length(vec2(gx, gy)) / 4.0;
      edge = clamp(edge, 0.0, 1.0);

      vec3 color = texture2D(t, v).rgb;
      float levels = max(posterize, 1.0);
      color = floor(color * levels) / levels;
      float gray = dot(color, luma);
      color = mix(vec3(gray), color, saturation);
      float edgeFactor = clamp(edgeWeight * edge, 0.0, 1.0);
      color = mix(color, vec3(0.0), edgeFactor);
      gl_FragColor = vec4(color, 1.0);
    }
  `;
  const vs = gl.createShader(gl.VERTEX_SHADER)!;
  gl.shaderSource(vs, vsrc);
  gl.compileShader(vs);
  const fs = gl.createShader(gl.FRAGMENT_SHADER)!;
  gl.shaderSource(fs, fsrc);
  gl.compileShader(fs);
  gl.attachShader(program, vs);
  gl.attachShader(program, fs);
  gl.linkProgram(program);
  gl.useProgram(program);

  const buffer = gl.createBuffer();
  gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
  gl.bufferData(
    gl.ARRAY_BUFFER,
    new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1]),
    gl.STATIC_DRAW
  );
  const loc = gl.getAttribLocation(program, 'a');
  gl.enableVertexAttribArray(loc);
  gl.vertexAttribPointer(loc, 2, gl.FLOAT, false, 0, 0);

  const tex = gl.createTexture();
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
  gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);

  const pixelLoc = gl.getUniformLocation(program, 'pixel');
  gl.uniform2f(pixelLoc, 1 / video.videoWidth, 1 / video.videoHeight);
  const edgeLoc = gl.getUniformLocation(program, 'edgeWeight');
  const posterizeLoc = gl.getUniformLocation(program, 'posterize');
  const saturationLoc = gl.getUniformLocation(program, 'saturation');

  const state = {
    edgeWeight: 1,
    posterize: 8,
    saturation: 1,
  };

  function setUniforms(values: {
    edgeWeight?: number;
    posterize?: number;
    saturation?: number;
  }) {
    Object.assign(state, values);
  }

  function render() {
    if (video.readyState >= 2) {
      gl.uniform1f(edgeLoc, state.edgeWeight);
      gl.uniform1f(posterizeLoc, state.posterize);
      gl.uniform1f(saturationLoc, state.saturation);
      gl.texImage2D(
        gl.TEXTURE_2D,
        0,
        gl.RGBA,
        gl.RGBA,
        gl.UNSIGNED_BYTE,
        video
      );
      gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
    }
    requestAnimationFrame(render);
  }
  render();

  return { setUniforms };
}
