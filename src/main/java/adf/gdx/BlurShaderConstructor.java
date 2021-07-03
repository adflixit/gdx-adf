package adf.gdx;

import static adf.gdx.MathUtil.*;

/**
 * Generates shader code.
 * Gaussian kernel calculator by theomader.
 * Blur shader by xissburg.
 */
public class BlurShaderConstructor {
  private static final String BLUR_VERT_PATTERN =
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
      "\n" +
      "void main() {\n" +
      "  v_color = a_color;\n" +
      "  v_color.a *= 1.00393700787401574803;\n" +
      "  v_texCoords = a_texCoord0;\n" +
      "  gl_Position = u_projTrans * a_position;\n" +
      "%s" + // here
      "}";

  private static final String BLUR_FRAG_PATTERN =
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
      "\n" +
      "void main() {\n" +
      "  vec4 color = vec4(0.);\n" +
      "%s" + // here
      "  gl_FragColor = color * v_color;\n" +
      "}";

  private BlurShaderConstructor() {}

  public static float gaussianDistribution(float x, float mu, float sigma) {
    float d = x - mu;
    float n = 1.f / (sqrtf(2 * PI) * sigma);
    return expf(-d*d/(2 * sigma * sigma)) * n;
  }

  public static float[][] sampleInterval(int sampleCount, float sigma, float minInclusive, float maxInclusive) {
    float[][] result = new float[2][sampleCount];

    for (int s=0; s < sampleCount; ++s) {
      float x = minInclusive + s * (maxInclusive - minInclusive) / (sampleCount-1);

      result[0][s] = x;
      result[1][s] = gaussianDistribution(x, 0, sigma);
    }

    return result;
  }

  public static float integrateSimpson(float[][] samples) {
    float result = samples[1][0] + samples[1][samples.length-1];

    for (int s=1; s < samples.length-1; ++s) {
      float sampleWeight = (s % 2 == 0) ? 2.f : 4.f;
      result += sampleWeight * samples[1][s];
    }

    float h = (samples[0][samples.length-1] - samples[0][0]) / (samples.length-1);
    return result * h / 3.f;
  }

  public static float[] calculateKernel(int kernelSize, float sigma, int sampleCount) {
    int samplesPerBin = (int)Math.ceil(sampleCount / kernelSize);
    if ((samplesPerBin & 1) == 0) { // need an even number of intervals for simpson integration => odd number of samples
      ++samplesPerBin;
    }

    float weightSum = 0;
    float kernelLeft = -floorf(kernelSize/2);
    float[] weights = new float[kernelSize+2];

    // now sample kernel taps and calculate tap weights
    for (int tap = 0; tap < kernelSize; ++tap) {
      float left = kernelLeft - .5f + tap;
      float tapWeight = integrateSimpson(sampleInterval(samplesPerBin, sigma, left, left+1));
      weights[tap+1] = tapWeight;
      weightSum += tapWeight;
    }

    // renormalize kernel
    for(int i=0; i < weights.length; ++i) {
      weights[i] /= weightSum;
    }

    return weights;
  };

  public static String[] generate(int size, float sigma, String blurVertPattern, String blurFragPattern,
      String vertIterPattern, String fragIterPattern, String fragIterMiddlePattern) {
    if ((size & 1) == 0 || size < 3) {
      throw new IllegalArgumentException("Kernel size cannot be even or less than 3.");
    }
    if (sigma <= 0) {
      throw new IllegalArgumentException("Sigma cannot be negative or zero.");
    }

    final String zero = ".0";
    final String multByUni = "* u_blur";
    final int halfSize = (size-1)/2;
    final float[] weights = calculateKernel(size, sigma, 1000);

    String hvertIter = "";
    String vvertIter = "";
    String fragIter = "";

    for (int i=0; i < size-1; i++) {
      String exp = String.valueOf(-.004f*halfSize + .004f*i) + multByUni;
      hvertIter += String.format(vertIterPattern, i, exp, zero);
      vvertIter += String.format(vertIterPattern, i, zero, exp);

      if (i == halfSize) {
        fragIter += String.format(fragIterMiddlePattern, weights[halfSize]);
      }
      fragIter += String.format(fragIterPattern, i, weights[(i < halfSize) ? i : i+1]);
    }

    return new String[] {
      String.format(blurVertPattern, size-1, hvertIter),
      String.format(blurVertPattern, size-1, vvertIter),
      String.format(blurFragPattern, size-1, fragIter)
    };
  }

  public static String[] generate(int size, float sigma) {
    return generate(size, sigma, BLUR_VERT_PATTERN, BLUR_FRAG_PATTERN,
        "v_blurTexCoords[%d] = v_texCoords + vec2(%s, %s);\n",
        "color += texture2D(u_texture, v_blurTexCoords[%d]) * %f;\n",
        "color += texture2D(u_texture, v_texCoords) * %f;\n");
  }
}
