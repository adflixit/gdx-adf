/*
 * Copyright 2018 Adflixit
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package adflixit.shared.misc;

import static adflixit.shared.BaseGame.*;
import static adflixit.shared.BaseScreen.*;
import static adflixit.shared.Defs.*;
import static adflixit.shared.TweenUtils.*;
import static com.badlogic.gdx.graphics.Color.*;

import aurelienribon.tweenengine.Timeline;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * {@link TextButton} {@link ClickListener}. Tweens the TextButton font color and the background color.
 */
public class TbClickListener extends ClickListener {
	private TextButton		btn;
	private Label			label;
	private Color			initBgClr;		// initial background color
	private Color			initFontClr;	// initial font color
	private Color			bgClr;
	private Color			fontClr;

	public TbClickListener(TextButton button, Color initBgClr, Color initFontClr, Color bgClr, Color fontClr) {
		super();
		btn = button;
		label = button.getLabel();
		this.initBgClr = initBgClr != null ? initBgClr : WHITE;
		if (initBgClr != null) {
			button.setColor(initBgClr);
		}
		this.initFontClr = initFontClr != null ? initFontClr : WHITE;
		if (initFontClr != null) {
			label.setColor(initFontClr);
		}
		this.bgClr = bgClr;
		this.fontClr = fontClr;
	}

	public TbClickListener(TextButton button, Color bgClr, Color fontClr) {
		this(button, null, null, bgClr, fontClr);
	}

	public TbClickListener(TextButton button, Color clr) {
		this(button, clr, clr);
	}

	public TbClickListener(TextButton button, String initBgClr, String initFontClr, String bgClr, String fontClr) {
		this(button, color(bgClr), color(initBgClr), color(fontClr), color(initFontClr));
	}

	public TbClickListener(TextButton button, String bgClr, String fontClr) {
		this(button, color(bgClr), color(fontClr));
	}

	public TbClickListener(TextButton button, String clr) {
		this(button, color(clr));
	}

	public TbClickListener(TextButton button) {
		this(button, "blue");
	}

	@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		super.touchDown(event, x, y, pointer, button);
		Timeline.createParallel()
		.push($tweenActorColor(btn, bgClr, C_HD))
		.push($tweenLabelColor(label, fontClr, C_HD))
		.start(tweenMgr);
		return true;
	}

	@Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		super.touchUp(event, x, y, pointer, button);
		Timeline.createParallel()
		.push($tweenActorColor(btn, initBgClr, C_HD))
		.push($tweenLabelColor(label, initFontClr, C_HD))
		.start(tweenMgr);
	}
}
