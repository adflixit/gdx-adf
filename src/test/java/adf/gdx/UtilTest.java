package adf.gdx;

import static adf.gdx.Util.*;
import static adf.gdx.ColorUtil.*;
import static adf.gdx.MathUtil.*;
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
    assertEquals("top-left", alnToStr(topLeft));
    assertEquals("bottom-right", alnToStr(bottomRight));
    assertEquals("center-top", alnToStr(center | top));
    assertEquals("n/a", alnToStr(0));
  }

  @Test public void testStringUtil() {
    assertEquals("tomatees, potatees, my peas", arrToStr("tomatees", "potatees", "my peas"));
    assertEquals("this is my boomstick", arrToStrf("%s ", "this", "is", "my", "boomstick"));
    assertEquals("klaatu-barada-nikto", arrToStrf("%s-", "klaatu", "barada", "nikto"));
    assertEquals("äteritsiputeritsipuolilautatsijänkä", arrToStrf("%s", "äteritsi", "puteritsi", "puolilautatsi", "jänkä"));
    assertEquals("'doodoo', 'caca', 'peepee'", arrToStrf("'%s'$|, ", "doodoo", "caca", "peepee"));
    assertEquals("1, 2, 3", arrToStr(1, 2, 3));
    assertEquals("1.0, 2.0, 3.0", arrToStr(1f, 2f, 3f));
    assertEquals("1-2-3", arrToStrf("%s-", 1, 2, 3));
    assertEquals("1.0-2.0-3.0", arrToStrf("%s-", 1f, 2f, 3f));
    assertEquals("true, true, false", arrToStr(true, true, false));
    assertEquals("true-true-false", arrToStrf("%s-", true, true, false));
    assertEquals("developers, developers, developers, developers, ", repeat("developers, ", 4));
  }

  @Test public void testColorUtil() {
    Color clr = new Color(0x408080ff), // 64, 128, 128
        tclr = new Color();
    Vector3 rgb = new Vector3(hslToRgb(.5f, .5f, .5f));
    assertEquals(.5f, getHue(clr), .01f);
    assertEquals(.5f, getSat(clr), .01f);
    assertEquals(.5f, getLgt(clr), .01f);
    assertEquals(.3f, getHue(setHue(tclr.set(clr), .3f)), .01f);
    assertEquals(.3f, getSat(setSat(tclr.set(clr), .3f)), .01f);
    assertEquals(.3f, getLgt(setLgt(tclr.set(clr), .3f)), .01f);
    assertEquals(.25f, getHue(setHue(tclr.set(clr), .25f)), .01f);
    assertEquals(.25f, getSat(setSat(tclr.set(clr), .25f)), .01f);
    assertEquals(.25f, getLgt(setLgt(tclr.set(clr), .25f)), .01f);
    assertEquals(.8f, getHue(setHue(tclr.set(clr), .8f)), .01f);
    assertEquals(.8f, getSat(setSat(tclr.set(clr), .8f)), .01f);
    assertEquals(.8f, getLgt(setLgt(tclr.set(clr), .8f)), .01f);
    assertEquals(clr.r, rgb.x, .01f);
    assertEquals(clr.g, rgb.y, .01f);
    assertEquals(clr.b, rgb.z, .01f);
  }

  @Test public void testMathUtil() {
    int flags = 1<<0 | 1<<1 | 1<<2;
    assertTrue(hasFlag(flags, 1<<0));
    assertTrue(hasFlag(flags, 1<<1));
    assertTrue(hasFlag(flags, 1<<2));
    assertEquals(1<<0 | 1<<1 | 1<<2, sumFlags(1<<2));
    assertEquals(5, getShift(1<<5), 0);
    assertTrue(isDec(10.f));
    assertEquals(.123456f, fract(10.123456f), 0);
    assertEquals(10, dec(10.123456f), 0);
    assertEquals(3, clamp(3, 2, 5), 0);
    assertEquals(3, clamp(2, 3, 5), 0);
    assertEquals(5, clamp(10, 3, 5), 0);
    assertEquals(30, lerp(20, 40, .5f), 0);
    assertEquals(25, lerp(20, 40, .25f), 0);
    assertEquals(35, lerp(20, 40, .75f), 0);
    assertEquals(35, lerp(20, 40, .75f), 0);
  }
}
