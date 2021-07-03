package adf.gdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import static adf.gdx.DefaultBlurShader.*;
import static adf.gdx.DefaultBlurShader.BLUR_FRAG;

/**
 * Makes flat blurred image, e.g. shadow or glow.
 */
public class FlatBlur extends Blur {
  private static final String FLAT_BLUR_VERT_PATTERN =
      "attribute vec4 a_position;\n" +
      "attribute vec4 a_color;\n" +
      "attribute vec2 a_texCoord0;\n" +
      "\n" +
      "varying vec4 v_color;\n" +
      "varying vec2 v_texCoords;\n" +
      "varying vec2 v_blurTexCoords[%d];\n" + // here
      "uniform mat4 u_projTrans;\n" +
      "\n" +
      "uniform float u_blur;\n" +
      "uniform vec4 u_color;\n" +
      "\n" +
      "void main() {\n" +
      "  v_color = a_color;\n" +
      "  v_color.a *= 1.00393700787401574803;\n" +
      "  v_texCoords = a_texCoord0;\n" +
      "  gl_Position = u_projTrans * a_position;\n" +
      "%s" + // here
      "}";

  private static final String FLAT_BLUR_FRAG_PATTERN =
      "#ifdef GL_ES\n" +
      "  #define LOWP lowp\n" +
      "  precision mediump float;\n" +
      "#else\n" +
      "  #define LOWP \n" +
      "#endif\n" +
      "\n" +
      "varying LOWP vec4 v_color;\n" +
      "varying vec2 v_texCoords;\n" +
      "varying vec2 v_blurTexCoords[%d];\n" + // here
      "uniform sampler2D u_texture;\n" +
      "\n" +
      "uniform float u_blur;\n" +
      "uniform vec4 u_color;\n" +
      "\n" +
      "void main() {\n" +
      "  vec4 color = vec4(0.);\n" +
      "  float a = 0.;\n" +
      "%s" + // here
      "  gl_FragColor = vec4(u_color.rgb, a) * v_color;\n" +
      "}";

  private static final String   UNI_COLOR   = "u_color";

  public final Color            color       = new Color(0x000000ff);

  public FlatBlur() {
    setFormat(Format.RGBA8888);
  }

  public FlatBlur(Color clr) {
    this();
    setColor(clr);
  }

  public FlatBlur(FileHandle hvert, FileHandle hfrag, FileHandle vvert, FileHandle vfrag) {
    this();
    load(hvert, hfrag, vvert, vfrag);
  }

  public FlatBlur(String hvert, String hfrag, String vvert, String vfrag) {
    this();
    load(hvert, hfrag, vvert, vfrag);
  }

  public void generateShader(int size, float sigma) {
    String[] src = BlurShaderConstructor.generate(size, sigma, FLAT_BLUR_VERT_PATTERN, FLAT_BLUR_FRAG_PATTERN,
        "v_blurTexCoords[%d] = v_texCoords + vec2(%s, %s);\n",
        "a += texture2D(u_texture, v_blurTexCoords[%d]).a * %f;\n",
        "a += texture2D(u_texture, v_texCoords).a * %f;\n");

    if (isInit()) {
      hpass.dispose();
      vpass.dispose();
    }

    hpass = new ShaderProgram(src[0], src[2]);
    vpass = new ShaderProgram(src[1], src[2]);
  }

  public Blur loadDefault() {
    load(FLAT_BLURH_VERT, FLAT_BLUR_FRAG, FLAT_BLURV_VERT, FLAT_BLUR_FRAG);
    return this;
  }

  public FlatBlur setColor(Color clr) {
    color.set(clr);
    return this;
  }

  @Override protected void setupHPassUniforms() {
    super.setupHPassUniforms();
    hpass.setUniformf(UNI_COLOR, color);
  }

  @Override protected void setupVPassUniforms() {
    super.setupHPassUniforms();
    vpass.setUniformf(UNI_COLOR, color);
  }
}
