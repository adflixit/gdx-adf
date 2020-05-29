/**
 * Copyright 2012 xissburg
 */

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_blurTexCoords[14];
uniform mat4 u_projTrans;

uniform float u_blur;

void main() {
  v_color = a_color;
  v_color.a *= 1.00393700787401574803;
  v_texCoords = a_texCoord0;
  gl_Position = u_projTrans * a_position;
  v_blurTexCoords[ 0] = v_texCoords + vec2(.0, -.028 * u_blur);
  v_blurTexCoords[ 1] = v_texCoords + vec2(.0, -.024 * u_blur);
  v_blurTexCoords[ 2] = v_texCoords + vec2(.0, -.020 * u_blur);
  v_blurTexCoords[ 3] = v_texCoords + vec2(.0, -.016 * u_blur);
  v_blurTexCoords[ 4] = v_texCoords + vec2(.0, -.012 * u_blur);
  v_blurTexCoords[ 5] = v_texCoords + vec2(.0, -.008 * u_blur);
  v_blurTexCoords[ 6] = v_texCoords + vec2(.0, -.004 * u_blur);
  v_blurTexCoords[ 7] = v_texCoords + vec2(.0,  .004 * u_blur);
  v_blurTexCoords[ 8] = v_texCoords + vec2(.0,  .008 * u_blur);
  v_blurTexCoords[ 9] = v_texCoords + vec2(.0,  .012 * u_blur);
  v_blurTexCoords[10] = v_texCoords + vec2(.0,  .016 * u_blur);
  v_blurTexCoords[11] = v_texCoords + vec2(.0,  .020 * u_blur);
  v_blurTexCoords[12] = v_texCoords + vec2(.0,  .024 * u_blur);
  v_blurTexCoords[13] = v_texCoords + vec2(.0,  .028 * u_blur);
}