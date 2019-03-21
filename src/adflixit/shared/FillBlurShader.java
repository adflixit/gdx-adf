package adflixit.shared;

/**
 * Built-in xissburg's shader for {@link FillBlur}.
 * TODO: not tested.
 */
public class FillBlurShader {
  public static final String BLUR_FRAG = "#ifdef GL_ES\n" +
      "  #define LOWP lowp\n" +
      "  precision mediump float;\n" +
      "#else\n" +
      "  #define LOWP \n" +
      "#endif\n" +
      "\n" +
      "varying LOWP vec4 v_color;\n" +
      "varying vec2 v_texCoords;\n" +
      "varying vec2 v_blurTexCoords[14];\n" +
      "uniform sampler2D u_texture;\n" +
      "\n" +
      "uniform float u_blur;\n" +
      "uniform vec4 u_color;\n" +
      "\n" +
      "void main() {\n" +
      "  vec4 color = vec4(0.);\n" +
      "  float a = 0.;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 0]).a * .0044299121055113265;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 1]).a * .00895781211794;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 2]).a * .0215963866053;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 3]).a * .0443683338718;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 4]).a * .0776744219933;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 5]).a * .115876621105;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 6]).a * .147308056121;\n" +
      "  a += texture2D(u_texture, v_texCoords        ).a * .159576912161;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 7]).a * .147308056121;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 8]).a * .115876621105;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[ 9]).a * .0776744219933;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[10]).a * .0443683338718;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[11]).a * .0215963866053;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[12]).a * .00895781211794;\n" +
      "  a += texture2D(u_texture, v_blurTexCoords[13]).a * .0044299121055113265;\n" +
      "  gl_FragColor = vec4(u_color.rgb, a) * v_color;\n" +
      "}";
}
