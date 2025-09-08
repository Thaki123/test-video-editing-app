export function hasWebGL(): boolean {
  if (typeof document === 'undefined') return false;
  const canvas = document.createElement('canvas');
  return !!(
    canvas.getContext('webgl') || canvas.getContext('experimental-webgl')
  );
}

// Minimal shader that simply draws the video frame
export function applyShader(video: HTMLVideoElement, canvas: HTMLCanvasElement) {
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
    varying vec2 v;
    void main(){gl_FragColor = texture2D(t,v);}
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
  gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1,-1, 1,-1, -1,1, 1,1]), gl.STATIC_DRAW);
  const loc = gl.getAttribLocation(program, 'a');
  gl.enableVertexAttribArray(loc);
  gl.vertexAttribPointer(loc, 2, gl.FLOAT, false, 0, 0);
  const tex = gl.createTexture();
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
  function render() {
    if (video.readyState >= 2) {
      gl.texImage2D(gl.TEXTURE_2D,0,gl.RGBA,gl.RGBA,gl.UNSIGNED_BYTE,video);
      gl.drawArrays(gl.TRIANGLE_STRIP,0,4);
    }
    requestAnimationFrame(render);
  }
  render();
}
