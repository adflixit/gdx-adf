/**
 * Copyright 2015 Jam3
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
uniform vec2  u_res;
uniform vec2  u_direction;

void main() {
  vec4 color = vec4(0.0);
  vec2 off1 = vec2(1.411764705882353) * u_direction;
  vec2 off2 = vec2(3.2941176470588234) * u_direction;
  vec2 off3 = vec2(5.176470588235294) * u_direction;
  color += texture2D(u_texture, v_texCoords) * 0.1964825501511404;
  color += texture2D(u_texture, v_texCoords + (off1 / u_res)) * 0.2969069646728344;
  color += texture2D(u_texture, v_texCoords - (off1 / u_res)) * 0.2969069646728344;
  color += texture2D(u_texture, v_texCoords + (off2 / u_res)) * 0.09447039785044732;
  color += texture2D(u_texture, v_texCoords - (off2 / u_res)) * 0.09447039785044732;
  color += texture2D(u_texture, v_texCoords + (off3 / u_res)) * 0.010381362401148057;
  color += texture2D(u_texture, v_texCoords - (off3 / u_res)) * 0.010381362401148057;
  gl_FragColor = color * v_color;
}