/**
 * Copyright 2012 Digia Plc
 */

#ifdef GL_ES
  #define LOWP lowp
  precision mediump float;
#else
  #define LOWP 
#endif

uniform LOWP sampler2D source;
varying vec2 qt_TexCoord0;
varying vec2 qt_TexCoord1;
varying vec2 qt_TexCoord2;
varying vec2 qt_TexCoord3;

void main() {
  vec4 sourceColor = (texture2D(source, qt_TexCoord0) +
  texture2D(source, qt_TexCoord1) +
  texture2D(source, qt_TexCoord2) +
  texture2D(source, qt_TexCoord3)) * 0.25;
  gl_FragColor = sourceColor;
}