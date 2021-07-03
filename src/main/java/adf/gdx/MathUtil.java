package adf.gdx;

import static java.lang.Math.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class MathUtil {
  private MathUtil() {}

  public static final float     SQRT2             = sqrtf(2),
                                SQRT3             = sqrtf(3),
                                LOG2              = logf(2),
                                PI                = (float)Math.PI,
                                PI2               = PI*2,
                                PI_2              = PI/2,
                                PI_4              = PI/4,
                                DEG               = PI/180,
                                RAD               = 180/PI;

  public static final Vector2 tmpv2 = new Vector2();
  public static final Vector3 tmpv3 = new Vector3();

  public static float sinf(float a)               {return (float)sin(a);}
  public static float cosf(float a)               {return (float)cos(a);}
  public static float tanf(float a)               {return (float)tan(a);}
  public static float asinf(float a)              {return (float)asin(a);}
  public static float acosf(float a)              {return (float)acos(a);}
  public static float atanf(float a)              {return (float)atan(a);}
  public static float expf(float a)               {return (float)exp(a);}
  public static float logf(float a)               {return (float)log(a);}
  public static float log10f(float a)             {return (float)log10(a);}
  public static float sqrtf(float a)              {return (float)sqrt(a);}
  public static float cbrtf(float a)              {return (float)cbrt(a);}
  public static float ceilf(float a)              {return (float)ceil(a);}
  public static float floorf(float a)             {return (float)floor(a);}
  public static float rintf(float a)              {return (float)rint(a);}
  public static float atan2f(float y, float x)    {return (float)atan2(y,x);}
  public static float powf(float a, float b)      {return (float)pow(a,b);}
  public static float sinhf(float x)              {return (float)sinh(x);}
  public static float coshf(float x)              {return (float)cosh(x);}
  public static float tanhf(float x)              {return (float)tanh(x);}
  public static float hypotf(float x, float y)    {return (float)hypot(x,y);}

  /**
   * @return if {@code flag} intersects {@code flags}. {@code flag} has to be one single bit.
   */
  public static boolean hasFlag(int flags, int flag) {
    return (flags & flag) != 0;
  }

  /**
   * @return if {@code flag} intersects {@code flags}. {@code flag} has to be one single bit.
   */
  public static boolean hasFlag(long flags, long flag) {
    return (flags & flag) != 0;
  }

  /**
   * @return a union of all flags preceding {@code flag}.
   * @author Nayuki
   */
  public static int sumFlags(int flag) {
    return flag * 2 - 1;
  }

  /**
   * @return a union of all flags preceding {@code flag}.
   * @author Nayuki
   */
  public static long sumFlags(long flag) {
    return flag * 2 - 1;
  }

  /**
   * @return binary shift of {@code a}.
   */
  public static int getShift(int a) {
    return (int)(log(a) / LOG2);
  }

  /**
   * @return binary shift of {@code a}.
   */
  public static long getShift(long a) {
    return (long)(log(a) / LOG2);
  }

  /**
   * @return random double in range from 0 to 1.
   */
  public static double rand() {
    return random();
  }

  /**
   * @return random double in a range from 0 to {@code a}.
   */
  public static double rand(double a) {
    return a * rand();
  }

  /**
   * @return random double in a range from {@code a} to {@code b}.
   */
  public static double rand(double a, double b) {
    return a + (b - a) * rand();
  }

  /**
   * @return random float in range from 0 to 1.
   */
  public static float randf() {
    return (float)rand();
  }

  /**
   * @return random float in a range from 0 to {@code a}.
   */
  public static float randf(float a) {
    return a * randf();
  }

  /**
   * @return random float in a range from {@code a} to {@code b}.
   */
  public static float randf(float a, float b) {
    return a + (b - a) * randf();
  }

  /**
   * @return random int in a range from 0 to {@code a}.
   */
  public static int randi(int a) {
    return round(randf(a));
  }

  /**
   * @return random int in a range from {@code a} to {@code b}.
   */
  public static int randi(int a, int b) {
    return round(randf(a, b));
  }

  /**
   * @return random bool, either true or false.
   */
  public static boolean randb() {
    return randi(1) > 0;
  }

  /**
   * @return is {@code a} decimal.
   */
  public static boolean isDec(float a) {
    return fract(a) == 0;
  }

  /**
   * @return is {@code a} decimal.
   */
  public static boolean isDec(double a) {
    return fract(a) == 0;
  }

  /**
   * @return the fraction of {@code a}.
   */
  public static float fract(float a) {
    return a%1;
  }

  /**
   * @return the fraction of {@code a}.
   */
  public static double fract(double a) {
    return a%1;
  }

  /**
   * @return the fraction of a vector.
   */
  public static Vector2 fract(Vector2 vec) {
    return tmpv2.set(fract(vec.x), fract(vec.y));
  }

  /**
   * @return the fraction of a vector.
   */
  public static Vector3 fract(Vector3 vec) {
    return tmpv3.set(fract(vec.x), fract(vec.y), fract(vec.z));
  }

  /**
   * @return the decimal part of {@code a}.
   */
  public static float dec(float a) {
    return a - fract(a);
  }

  /**
   * @return the decimal part of {@code a}.
   */
  public static double dec(double a) {
    return a - fract(a);
  }

  /**
   * @return float floor of a vector.
   */
  public static Vector2 floorf(Vector2 v2) {
    return tmpv2.set(floorf(v2.x), floorf(v2.y));
  }

  /**
   * @return float floor of a vector.
   */
  public static Vector3 floorf(Vector3 v3) {
    return tmpv3.set(floorf(v3.x), floorf(v3.y), floorf(v3.z));
  }

  /**
   * @return {@code a} bounded between {@code min} and {@code max}.
   */
  public static int clamp(int a, int min, int max) {
    if (min > max) {
      int t = min;
      min = max;
      max = t;
    }
    return (a < min) ? min : ((a > max) ? max : a);
  }

  /**
   * @return {@code a} bounded between {@code min} and {@code max}.
   */
  public static long clamp(long a, long min, long max) {
    if (min > max) {
      long t = min;
      min = max;
      max = t;
    }
    return (a < min) ? min : ((a > max) ? max : a);
  }

  /**
   * @return {@code a} bounded between {@code min} and {@code max}.
   */
  public static float clamp(float a, float min, float max) {
    if (min > max) {
      float t = min;
      min = max;
      max = t;
    }
    return (a < min) ? min : ((a > max) ? max : a);
  }

  /**
   * @return {@code a} bounded between {@code min} and {@code max}.
   */
  public static double clamp(double a, double min, double max) {
    if (min > max) {
      double t = min;
      min = max;
      max = t;
    }
    return (a < min) ? min : ((a > max) ? max : a);
  }

  /**
   * @return the greatest of the three numbers.
   */
  public static int max(int a, int b, int c) {
    return Math.max(a, Math.max(b, c));
  }

  /**
   * @return the greatest of the three numbers.
   */
  public static long max(long a, long b, long c) {
    return Math.max(a, Math.max(b, c));
  }

  /**
   * @return the greatest of the three numbers.
   */
  public static float max(float a, float b, float c) {
    return Math.max(a, Math.max(b, c));
  }

  /**
   * @return the greatest of the three numbers.
   */
  public static double max(double a, double b, double c) {
    return Math.max(a, Math.max(b, c));
  }

  /**
   * @return the smallest of the three numbers.
   */
  public static int min(int a, int b, int c) {
    return Math.min(a, Math.min(b, c));
  }

  /**
   * @return the smallest of the three numbers.
   */
  public static long min(long a, long b, long c) {
    return Math.min(a, Math.min(b, c));
  }

  /**
   * @return the smallest of the three numbers.
   */
  public static float min(float a, float b, float c) {
    return Math.min(a, Math.min(b, c));
  }

  /**
   * @return the smallest of the three numbers.
   */
  public static double min(double a, double b, double c) {
    return Math.min(a, Math.min(b, c));
  }

  /**
   * @return absolute values.
   */
  public static Vector2 abs(Vector2 v2) {
    return tmpv2.set(Math.abs(v2.x), Math.abs(v2.y));
  }

  /**
   * @return absolute values.
   */
  public static Vector3 abs(Vector3 v3) {
    return v3.set(Math.abs(v3.x), Math.abs(v3.y), Math.abs(v3.z));
  }

  /**
   * @return linear interpolation of {@code c} between {@code a} and {@code b}.
   */
  public static float lerp(float a, float b, float c) {
    return (a * (1 - c)) + (b * c);
  }

  /**
   * @return linear interpolation of {@code c} between {@code a} and {@code b}.
   */
  public static double lerp(double a, double b, double c) {
    return (a * (1 - c)) + (b * c);
  }

  /**
   * @return multiplication of {@code a} by {@code n} divided by {@code d}.
   */
  public static float div(float a, float n, float d) {
    return a * (n / d);
  }

  /**
   * @return multiplication of {@code a} by {@code n} divided by {@code d}.
   */
  public static double div(double a, double n, double d) {
    return a * (n / d);
  }

  /**
   * @return is {@code a} divisible by {@code b}.
   */
  public static boolean isDivBy(int a, int b) {
    return a % b == 0;
  }

  /**
   * @return is {@code a} divisible by {@code b}.
   */
  public static boolean isDivBy(float a, float b) {
    return a % b == 0;
  }

  /**
   * @return is {@code a} divisible by {@code b}.
   */
  public static boolean isDivBy(double a, double b) {
    return a % b == 0;
  }

  /**
   * @return distance between two given points.
   */
  public static float dist(float x1, float y1, float x2, float y2) {
    return hypotf(x2 - x1, y2 - y1);
  }

  /**
   * @return distance between two given points.
   */
  public static float dist(Vector2 p1, Vector2 p2) {
    return dist(p1.x, p1.y, p2.x, p2.y);
  }

  /**
   * @param r radius
   * @return side of a circumscribed square.
   */
  public static float circSqrSide(float r) {
    return (r * SQRT2) / 2;
  }
}
