/**
 * Copyright 2012 xissburg
 */

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

void main() {
  vec4 color = vec4(0.);
  color += texture2D(u_texture, v_blurTexCoords[ 0]) * .0044299121055113265;
  color += texture2D(u_texture, v_blurTexCoords[ 1]) * .00895781211794;
  color += texture2D(u_texture, v_blurTexCoords[ 2]) * .0215963866053;
  color += texture2D(u_texture, v_blurTexCoords[ 3]) * .0443683338718;
  color += texture2D(u_texture, v_blurTexCoords[ 4]) * .0776744219933;
  color += texture2D(u_texture, v_blurTexCoords[ 5]) * .115876621105;
  color += texture2D(u_texture, v_blurTexCoords[ 6]) * .147308056121;
  color += texture2D(u_texture, v_texCoords        ) * .159576912161;
  color += texture2D(u_texture, v_blurTexCoords[ 7]) * .147308056121;
  color += texture2D(u_texture, v_blurTexCoords[ 8]) * .115876621105;
  color += texture2D(u_texture, v_blurTexCoords[ 9]) * .0776744219933;
  color += texture2D(u_texture, v_blurTexCoords[10]) * .0443683338718;
  color += texture2D(u_texture, v_blurTexCoords[11]) * .0215963866053;
  color += texture2D(u_texture, v_blurTexCoords[12]) * .00895781211794;
  color += texture2D(u_texture, v_blurTexCoords[13]) * .0044299121055113265;
  gl_FragColor = color * v_color;
}