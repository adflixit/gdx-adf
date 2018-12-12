attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_blurTexCoords[14];
uniform mat4 u_projTrans;

uniform float	u_blur;
uniform float	u_tiltshiftY;
uniform float	u_tiltshiftX;
uniform float	u_tiltshiftYPos;

#define tsyp (u_tiltshiftYPos <= 0. ? abs(uv.y-.3) : abs(uv.y-u_tiltshiftYPos))
#define tsxp abs(uv.x-.5)

void blur(float a) {
  v_blurTexCoords[ 0] = v_texCoords + vec2(-.028 * a, .0);
  v_blurTexCoords[ 1] = v_texCoords + vec2(-.024 * a, .0);
  v_blurTexCoords[ 2] = v_texCoords + vec2(-.020 * a, .0);
  v_blurTexCoords[ 3] = v_texCoords + vec2(-.016 * a, .0);
  v_blurTexCoords[ 4] = v_texCoords + vec2(-.012 * a, .0);
  v_blurTexCoords[ 5] = v_texCoords + vec2(-.008 * a, .0);
  v_blurTexCoords[ 6] = v_texCoords + vec2(-.004 * a, .0);
  v_blurTexCoords[ 7] = v_texCoords + vec2( .004 * a, .0);
  v_blurTexCoords[ 8] = v_texCoords + vec2( .008 * a, .0);
  v_blurTexCoords[ 9] = v_texCoords + vec2( .012 * a, .0);
  v_blurTexCoords[10] = v_texCoords + vec2( .016 * a, .0);
  v_blurTexCoords[11] = v_texCoords + vec2( .020 * a, .0);
  v_blurTexCoords[12] = v_texCoords + vec2( .024 * a, .0);
  v_blurTexCoords[13] = v_texCoords + vec2( .028 * a, .0);
}

void main() {
  v_color = a_color;
  v_color.a *= 1.00393700787401574803;
  v_texCoords = a_texCoord0;
  gl_Position =  u_projTrans * a_position;
  vec2 uv = v_texCoords;
  if (u_blur + u_tiltshiftY + u_tiltshiftX != 0.) {
    blur(u_tiltshiftY + u_tiltshiftX > 0. ? u_blur + 
	    (clamp(u_tiltshiftY - u_blur, 0., 1.)*tsyp) + 
	    (clamp(u_tiltshiftX - u_blur, 0., 1.)*tsxp) : u_blur);
  }
}