package adflixit.shared;

/**
 * Built-in xissburg's blur shader.
 */
public class DefaultBlur {
  public static final String BLURH_VERT = "attribute vec4 a_position;\n" +
      "attribute vec4 a_color;\n" +
      "attribute vec2 a_texCoord0;\n" +
      "\n" +
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoords;\n" +
      "varying vec2 v_blurTexCoords[14];\n" +
      "uniform mat4 u_projTrans;\n" +
      "\n" +
      "uniform float u_blur;\n" +
      "\n" +
      "void main() {\n" +
      "  v_color = a_color;\n" +
      "  v_color.a *= 1.00393700787401574803;\n" +
      "  v_texCoords = a_texCoord0;\n" +
      "  gl_Position = u_projTrans * a_position;\n" +
      "  v_blurTexCoords[ 0] = v_texCoords + vec2(-.028 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 1] = v_texCoords + vec2(-.024 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 2] = v_texCoords + vec2(-.020 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 3] = v_texCoords + vec2(-.016 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 4] = v_texCoords + vec2(-.012 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 5] = v_texCoords + vec2(-.008 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 6] = v_texCoords + vec2(-.004 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 7] = v_texCoords + vec2( .004 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 8] = v_texCoords + vec2( .008 * u_blur, .0);\n" +
      "  v_blurTexCoords[ 9] = v_texCoords + vec2( .012 * u_blur, .0);\n" +
      "  v_blurTexCoords[10] = v_texCoords + vec2( .016 * u_blur, .0);\n" +
      "  v_blurTexCoords[11] = v_texCoords + vec2( .020 * u_blur, .0);\n" +
      "  v_blurTexCoords[12] = v_texCoords + vec2( .024 * u_blur, .0);\n" +
      "  v_blurTexCoords[13] = v_texCoords + vec2( .028 * u_blur, .0);\n" +
      "}";

  public static final String BLURV_VERT = "attribute vec4 a_position;\n" +
      "attribute vec4 a_color;\n" +
      "attribute vec2 a_texCoord0;\n" +
      "\n" +
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoords;\n" +
      "varying vec2 v_blurTexCoords[14];\n" +
      "uniform mat4 u_projTrans;\n" +
      "\n" +
      "uniform float u_blur;\n" +
      "\n" +
      "void main() {\n" +
      "  v_color = a_color;\n" +
      "  v_color.a *= 1.00393700787401574803;\n" +
      "  v_texCoords = a_texCoord0;\n" +
      "  gl_Position = u_projTrans * a_position;\n" +
      "  v_blurTexCoords[ 0] = v_texCoords + vec2(.0, -.028 * u_blur);\n" +
      "  v_blurTexCoords[ 1] = v_texCoords + vec2(.0, -.024 * u_blur);\n" +
      "  v_blurTexCoords[ 2] = v_texCoords + vec2(.0, -.020 * u_blur);\n" +
      "  v_blurTexCoords[ 3] = v_texCoords + vec2(.0, -.016 * u_blur);\n" +
      "  v_blurTexCoords[ 4] = v_texCoords + vec2(.0, -.012 * u_blur);\n" +
      "  v_blurTexCoords[ 5] = v_texCoords + vec2(.0, -.008 * u_blur);\n" +
      "  v_blurTexCoords[ 6] = v_texCoords + vec2(.0, -.004 * u_blur);\n" +
      "  v_blurTexCoords[ 7] = v_texCoords + vec2(.0,  .004 * u_blur);\n" +
      "  v_blurTexCoords[ 8] = v_texCoords + vec2(.0,  .008 * u_blur);\n" +
      "  v_blurTexCoords[ 9] = v_texCoords + vec2(.0,  .012 * u_blur);\n" +
      "  v_blurTexCoords[10] = v_texCoords + vec2(.0,  .016 * u_blur);\n" +
      "  v_blurTexCoords[11] = v_texCoords + vec2(.0,  .020 * u_blur);\n" +
      "  v_blurTexCoords[12] = v_texCoords + vec2(.0,  .024 * u_blur);\n" +
      "  v_blurTexCoords[13] = v_texCoords + vec2(.0,  .028 * u_blur);\n" +
      "}";

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
      "\n" +
      "void main() {\n" +
      "  vec4 color = vec4(0.);\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 0]) * .0044299121055113265;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 1]) * .00895781211794;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 2]) * .0215963866053;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 3]) * .0443683338718;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 4]) * .0776744219933;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 5]) * .115876621105;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 6]) * .147308056121;\n" +
      "  color += texture2D(u_texture, v_texCoords        ) * .159576912161;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 7]) * .147308056121;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 8]) * .115876621105;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[ 9]) * .0776744219933;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[10]) * .0443683338718;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[11]) * .0215963866053;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[12]) * .00895781211794;\n" +
      "  color += texture2D(u_texture, v_blurTexCoords[13]) * .0044299121055113265;\n" +
      "  gl_FragColor = color * v_color;\n" +
      "}";
}
