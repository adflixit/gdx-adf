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
import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;
import static com.badlogic.gdx.graphics.Color.*;

import aurelienribon.tweenengine.Timeline;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import adflixit.shared.ClickCallback;

/**
 * {@link ImageButton} {@link ClickListener}. Tweens the ImageButton image color and the background color.
 */
public class IbClickListener extends ClickListener {
    private ImageButton	btn;
    private Image		image;
    private Color		initBgClr;	// initial background color
    private Color		initImageClr;	// initial image color
    private Color		bgClr;
    private Color		imageClr;
    private ClickCallback	callback;

    public IbClickListener(ImageButton button, Color initBgClr, Color initImageClr, Color bgClr, Color imageClr, ClickCallback cb) {
        super();
        btn = button;
        image = button.getImage();
        this.initBgClr = initBgClr != null ? initBgClr : WHITE;
        if (initBgClr != null) {
            button.setColor(initBgClr);
        }
        this.initImageClr = initImageClr != null ? initImageClr : WHITE;
        if (initImageClr != null) {
            image.setColor(initImageClr);
        }
        this.bgClr = bgClr;
        this.imageClr = imageClr;
        setCallback(cb);
    }

    public IbClickListener(ImageButton button, Color bgClr, Color imageClr, ClickCallback cb) {
        this(button, null, null, bgClr, imageClr, cb);
    }

    public IbClickListener(ImageButton button, Color clr, ClickCallback cb) {
        this(button, clr, clr, cb);
    }

    public IbClickListener(ImageButton button, String initBgClr, String initImageClr, String bgClr, String imageClr, ClickCallback cb) {
        this(button, color(bgClr), color(initBgClr), color(imageClr), color(initImageClr), cb);
    }

    public IbClickListener(ImageButton button, String bgClr, String imageClr, ClickCallback cb) {
        this(button, color(bgClr), color(imageClr), cb);
    }

    public IbClickListener(ImageButton button, String clr, ClickCallback cb) {
        this(button, color(clr), cb);
    }

    public IbClickListener(ImageButton button, ClickCallback cb) {
        this(button, "blue", cb);
    }

    public void setCallback(ClickCallback cb) {
        callback = cb;
    }

    public void fadeIn() {
        Timeline.createParallel()
        .push($tweenActorColor(btn, bgClr, C_HD))
        .push($tweenActorColor(image, imageClr, C_HD))
        .start(tweenMgr);
    }

    public void fadeOut() {
        Timeline.createParallel()
        .push($tweenActorColor(btn, initBgClr, C_HD))
        .push($tweenActorColor(image, initImageClr, C_HD))
        .start(tweenMgr);
    }

    @Override public void clicked(InputEvent event, float x, float y) {
        callback.clicked(event, x, y);
    }

    @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        super.touchDown(event, x, y, pointer, button);
        fadeIn();
        return true;
    }

    @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
        fadeOut();
    }

    @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        super.enter(event, x, y, pointer, fromActor);
        fadeIn();
    }

    @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.exit(event, x, y, pointer, toActor);
        fadeOut();
    }
}
