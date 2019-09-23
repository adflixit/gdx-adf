package adflixit.gdx;

import static adflixit.gdx.Util.*;
import static com.badlogic.gdx.utils.Align.*;
import static org.junit.Assert.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import org.junit.Test;

public class UtilTest {
  @Test public void testAlign() {
    assertEquals(0, alignX(20, 40, center), 0);
    assertEquals(20, alignX(20, 40, left), 0);
    assertEquals(-20, alignX(20, 40, right), 0);
    assertEquals(-20, alignY(20, 40, top), 0);
    assertEquals(20, alignY(20, 40, bottom), 0);
    assertEquals(40, disalignX(20, 40, center), 0);
    assertEquals(20, disalignX(20, 40, left), 0);
    assertEquals(60, disalignX(20, 40, right), 0);
    assertEquals(60, disalignY(20, 40, top), 0);
    assertEquals(20, disalignY(20, 40, bottom), 0);
  }

  @Test public void testAlnToStr() {
    assertEquals("top-left", alnToStr(topLeft));
    assertEquals("bottom-right", alnToStr(bottomRight));
    assertEquals("center-top", alnToStr(center | top));
    assertEquals("n/a", alnToStr(0));
  }

  @Test public void testStringUtils() {
    assertEquals("tomatees, potatees, my peas", arrToStr("tomatees", "potatees", "my peas"));
    assertEquals("this is my boomstick", arrToStrf("%s ", "this", "is", "my", "boomstick"));
    assertEquals("klaatu-barada-nikto", arrToStrf("%s-", "klaatu", "barada", "nikto"));
    assertEquals("'doodoo', 'caca', 'peepee'", arrToStrf("'%s'$|, ", "doodoo", "caca", "peepee"));
    assertEquals("1, 2, 3", arrToStr(1, 2, 3));
    assertEquals("1.0, 2.0, 3.0", arrToStr(1f, 2f, 3f));
    assertEquals("1-2-3", arrToStrf("%s-", 1, 2, 3));
    assertEquals("1.0-2.0-3.0", arrToStrf("%s-", 1f, 2f, 3f));
    assertEquals("true, true, false", arrToStr(true, true, false));
    assertEquals("true-true-false", arrToStrf("%s-", true, true, false));
    assertEquals("developers, developers, developers, developers, ", repeat("developers, ", 4));
  }

  @Test public void testPrintStackTrace() {
    printStackTrace();
  }

  @Test public void testColorUtils() {
    Color clr = new Color(0x3f7f7fff), tclr = new Color();
    Vector3 rgb = HslToRgb(.5f, .5f, .5f);
    assertEquals(.5f, getHue(clr), .01f);
    assertEquals(.5f, getSat(clr), .01f);
    assertEquals(.5f, getLgt(clr), .01f);
    assertEquals(.5f, getHue(setHue(tclr.set(clr), .5f)), .01f);
    assertEquals(.5f, getSat(setSat(tclr.set(clr), .5f)), .01f);
    assertEquals(.5f, getLgt(setLgt(tclr.set(clr), .5f)), .01f);
    assertEquals(.5f, getLgt(setLgt(tclr.set(clr), .5f)), .01f);
    assertEquals(clr.r, rgb.x, .01f);
    assertEquals(clr.g, rgb.y, .01f);
    assertEquals(clr.b, rgb.z, .01f);
  }

  @Test public void testFlagUtils() {
    int flags = 1<<0 | 1<<1 | 1<<2;
    assertTrue(hasFlag(flags, 1<<0));
    assertTrue(hasFlag(flags, 1<<1));
    assertTrue(hasFlag(flags, 1<<2));
    assertEquals(1<<0 | 1<<1 | 1<<2, sumFlags(1<<2));
    assertEquals(5, getShift(1<<5), 0);
  }
}
