/**
 * Copyright 2012 Digia Plc
 */

attribute vec4 qt_Vertex;
attribute vec2 qt_MultiTexCoord0;
uniform mat4 qt_Matrix;
uniform float yStep;
uniform float xStep;
varying vec2 qt_TexCoord0;
varying vec2 qt_TexCoord1;
varying vec2 qt_TexCoord2;
varying vec2 qt_TexCoord3;

void main() {
  qt_TexCoord0 = vec2(qt_MultiTexCoord0.x + xStep, qt_MultiTexCoord0.y + yStep * 0.36);
  qt_TexCoord1 = vec2(qt_MultiTexCoord0.x + xStep * 0.36, qt_MultiTexCoord0.y - yStep);
  qt_TexCoord2 = vec2(qt_MultiTexCoord0.x - xStep * 0.36, qt_MultiTexCoord0.y + yStep);
  qt_TexCoord3 = vec2(qt_MultiTexCoord0.x - xStep, qt_MultiTexCoord0.y - yStep * 0.36);
  gl_Position = qt_Matrix * qt_Vertex;
}