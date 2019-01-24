#ifdef GL_ES
  #define LOWP lowp
  precision mediump float;
#else
  #define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_blurTexCoords[14];
uniform sampler2D u_texture;

uniform float u_blur;
uniform float u_tiltshiftY;
uniform float u_tiltshiftX;
uniform float u_tiltshiftYPos;

#define tsyp (u_tiltshiftYPos <= 0. ? abs(uv.y-.3) : abs(uv.y-u_tiltshiftYPos))
#define tsxp abs(uv.x-.5)

vec4 blur(vec4 color) {
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 0]) * .0044299121055113265;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 1]) * .00895781211794;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 2]) * .0215963866053;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 3]) * .0443683338718;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 4]) * .0776744219933;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 5]) * .115876621105;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 6]) * .147308056121;
  color += v_color * texture2D(u_texture, v_texCoords        ) * .159576912161;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 7]) * .147308056121;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 8]) * .115876621105;
  color += v_color * texture2D(u_texture, v_blurTexCoords[ 9]) * .0776744219933;
  color += v_color * texture2D(u_texture, v_blurTexCoords[10]) * .0443683338718;
  color += v_color * texture2D(u_texture, v_blurTexCoords[11]) * .0215963866053;
  color += v_color * texture2D(u_texture, v_blurTexCoords[12]) * .00895781211794;
  color += v_color * texture2D(u_texture, v_blurTexCoords[13]) * .0044299121055113265;
  return color;
}

void main() {
  vec2 uv = v_texCoords;
  vec4 color = vec4(0.);
  if (u_blur + u_tiltshiftY + u_tiltshiftX > 0.) {
    float a = u_tiltshiftX + u_tiltshiftY > 0. ? u_blur + 
        (clamp(u_tiltshiftY - u_blur, 0., 1.)*tsyp) + 
        (clamp(u_tiltshiftX - u_blur, 0., 1.)*tsxp) : u_blur;
    color = vec4(blur(color).rgb, 1. - (a-u_blur));
  }
  gl_FragColor = color;
}