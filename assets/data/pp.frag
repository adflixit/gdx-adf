#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec2	u_res;
uniform float	u_blur;
uniform float	u_tiltshiftY;
uniform float	u_tiltshiftX;

#define tsyp	abs(uv.y-.3)
#define tsxp	abs(uv.x-.5)

/** Created by williammalo2. */
vec4 fastBlur(sampler2D tex, vec2 pos, vec2 ps, float res) {
	float pr = pow(2., res);
	vec4 blurred =
	pow(texture2D(tex, (pos+pr*vec2(-1.5, -.5))*ps, res+1.), vec4(2.2))*4. +
	pow(texture2D(tex, (pos+pr*vec2(  .5,-1.5))*ps, res+1.), vec4(2.2))*4. +
	pow(texture2D(tex, (pos+pr*vec2( 1.5,  .5))*ps, res+1.), vec4(2.2))*4. +
	pow(texture2D(tex, (pos+pr*vec2( -.5, 1.5))*ps, res+1.), vec4(2.2))*4. +
	pow(texture2D(tex, pos*ps, res+1.), vec4(2.2));
	blurred*=.058823529;
	return pow(blurred, vec4(1./2.2));
}

void main() {
	vec2 uv = v_texCoords;
	vec4 color = v_color * texture2D(u_texture, uv);
	if (u_blur + u_tiltshiftY + u_tiltshiftX != 0.) {
		float a = u_tiltshiftX > 0. || u_tiltshiftY > 0. ? u_blur + (clamp(u_tiltshiftY - u_blur, 0., 1.)*tsyp) + (clamp(u_tiltshiftX - u_blur, 0., 1.)*tsxp) : u_blur;
		color = vec4(fastBlur(u_texture, gl_FragCoord.xy, 1./u_res, 6.*a-2.).rgb, 1.-a);
	}
	gl_FragColor = color;
}
