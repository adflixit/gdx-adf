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
import static adflixit.shared.BaseScreen.tweenMgr;
import static adflixit.shared.TweenUtils.*;
import static adflixit.shared.Util.*;
import static com.badlogic.gdx.graphics.Color.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import adflixit.shared.ClickCallback;
import aurelienribon.tweenengine.Timeline;

public class TintClickListener extends ClickListener {
    private Actor		actor;
    private Color		initClr;
    private Color		clr;
    private ClickCallback	callback;

    public TintClickListener(Actor actor, Color initClr, Color clr, ClickCallback cb) {
        super();
        this.actor = actor;
        this.initClr = initClr != null ? initClr : WHITE;
        if (initClr != null) {
            actor.setColor(initClr);
        }
        this.clr = clr;
        setCallback(cb);
    }

    public TintClickListener(Actor actor, Color clr, ClickCallback cb) {
        this(actor, null, clr, cb);
    }

    public TintClickListener(Actor actor, String initClr, String clr, ClickCallback cb) {
        this(actor, color(initClr), color(clr), cb);
    }

    public TintClickListener(Actor actor, String clr, ClickCallback cb) {
        this(actor, color(clr), cb);
    }

    public TintClickListener(Actor actor, ClickCallback cb) {
        this(actor, "blue", cb);
    }

    public void setCallback(ClickCallback cb) {
        callback = cb;
    }

    protected void fadeIn() {
        tweenActorColor(actor, clr, C_HD);
    }

    protected void fadeOut() {
        tweenActorColor(actor, initClr, C_HD);
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
