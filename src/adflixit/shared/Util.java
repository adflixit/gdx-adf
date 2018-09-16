/*
 * Copyright 2018 Adflixit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adflixit.shared;

import static adflixit.shared.BaseGame.*;
import static com.badlogic.gdx.utils.Align.*;
import static java.lang.Math.*;

import adflixit.shared.misc.RoundedDrawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Json;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class Util {
	private Util() {}

	public static final double		SQRT2				= sqrt(2),
									SQRT3				= sqrt(3),
									LOG2				= log(2),
									CIRCLE				= PI*2,
									SEMICIRCLE			= PI,
									QTRCIRCLE			= PI/2,
									DEGREE				= PI/180,
									GOLDENRATIO			= (1+sqrt(5))/2,
									INVGOLDENRATIO		= 1/GOLDENRATIO;

	// Common use temporary variables
	public static final Vector2		tmpv2				= new Vector2();
	public static final Vector3		tmpv3				= new Vector3();
	public static final Color		tmpclr				= new Color();

	/** @return current time in milliseconds. */
	public static long currentTime() {
		return System.currentTimeMillis();
	}

	/** @return current time in seconds. */
	public static long currentTimeS() {
		return currentTime()/1000;
	}

	/** @return current time in minutes. */
	public static long currentTimeM() {
		return currentTimeS()/60;
	}

	/** @return current time in hours. */
	public static long currentTimeH() {
		return currentTimeM()/60;
	}

	/** @return current time in days. */
	public static long currentTimeD() {
		return currentTimeH()/24;
	}

	public static void illegalArgument(String info) {
		throw new IllegalArgumentException("Illegal argument(s): "+info+".");
	}

	/** Template for calculating the position of one point relative to the other in single dimension.
	 * @param p position
	 * @param l length
	 * @param aln alignment flags
	 * @param piv initial pivot
	 * @param piv0 lowest pivot
	 * @param piv1 highest pivot */
	private static float alignT(float p, float l, int aln, int piv, int piv0, int piv1) {
		if (hasFlag(piv, piv0)) {
			if (hasFlag(aln, piv1)) {
				p -= l;
			} else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
				p -= l/2;
			}
		} else if (hasFlag(piv, piv1)) {
			if (hasFlag(aln, piv0)) {
				p += l;
			} else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
				p += l/2;
			}
		} else if (!hasFlag(piv, piv0) && !hasFlag(piv, piv1)) {
			if (hasFlag(aln, piv0)) {
				p += l/2;
			} else if (hasFlag(aln, piv1)) {
				p -= l/2;
			}
		}
		return p;
	}

	/** Template for undoing the alignment of one point relative to the other in single dimension.
	 * @param p position
	 * @param l length
	 * @param aln alignment flags
	 * @param piv initial pivot
	 * @param piv0 lowest pivot
	 * @param piv1 highest pivot */
	private static float disalignT(float p, float d, int aln, int piv, int piv0, int piv1) {
		if (hasFlag(piv, piv0)) {
			if (hasFlag(aln, piv1)) {
				p += d;
			} else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
				p += d/2;
			}
		} else if (hasFlag(piv, piv1)) {
			if (hasFlag(aln, piv0)) {
				p -= d;
			} else if (!hasFlag(aln, piv0) && !hasFlag(piv, piv1)) {
				p -= d/2;
			}
		} else if (!hasFlag(piv, piv0) && !hasFlag(piv, piv1)) {
			if (hasFlag(aln, piv0)) {
				p -= d/2;
			} else if (hasFlag(aln, piv1)) {
				p += d/2;
			}
		}
		return p;
	}

	/** Aligns {@code x} in {@code width} relative to the pivot.
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static float alignX(float x, float width, int aln, int piv) {
		return alignT(x, width, aln, piv, left, right);
	}

	/** Aligns {@code x} in {@code width} relative to the bottom left point.
	 * @param aln alignment flags */
	public static float alignX(float x, float width, int aln) {
		return alignX(x, width, aln, bottomLeft);
	}

	/** Aligns {@code y} in {@code height} relative to the pivot.
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static float alignY(float y, float height, int aln, int piv) {
		return alignT(y, height, aln, piv, bottom, top);
	}

	/** Aligns {@code y} in {@code height} relative to the bottom left point.
	 * @param aln alignment flags */
	public static float alignY(float y, float height, int aln) {
		return alignY(y, height, aln, bottomLeft);
	}

	/** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static Vector2 align(float x, float y, float width, float height, int aln, int piv) {
		/*if ((aln & right) != 0) x -= width;
		else if ((aln & left)==0) x -= width / 2;
		if ((aln & top) != 0) y -= height;
		else if ((aln & bottom)==0) y -= height / 2;*/
		return tmpv2.set(alignX(x, width, aln, piv), alignY(y, height, aln, piv));
	}

	/** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
	 * @param aln alignment flags */
	public static Vector2 align(float x, float y, float width, float height, int aln) {
		return align(x, y, width, height, aln, bottomLeft);
	}

	/** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
	 * @param v vector
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static Vector2 align(Vector2 v, float width, float height, int aln, int piv) {
		return align(v.x, v.y, width, height, aln, piv);
	}

	/** Aligns {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
	 * @param v vector
	 * @param aln alignment flags */
	public static Vector2 align(Vector2 v, float width, float height, int aln) {
		return align(v, width, height, aln, bottomLeft);
	}

	/** Reverses the alignment of {@code x} in {@code width} relative to the pivot.
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static float disalignX(float x, float width, int aln, int piv) {
		return disalignT(x, width, aln, piv, left, right);
	}

	/** Reverses the alignment of {@code x} in {@code width} relative to the bottom left point.
	 * @param aln alignment flags */
	public static float disalignX(float x, float width, int aln) {
		return disalignX(x, width, aln, bottomLeft);
	}

	/** Reverses the alignment of {@code y} in {@code height} relative to the pivot.
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static float disalignY(float y, float height, int aln, int piv) {
		return disalignT(y, height, aln, piv, bottom, top);
	}

	/** Reverses the alignment of {@code y} in {@code height} relative to the bottom left point.
	 * @param aln alignment flags */
	public static float disalignY(float y, float height, int aln) {
		return disalignY(y, height, aln, bottomLeft);
	}

	/** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static Vector2 disalign(float x, float y, float width, float height, int aln, int piv) {
		return tmpv2.set(disalignX(x, width, aln, piv), disalignY(y, height, aln, piv));
	}

	/** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
	 * @param aln alignment flags */
	public static Vector2 disalign(float x, float y, float width, float height, int aln) {
		return disalign(x, y, width, height, aln, bottomLeft);
	}

	/** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the pivot.
	 * @param v vector
	 * @param aln alignment flags
	 * @param piv initial pivot */
	public static Vector2 disalign(Vector2 v, float width, float height, int aln, int piv) {
		return disalign(v.x, v.y, width, height, aln, piv);
	}

	/** Reverses the alignment of {@code x} and {@code y} both in {@code width} and {@code height} respectively relative to the bottom left point.
	 * @param v vector
	 * @param aln alignment flags */
	public static Vector2 disalign(Vector2 v, float width, float height, int aln) {
		return disalign(v, width, height, aln, bottomLeft);
	}

	/** @return {@link Align} translated to string. */
	public static String alnToStr(int aln) {
		String s = "";
		boolean v = hasFlag(aln,top)||hasFlag(aln,bottom), h = hasFlag(aln,left)||hasFlag(aln,right);
		if (hasFlag(aln, center)) {
			s+="center"+(v||h?" ":"");
		}
		if (hasFlag(aln, top)) {
			s+="top"+(h?" ":"");
		}
		if (hasFlag(aln, bottom)) {
			s+="bottom"+(h?" ":"");
		}
		if (hasFlag(aln, left)) {
			s+="left";
		}
		if (hasFlag(aln, right)) {
			s+="right";
		}
		return aln != 0 ? s : "n/a";
	}

	/** @return array items represented as text. */
	public static <T> String arrayToString(T... array) {
		String s = "";
		for (T i : array) {
			s += i+", ";
		}
		return s.substring(0, s.length()-2);
	}
	
	/** @return array items represented as text. */
	public static String arrayToString(int... array) {
		String s = "";
		for (Object i : array) {
			s += i+", ";
		}
		return s.substring(0, s.length()-2);
	}
	
	/** @return array items represented as text. */
	public static String arrayToString(long... array) {
		String s = "";
		for (Object i : array) {
			s += i+", ";
		}
		return s.substring(0, s.length()-2);
	}
	
	/** @return array items represented as text. */
	public static String arrayToString(float... array) {
		String s = "";
		for (Object i : array) {
			s += i+", ";
		}
		return s.substring(0, s.length()-2);
	}

	/** @return array items represented as text. */
	public static String arrayToString(double... array) {
		String s = "";
		for (Object i : array) {
			s += i+", ";
		}
		return s.substring(0, s.length()-2);
	}

	/** Uses the standard string formatting pattern, where "%s" marks the placeholder, 
	 * e.g. the pattern "%s, " will format the array {'a', 'b', 'c'} as "a, b, c".
	 * @return array items ordered by the pattern, represented as text. */
	public static <T> String arrayToStringF(String pattern, T... array) {
		String s = "";
		for (T i : array) {
			s += String.format(pattern, i);
		}
		// trimming
		return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
	}

	/** @return array items ordered by the pattern, represented as text.
	 * @see #arrayToStringF(String, Object...) */
	public static String arrayToStringF(String pattern, int... array) {
		String s = "";
		for (Object i : array) {
			s += String.format(pattern, i);
		}
		// trimming
		return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
	}

	/** @return array items ordered by the pattern, represented as text.
	 * @see #arrayToStringF(String, Object...) */
	public static String arrayToStringF(String pattern, long... array) {
		String s = "";
		for (Object i : array) {
			s += String.format(pattern, i);
		}
		// trimming
		return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
	}

	/** @return array items ordered by the pattern, represented as text.
	 * @see #arrayToStringF(String, Object...) */
	public static String arrayToStringF(String pattern, float... array) {
		String s = "";
		for (Object i : array) {
			s += String.format(pattern, i);
		}
		// trimming
		return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
	}

	/** @return array items ordered by the pattern, represented as text.
	 * @see #arrayToStringF(String, Object...) */
	public static String arrayToStringF(String pattern, double... array) {
		String s = "";
		for (Object i : array) {
			s += String.format(pattern, i);
		}
		// trimming
		return s.substring(0, s.length()-(pattern.length()-(pattern.indexOf("%s")+2)));
	}

	public static String getStackTrace() {
		return arrayToStringF("%s\n", Thread.currentThread().getStackTrace());
	}

	public static void printStackTrace() {
		for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
		    System.out.println(i);
		}
	}

	/*public static BitmapFont createFont(FileHandle file, int size) {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(file);
		FreeTypeFontParameter par = new FreeTypeFontParameter();
		par.size = size;
		BitmapFont font = gen.generateFont(par);
		gen.dispose();
		return font;
	}*/

	/** Draws a rectangle tiled with {@link TextureRegion} with an offset.
	 * FIXME: unfinished. */
	public static void drawTiledRect(Batch batch, TextureRegion region, float x, float y, float ofsX, float ofsY, float width, float height) {
		float regionWidth = region.getRegionWidth(), regionHeight = region.getRegionHeight();
		if (ofsX > 0) {
			ofsX %= regionWidth;
		}
		if (ofsY > 0) {
			ofsY %= regionHeight;
		}
		if (ofsX < 0) {
			ofsX += regionWidth;
		}
		if (ofsY < 0) {
			ofsY += regionHeight;
		}
		int fullX = (int)(width/regionWidth), fullY = (int)(height/regionHeight);
		float fullWidth = regionWidth*fullX, fullHeight = regionHeight*fullY;
		float remainingX = width - fullWidth - ofsX, remainingY = height - fullHeight - ofsY;
		float startX = x, startY = y;
		float endX = x + width - remainingX, endY = y + height - remainingY;
		for (int i=0; i < fullX; i++) {
			y = startY + ofsY;
			for (int j=0; j < fullY; j++) {
				batch.draw(region, x, y, regionWidth, regionHeight);
				y+=regionHeight;
			}
			x+=regionWidth;
		}
		Texture texture = region.getTexture();
		float u = region.getU();
		float v2 = region.getV2();
		if (ofsX > 0) {
			float u2 = u + ofsX / texture.getWidth();
			float v = region.getV();
			y = startY;
			for (int i=0; i < fullY; i++) {
				batch.draw(texture, x, y, ofsX, regionHeight, u, v2, u2, v);
				y+=regionHeight;
			}
			if (remainingY > 0) {
				v = v2 - remainingY / texture.getHeight();
				batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v);
			}
		}
		if (ofsY > 0) {
			float u2 = region.getU2();
			float v = v2 - ofsY / texture.getHeight();
			x = startX;
			for (int i=0; i < fullX; i++) {
				batch.draw(texture, x, y, regionWidth, ofsY, u, v2, u2, v);
				x+=regionWidth;
			}
		}
		if (remainingX > 0) {
			float u2 = u + remainingX / texture.getWidth();
			float v = region.getV();
			y = fullHeight;
			for (int i=0; i < fullY; i++) {
				batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v);
				y+=regionHeight;
			}
			if (remainingY > 0) {
				v = v2 - remainingY / texture.getHeight();
				batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v);
			}
		}
		if (remainingY > 0) {
			float u2 = region.getU2();
			float v = v2 - remainingY / texture.getHeight();
			x = fullWidth;
			for (int i=0; i < fullX; i++) {
				batch.draw(texture, x, y, regionWidth, remainingY, u, v2, u2, v);
				x+=regionWidth;
			}
		}
	}

	/************* Scene utilities ************/

	public static void addActors(Group g, Actor... a) {
		for (Actor actor : a) {
			g.addActor(actor);
		}
	}

	public static boolean hasChild(Group g, Actor a) {
		return g.getChildren().contains(a, true);
	}

	/** Turns actor visible. */
	public static void showActor(Actor actor) {
		actor.setVisible(true);
	}

	/** Turns actors visible. */
	public static void showActors(Actor... actors) {
		for (Actor actor : actors) {
			showActor(actor);
		}
	}

	/** Turns actor invisible. */
	public static void hideActor(Actor actor) {
		actor.setVisible(false);
	}

	/** Turns actors invisible. */
	public static void hideActors(Actor... actors) {
		for (Actor actor : actors) {
			hideActor(actor);
		}
	}

	public static float getAlpha(Actor actor) {
		return actor.getColor().a;
	}

	public static float getAlpha(Sprite sprite) {
		return sprite.getColor().a;
	}

	public static void setAlpha(Actor actor, float a) {
		actor.getColor().a = a;
	}

	/** Sets alpha to 0. */
	public static void resetAlpha(Actor actor) {
		actor.getColor().a = 0;
	}

	/** Sets alpha to 0. */
	public static void resetAlpha(Sprite sprite) {
		sprite.setAlpha(0);
	}

	/** Sets alpha to 0. */
	public static void resetAlpha(Actor... actors) {
		for (Actor actor : actors) {
			resetAlpha(actor);
		}
	}

	/** Sets alpha to 0. */
	public static void resetAlpha(Sprite... sprites) {
		for (Sprite sprite : sprites) {
			resetAlpha(sprite);
		}
	}

	public static void setRgb(Actor actor, float r, float g, float b) {
		actor.setColor(r, g, b, getAlpha(actor));
	}

	public static void setRgb(Actor actor, Color clr) {
		setRgb(actor, clr.r, clr.g, clr.b);
	}

	public static void setRgb(Sprite spr, float r, float g, float b) {
		spr.setColor(r, g, b, getAlpha(spr));
	}

	public static void setRgb(Sprite spr, Color clr) {
		setRgb(spr, clr.r, clr.g, clr.b);
	}

	/** @return a {@link ImageButton} with specified image and background. */
	public static ImageButton simpleImageButton(Drawable image, Drawable bg) {
		return new ImageButton(new ImageButtonStyle(bg, null, null, image, null, null));
	}

	/** @return a {@link ImageButton} with specified image without background. */
	public static ImageButton simpleImageButton(Drawable image) {
		return simpleImageButton(image, drawable("btn_bg"));
	}

	/** @return a {@link ImageButton} with specified image and background skin drawable names. */
	public static ImageButton simpleImageButton(String image, String bg) {
		return simpleImageButton(drawable(image), drawable(bg));
	}

	/** @return a {@link ImageButton} with specified image skin drawable name without background. */
	public static ImageButton simpleImageButton(String image) {
		return simpleImageButton(drawable(image));
	}

	/** Sets the {@link ImageButton} {@link ImageButtonStyle#imageUp}. */
	public static void setImageButtonImage(ImageButton btn, Drawable image) {
		btn.getStyle().imageUp = image;
	}

	/** Because there is no other workaround in libgdx to do this, this function converts an existing {@link TextButtonStyle} 
	 * to a rounded one by turning it's background into a {@link RoundedDrawable}.<br><br>
	 * Warning: this function permanently mutates the specified {@link TextButtonStyle}.
	 * @return a {@link TextButton} with {@link TextButtonStyle} with the {@link RoundedDrawable} background.
	 * FIXME: still not working properly. */
	public static TextButton roundedTextButton(String text, Skin skin, String styleName) {
		TextButton btn = new TextButton(text, skin, styleName);
		Drawable up = btn.getStyle().up;
		if (up instanceof NinePatchDrawable) {
			btn.getStyle().up = new RoundedDrawable((NinePatchDrawable)up);
		}
		return btn;
	}

	/** @return {@code null} if nothing found. */
	public static ParticleEmitter getPfxEmitterByName(ParticleEffect pfx, String name) {
		for (ParticleEmitter emitter : pfx.getEmitters()) {
			if (emitter.getName().equals(name)) {
				return emitter;
			}
		}
		return null;
	}

	/************* Box2D utilities ************/

	/** @return {@link Filter#categoryBits} of a Box2D {@link Fixture}. */
	public static short categoryBits(Fixture fix) {
		return fix.getFilterData().categoryBits;
	}

	/** @return {@link maskBits} of a Box2D {@link Fixture}. */
	public static short maskBits(Fixture fix) {
		return fix.getFilterData().maskBits;
	}

	/** @return {@link groupIndex} of a Box2D {@link Fixture}. */
	public static short groupIndex(Fixture fix) {
		return fix.getFilterData().groupIndex;
	}

	public static boolean fixtureIsInContact(Contact contact, Fixture fix) {
		return contact.getFixtureA()==fix || contact.getFixtureB()==fix;
	}

	public static boolean checkContactByCategory(Fixture fixa, Fixture fixb, short cba, short cbb) {
		return categoryBits(fixa)==cba && categoryBits(fixb)==cbb;
	}

	public static boolean checkContactByMask(Fixture fixa, Fixture fixb, short mba, short mbb) {
		return maskBits(fixa)==mba && maskBits(fixb)==mbb;
	}

	public static boolean checkContactByGroup(Fixture fixa, Fixture fixb, short gia, short gib) {
		return groupIndex(fixa)==gia && groupIndex(fixb)==gib;
	}

	public static boolean checkContactByCategory(Contact contact, short cba, short cbb) {
		return checkContactByCategory(contact.getFixtureA(), contact.getFixtureB(), cba, cbb);
	}

	public static boolean checkContactByMask(Contact contact, short mba, short mbb) {
		return checkContactByMask(contact.getFixtureA(), contact.getFixtureB(), mba, mbb);
	}

	public static boolean checkContactByGroup(Contact contact, short gia, short gib) {
		return checkContactByGroup(contact.getFixtureA(), contact.getFixtureB(), gia, gib);
	}

	public static boolean checkUnorderedContactByCategory(Fixture fixa, Fixture fixb, short cba, short cbb) {
		return (categoryBits(fixa)==cba && categoryBits(fixb)==cbb) || (categoryBits(fixa)==cbb && categoryBits(fixb)==cba);
	}

	public static boolean checkUnorderedContactByMask(Fixture fixa, Fixture fixb, short mba, short mbb) {
		return (maskBits(fixa)==mba && maskBits(fixb)==mbb) || (maskBits(fixa)==mbb && maskBits(fixb)==mba);
	}

	public static boolean checkUnorderedContactByGroup(Fixture fixa, Fixture fixb, short gia, short gib) {
		return (groupIndex(fixa)==gia && groupIndex(fixb)==gib) || (maskBits(fixa)==gib && maskBits(fixb)==gia);
	}

	public static boolean checkUnorderedContactByCategory(Contact contact, short cba, short cbb) {
		return checkUnorderedContactByGroup(contact.getFixtureA(), contact.getFixtureB(), cba, cbb);
	}

	public static boolean checkUnorderedContactByMask(Contact contact, short mba, short mbb) {
		return checkUnorderedContactByMask(contact.getFixtureA(), contact.getFixtureB(), mba, mbb);
	}

	public static boolean checkUnorderedContactByGroup(Contact contact, short gia, short gib) {
		return checkUnorderedContactByGroup(contact.getFixtureA(), contact.getFixtureB(), gia, gib);
	}

	/************* Color utilities ************/

	public static void setAlpha(Color clr, float a) {
		clr.a = a;
	}

	/** @return HSL hue of a color. */
	public static float getHue(Color clr) {
		return fatan2((float)(SQRT3 * (clr.g-clr.b)), 2*clr.r - clr.g - clr.b);
	}

	/** @return HSL hue of a color. */
	public static float getSat(Color clr) {
		float min = tmin(clr.r, clr.g, clr.b), max = tmax(clr.r, clr.g, clr.b);
		return (max - min) / max;
	}

	/** @return HSL hue of a color. */
	public static float getLgt(Color clr) {
		return tmax(clr.r, clr.g, clr.b);
	}

	public static Vector3 getHsl(Color clr) {
		return tmpv3.set(getHue(clr), getSat(clr), getLgt(clr));
	}

	/** Sets HSL hue. */
	public static Color setHue(Color clr, float h) {
		float r = clr.r, g = clr.g, b = clr.b, u = (float)cos(h * DEGREE), w = (float)sin(h * DEGREE);
		return clr.set((.299f + .701f * u+.168f*w)*r + (.587f-.587f*u+.330f*w)*g + (.114f-.114f*u-.497f*w)*b,
				(.299f-.299f*u-.328f*w)*r + (.587f+.413f*u+.035f*w)*g + (.114f-.114f*u+.292f*w)*b,
				(.299f-.3f*u+1.25f*w)*r + (.587f-.588f*u-1.05f*w)*g + (.114f+.886f*u-.203f*w)*b,
				clr.a);
	}

	/** Sets HSL saturation. */
	public static Color setSat(Color clr, float s) {
		float r = clr.r, g = clr.g, b = clr.b, min = tmin(clr.r, clr.g, clr.b), max = tmax(clr.r, clr.g, clr.b), amin = (1-s)*max;
		return clr.set(r==max ? r : r==min ? amin : amin + (max-amin) * ((r-min)/(max-min)),
		g==max ? g : g==min ? amin : amin + (max-amin) * ((g-min)/(max-min)),
		b==max ? b : b==min ? amin : amin + (max-amin) * ((b-min)/(max-min)),
		clr.a);
	}

	/** Sets HSL lightness. */
	public static Color setLgt(Color clr, float l) {
		return clr.mul(l / getLgt(clr));
	}

	public static Vector3 HslToRgb(float h, float s, float l) {
		float hs = fract(h)*6, f = fract(hs), p = l*(1-s), q = l*(1-f*s), t = l*(1-(1-f)*s);
		switch ((int)hs) {
			case 0: return tmpv3.set(l,t,p);
			case 1: return tmpv3.set(q,l,p);
			case 2: return tmpv3.set(p,l,t);
			case 3: return tmpv3.set(p,q,l);
			case 4: return tmpv3.set(t,p,l);
			case 5: return tmpv3.set(l,p,q);
			default: illegalArgument("hs = "+hs); return null;
		}
	}

	public static Vector3 HslToRgb(Vector3 hsl) {
		return HslToRgb(hsl.x, hsl.y, hsl.z);
	}

	public static Color setHsl(Color clr, float h, float s, float l) {
		tmpv3.set(HslToRgb(h,s,l));
		return clr.set(tmpv3.x, tmpv3.y, tmpv3.z, clr.a);
	}

	/************** Math utilities ************/

	public static float fsin(float a)				{return (float)sin(a);}
	public static float fcos(float a)				{return (float)cos(a);}
	public static float ftan(float a)				{return (float)tan(a);}
	public static float fasin(float a)				{return (float)asin(a);}
	public static float facos(float a)				{return (float)acos(a);}
	public static float fatan(float a)				{return (float)atan(a);}
	public static float fexp(float a)				{return (float)exp(a);}
	public static float flog(float a)				{return (float)log(a);}
	public static float flog10(float a)				{return (float)log10(a);}
	public static float fsqrt(float a)				{return (float)sqrt(a);}
	public static float fcbrt(float a)				{return (float)cbrt(a);}
	public static float fceil(float a)				{return (float)ceil(a);}
	public static float ffloor(float a)				{return (float)floor(a);}
	public static float frint(float a)				{return (float)rint(a);}
	public static float fatan2(float y, float x)	{return (float)atan2(y,x);}
	public static float fpow(float a, float b)		{return (float)pow(a, b);}
	public static float fsinh(float x)				{return (float)sinh(x);}
	public static float fcosh(float x)				{return (float)cosh(x);}
	public static float ftanh(float x)				{return (float)tanh(x);}
	public static float fhypot(float x, float y)	{return (float)hypot(x, y);}
	
	/** @return if {@code flag} intersects {@code flags}. {@code flag} has to be one single bit. */
	public static boolean hasFlag(int flags, int flag) {
		return (flags&flag)!=0;
	}
	
	/** @return if {@code flag} intersects {@code flags}. {@code flag} has to be one single bit. */
	public static boolean hasFlag(long flags, long flag) {
		return (flags&flag)!=0;
	}
	
	/** @return a union of all flags preceding {@code flag}.
	 * @author Nayuki */
	public static int sumFlags(int flag) {
		return flag*2-1;
	}

	/** @return a union of all flags preceding {@code flag}.
	 * @author Nayuki */
	public static long sumFlags(long flag) {
		return flag*2-1;
	}

	/** @return binary shift of {@code a}. */
	public static int getShift(int a) {
		return (int)(log(a)/LOG2);
	}

	/** @return binary shift of {@code a}. */
	public static long getShift(long a) {
		return (long)(log(a)/LOG2);
	}

	/** @return random double in a range from 0 to 1. */
	public static double rand() {
		return random();
	}

	/** @return random double in a range from 0 to {@code a}. */
	public static double rand(double a) {
		return a*rand();
	}

	/** @return random double in a range from {@code a} to {@code b}. */
	public static double rand(double a, double b) {
		return a+(b-a)*rand();
	}

	/** @return random float in a range from 0 to 1. */
	public static float randFloat() {
		return (float)random();
	}

	/** @return random float in a range from 0 to {@code a}. */
	public static float randFloat(float a) {
		return a*randFloat();
	}

	/** @return random float in a range from {@code a} to {@code b}. */
	public static float randFloat(float a, float b) {
		return a+(b-a)*randFloat();
	}

	/** @return random int in a range from 0 to {@code a}. */
	public static int randInt(int a) {
		return round(randFloat(a));
	}

	/** @return random int in a range from {@code a} to {@code b}. */
	public static int randInt(int a, int b) {
		return round(randFloat(a,b));
	}

	/** @return random bool, either true or false. */
	public static boolean randBool() {
		return randInt(1) > 0;
	}

	/** @return is {@code a} even.
	 * @author Nayuki */
	public static boolean isEven(int a) {
		return (a&1)==0;
	}

	/** @return is {@code a} even.
	 * @author Nayuki */
	public static boolean isEven(long a) {
		return (a&1)==0;
	}

	/** @return is {@code a} even. */
	public static boolean isEven(float a) {
		return (dec(a)%2)==0;
	}

	/** @return is {@code a} even. */
	public static boolean isEven(double a) {
		return (dec(a)%2)==0;
	}

	/** @return is {@code a} decimal. */
	public static boolean isDec(float a) {
		return fract(a)==0;
	}

	/** @return is {@code a} decimal. */
	public static boolean isDec(double a) {
		return fract(a)==0;
	}
	
	/** @return the fraction of {@code a}. */
	public static float fract(float a) {
		return a%1;
	}
	
	/** @return the fraction of {@code a}. */
	public static double fract(double a) {
		return a%1;
	}

	/** @return the fraction of a vector. */
	public static Vector2 fract(Vector2 v2) {
		return tmpv2.set(fract(v2.x), fract(v2.y));
	}

	/** @return the fraction of a vector. */
	public static Vector3 fract(Vector3 v3) {
		return tmpv3.set(fract(v3.x), fract(v3.y), fract(v3.z));
	}

	/** @return the decimal part of {@code a}. */
	public static float dec(float a) {
		return a-fract(a);
	}

	/** @return the decimal part of {@code a}. */
	public static double dec(double a) {
		return a-fract(a);
	}

	/** @return float floor of a vector. */
	public static Vector2 ffloor(Vector2 v2) {
		return tmpv2.set(ffloor(v2.x), ffloor(v2.y));
	}

	/** @return float floor of a vector. */
	public static Vector3 ffloor(Vector3 v3) {
		return tmpv3.set(ffloor(v3.x), ffloor(v3.y), ffloor(v3.z));
	}
	
	/** @return {@code a} bounded to {@code min} and {@code max}. */
	public static int clamp(int a, int min, int max) {
		return a<min ? min : a>max?max:a;
	}

	/** @return {@code a} bounded to {@code min} and {@code max}. */
	public static long clamp(long a, long min, long max) {
		return a<min ? min : a>max?max:a;
	}

	/** @return {@code a} bounded to {@code min} and {@code max}. */
	public static double clamp(double a, double min, double max) {
		return a<min ? min : a>max?max:a;
	}

	/** @return {@code a} bounded to {@code min} and {@code max}. */
	public static float clamp(float a, float min, float max) {
		return a<min ? min : a>max?max:a;
	}

	/** @return the greatest of the three arguments. */
	public static int tmax(int a, int b, int c) {
		return max(a, max(b, c));
	}

	/** @return the greatest of the three arguments. */
	public static long tmax(long a, long b, long c) {
		return max(a, max(b, c));
	}

	/** @return the greatest of the three arguments. */
	public static float tmax(float a, float b, float c) {
		return max(a, max(b, c));
	}

	/** @return the greatest of the three arguments. */
	public static double tmax(double a, double b, double c) {
		return max(a, max(b, c));
	}

	/** @return the smallest of the three arguments. */
	public static int tmin(int a, int b, int c) {
		return min(a, min(b, c));
	}

	/** @return the smallest of the three arguments. */
	public static long tmin(long a, long b, long c) {
		return min(a, min(b, c));
	}

	/** @return the smallest of the three arguments. */
	public static float tmin(float a, float b, float c) {
		return min(a, min(b, c));
	}

	/** @return the smallest of the three arguments. */
	public static double tmin(double a, double b, double c) {
		return min(a, min(b, c));
	}

	/** @return the absolute values. */
	public static Vector2 abs(Vector2 v2) {
		return tmpv2.set(Math.abs(v2.x), Math.abs(v2.y));
	}

	/** @return the absolute values. */
	public static Vector3 abs(Vector3 v3) {
		return v3.set(Math.abs(v3.x), Math.abs(v3.y), Math.abs(v3.z));
	}

	/** @return sine of {@code a} multiplied by {@code m}. */
	public static double msin(double a, double m) {
		return sin(a)*m;
	}

	/** @return cosine of {@code a} multiplied by {@code m}. */
	public static double mcos(double a, double m) {
		return cos(a)*m;
	}

	/** @return sine of {@code a} multiplied by {@code m}. */
	public static float msin(double a, float m) {
		return (float)sin(a)*m;
	}

	/** @return cosine of {@code a} multiplied by {@code m}. */
	public static float mcos(double a, float m) {
		return (float)cos(a)*m;
	}

	/** @return multiplication of {@code a} by {@code n} divided by {@code d}. */
	public static float div(float a, float n, float d) {
		return a*(n/d);
	}

	/** @return multiplication of {@code a} by {@code n} divided by {@code d}. */
	public static double div(double a, double n, double d) {
		return a*(n/d);
	}

	/** @return is {@code a} divisible by {@code b}. */
	public static boolean isDivBy(int a, int b) {
		return a%b==0;
	}

	/** @return is {@code a} divisible by {@code b}. */
	public static boolean isDivBy(float a, float b) {
		return a%b==0;
	}

	/** @return is {@code a} divisible by {@code b}. */
	public static boolean isDivBy(double a, double b) {
		return a%b==0;
	}

	/** @return distance between two given points. */
	public static float dist(float x1, float y1, float x2, float y2) {
		return fhypot(x2-x1, y2-y1);
	}

	/** @return distance between two given points. */
	public static float dist(Vector2 p1, Vector2 p2) {
		return dist(p1.x, p1.y, p2.x, p2.y);
	}

	/** @param r radius
	 * @return side of a circumscribed square. */
	public static float circSqrSide(float r) {
		return (float)(r*SQRT2)/2;
	}
	
	public static boolean isPointInRect(float x, float y, float x1, float y1, float x2, float y2) {
		return x>=x1 && x<=x2 && y>=y1 && y<=y2;
	}

	public static boolean isPointOnLine(float x, float y, float x1, float y1, float x2, float y2) {
		return dist(x1,y1, x,y) + dist(x2,y2, x,y) == dist(x1,y1, x2,y2);
	}

	public static boolean isPointInCircle(float x, float y, float x1, float y1, float r) {
		return fhypot(x-x1, y-y1) <= r;
	}

	/** Calculates whether the rectangle defined by the first four arguments intersects the rectangle defined by the last four arguments.
	 * @param x1 x of the bottom left corner of the first rectangle
	 * @param y1 y of the bottom left corner of the first rectangle
	 * @param x2 x of the top right corner of the first rectangle
	 * @param y2 y of the top right corner of the first rectangle
	 * @param x3 x of the bottom left corner of the last rectangle
	 * @param y3 y of the bottom left corner of the last rectangle
	 * @param x4 x of the top right corner of the last rectangle
	 * @param y4 y of the top right corner of the last rectangle
	 * @return whether the rectangle intersects the other rectangle. */
	public static boolean testRectIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		return x1<x4 && x2>x3 && y1<y4 && y2>y3;
	}

	/** Calculates whether the rectangle defined by the first four arguments intersects the line defined by the last four arguments.
	 * @param x1 x of the bottom left corner of the rectangle
	 * @param y1 y of the bottom left corner of the rectangle
	 * @param x2 x of the top right corner of the rectangle
	 * @param y2 y of the top right corner of the rectangle
	 * @return whether the box intersects the line. */
	public static boolean testRectLineIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		if ((x3<=x1 && x4<=x1) ||
			(y3<=y1 && y4<=y1) ||
			(x3>=x2 && x4>=x2) ||
			(y3>=y2 && y4>=y2)) {
			return false;
		}
		if ((x3>x1 && x3<x2 && y3>y1 && y3<y2) ||
			(x4>x1 && x4<x2 && y4>y1 && y4<y2)) {
			return true;
		}
		return false;
	}
	
	/** Calculates whether the rectangle defined by the first four arguments intersects the circle defined by the rest of the arguments.
	 * @param x1 x of the bottom left corner of the rectangle
	 * @param y1 y of the bottom left corner of the rectangle
	 * @param x2 x of the top right corner of the rectangle
	 * @param y2 y of the top right corner of the rectangle
	 * @param x3 x of the circle
	 * @param y3 y of the circle
	 * @param r radius of the circle
	 * @return whether the box intersects the circle. */
	public static boolean testRectCircleIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float r) {
		float width = x2-x1, height = y2-y1, x = x1 + width/2, y = y1 + height/2, cdx = Math.abs(x3-x), cdy = Math.abs(y3-y);
		if (cdx > width/2 + r || cdy > height/2 + r) {
			return false;
		}
		if (cdx <= width/2 || cdy <= height/2) {
			return true;
		}
		return fpow(cdx-width/2,2) + fpow(cdy-height/2,2) <= (fpow(r,2));
	}

	/** Calculates whether the line defined by the first four arguments intersects the line defined by the last four arguments.
	 * @return whether the box intersects the line. */
	public static boolean testLineIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		float bx = x2-x1; 
		float by = y2-y1; 
		float dx = x4-x3; 
		float dy = y4-y3;
		float perp = bx*dy - by*dx;
		if (perp==0) {
			return false;
		}
		float cx = x3-x1;
		float cy = y3-y1;
		float t = (cx*dy - cy*dx) / perp;
		if (t<0 || t>1) {
			return false;
		}
		float u = (cx*by - cy*bx) / perp;
		if (u<0 || u>1) { 
			return false;
		}
		return true;
	}
	
	/** Calculates whether the line defined by the first four arguments intersects the circle defined by the rest of the arguments.
	 * @param x3 x of the circle
	 * @param y3 y of the circle
	 * @param r radius of the circle
	 * @return whether the box intersects the line. */
	public static boolean testLineCircleIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float r) {
		float a = tmpv2.set(x2-x1, y2-y1).dot(tmpv2);
		float b = 2*tmpv2.dot(x1-x3, y1-y3);
		float c = tmpv2.set(x1-x3, y1-y3).dot(tmpv2) - r*r;
		float dsc = b*b-4*a*c;
		if (dsc > 0) {
			dsc = fsqrt(dsc);
			float t1 = (-b - dsc)/(2*a);
			float t2 = (-b + dsc)/(2*a);
			return t1>=0 && t1<=1 || t2>=0 && t2<=t1;
		}
		return false;
	}

	/** Calculates the point of intersection between the line defined by the first four arguments and the line defined by the last four arguments.
	 * @param p intersection point reference
	 * @return whether the intersection occurs. */
	public static boolean lineIntersectionPoint(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Vector2 p) {
		float d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
		if (d==0) {
			return false;
		}
		p.set(((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d, ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d);
		return true;
	}

	/** Calculates the angle between the line defined by the first four arguments and the line defined by the last four arguments.
	 * @return angle in radians. */
	public static double angleBetweenLines(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		return atan2(y1-y2, x1-x2) - atan2(y3-y4, x3-x4);
	}

	/** Calculates the x on a line at the specified y.
	 * @param y y of the point
	 * @return angle in radians. */
	public static float lineXatY(float x1, float y1, float x2, float y2, float y) {
		return y*((x2-x1)/(y2-y1));
	}

	/** Calculates the y on a line at the specified x.
	 * @param x x of the point
	 * @return angle in radians. */
	public static float lineYatX(float x1, float y1, float x2, float y2, float x) {
		return x*((y2-y1)/(x2-x1));
	}

	/** Calculates whether the circle defined by the first three arguments intersects the circle defined by the last three arguments.
	 * @param x3 x of the circle
	 * @param y3 y of the circle
	 * @param r radius of the circle
	 * @return whether the box intersects the line. */
	public static boolean testCircleIntersection(float x1, float y1, float r1, float x2, float y2, float r2) {
		return fhypot(x1-x2, y1-y2) <= (r1+r2);
	}

	/** @param p power
	 * @param a 0 to 1 value
	 * @return exponential interpolation of {@code a} defined by {@code p}. */
	public static float easeIn(float p, float a) {
		return fpow(a,p);
	}

	/** @param a 0 to 1 value
	 * @return quartic exponential interpolation of {@code a}. */
	public static float easeIn(float a) {
		return easeIn(4, a);
	}
	
	/** @param p power
	 * @param a 0 to 1 value
	 * @return exponential interpolation of {@code a} defined by {@code p}. */
	public static float easeOut(float p, float a) {
		float m = fpow(-1,ffloor(p)-1);
		boolean c = p > 2;
		return m * ((a-=(c?1:0)) * (c?fpow(a,p-1):(a-2)) + (c?m:0));
	}

	/** @param a 0 to 1 value
	 * @return quartic exponential interpolation of {@code a}. */
	public static float easeOut(float a) {
		return easeOut(4, a);
	}
	
	/** @param p power
	 * @param a 0 to 1 value
	 * @return exponential interpolation of {@code a} defined by {@code p}. */
	public static float easeInOut(float p, float a) {
		float m = fpow(-1,ffloor(p)-1), b = p>2?2:1;
		if ((a*=2) < 1) {
			return .5f*fpow(a,p);
		}
		return m*.5f * ((a-=b) * (p>2?fpow(a,p-1):a-2) + m*b);
	}

	/** @param a 0 to 1 value
	 * @return quartic exponential interpolation of {@code a}. */
	public static float easeInOut(float a) {
		return easeInOut(4, a);
	}
	
	/** @param p power
	 * @param a 0 to 1 value
	 * @return exponential interpolation of {@code a} defined by {@code p}. */
	public static double easeIn(double p, double a) {
		return pow(a,p);
	}

	/** @param a 0 to 1 value
	 * @return quartic exponential interpolation of {@code a}. */
	public static double easeIn(double a) {
		return easeIn(4, a);
	}
	
	/** @param p power
	 * @param a 0 to 1 value
	 * @return exponential interpolation of {@code a} defined by {@code p}. */
	public static double easeOut(double p, double a) {
		double m = pow(-1,floor(p)-1);
		boolean c = p > 2;
		return m * ((a-=(c?1:0)) * (c?pow(a,p-1):(a-2)) + (c?m:0));
	}

	/** @param a 0 to 1 value
	 * @return quartic exponential interpolation of {@code a}. */
	public static double easeOut(double a) {
		return easeOut(4, a);
	}
	
	/** @param p power
	 * @param a 0 to 1 value
	 * @return exponential interpolation of {@code a} defined by {@code p}. */
	public static double easeInOut(double p, double a) {
		double m = pow(-1,floor(p)-1), b = p>2?2:1;
		if ((a*=2) < 1) {
			return .5*pow(a,p);
		}
		return m*.5 * ((a-=b) * (p>2?pow(a,p-1):a-2) + m*b);
	}

	/** @param a 0 to 1 value
	 * @return quartic exponential interpolation of {@code a}. */
	public static double easeInOut(double a) {
		return easeInOut(4, a);
	}

	/************** Type utilities ************/

	public static Integer	wrap(int a)		{return new Integer(a);}
	public static Long		wrap(long a)	{return new Long(a);}
	public static Float		wrap(float a)	{return new Float(a);}
	public static Double	wrap(double a)	{return new Double(a);}
	public static Boolean	wrap(boolean a)	{return new Boolean(a);}

	/************** JSON utilities ************/

	public static String				toJson(Object obj)						{return (new Json()).toJson(obj);}
	public static <T extends Object> T	readJson(Class<T> type, String data)	{return (new Json()).fromJson(type, data);}
	public static int					JsonInt(JSONObject obj, String key)		{return ((Long)obj.get(key)).intValue();}
	public static short					JsonShort(JSONObject obj, String key)	{return ((Long)obj.get(key)).shortValue();}
	public static long					JsonLong(JSONObject obj, String key)	{return ((Long)obj.get(key)).longValue();}
	public static float					JsonFloat(JSONObject obj, String key)	{return ((Double)obj.get(key)).floatValue();}
	public static double				JsonDouble(JSONObject obj, String key)	{return ((Double)obj.get(key)).doubleValue();}
	public static boolean				JsonBool(JSONObject obj, String key)	{return ((Boolean)obj.get(key)).booleanValue();}
	public static String				JsonString(JSONObject obj, String key)	{return (String)obj.get(key);}
	public static JSONObject			JsonObject(JSONObject obj, String key)	{return (JSONObject)obj.get(key);}
	public static JSONArray				JsonArray(JSONObject obj, String key)	{return (JSONArray)obj.get(key);}
}
