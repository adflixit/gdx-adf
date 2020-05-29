/**
 * Copyright 2016 williammalo2
 */

#ifdef GL_ES
  #define LOWP lowp
  precision mediump float;
#else
  #define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float u_blur;
uniform vec2  u_res;

vec4 fastBlur(sampler2D tex, vec2 pos, float res, vec2 ps) {
  float pr = pow(2.,res);
  vec4 blurred =
  pow(texture2D(tex, (pos+pr*vec2(-1.5,-0.5))*ps,res+1.), vec4(2.2))*4.+
  pow(texture2D(tex, (pos+pr*vec2( 0.5,-1.5))*ps,res+1.), vec4(2.2))*4.+
  pow(texture2D(tex, (pos+pr*vec2( 1.5, 0.5))*ps,res+1.), vec4(2.2))*4.+
  pow(texture2D(tex, (pos+pr*vec2(-0.5, 1.5))*ps,res+1.), vec4(2.2))*4./*+
  pow(texture2D(tex, pos*ps, res+1.), vec4(2.2))*/;
  blurred*=.058823529;
  return pow(blurred,vec4(1./2.2));
}

void main() {
  gl_FragColor = v_color * fastBlur(u_texture, gl_FragCoord.xy, u_blur*6.-2., 1./u_res);
}