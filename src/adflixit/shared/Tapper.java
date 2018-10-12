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

package adflixit.shared;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Simple widget that utilizes the UI input events handling, preserving the valid UI stage hierarchy.
 * Creates a full-screen layer which receives the input.
 */
public class Tapper extends ScreenComponent<BaseScreen<?>> {
	private final Actor	area		= new Actor();
	private InputListener	listener;

	public Tapper(BaseScreen<?> screen) {
		super(screen);
	}

	public void reset() {
		area.remove();
		area.removeListener(listener);
	}

	public void set(Group parent, int z, final Callback cb, boolean down) {
		parent.addActor(area);
		// set position to the absolute zero
		updatePos();
		area.setZIndex(z);
		if (down) {
			area.addListener(listener = new InputListener() {
				@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					cb.call();
					return true;
				}
			});
		} else {
			area.addListener(listener = new InputListener() {
				@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}

				@Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					super.touchDown(event, x, y, pointer, button);
					cb.call();
				}
			});
		}
	}

	public void setOnce(Group parent, int z, final Callback cb, boolean down) {
		parent.addActor(area);
		// set position to the absolute zero
		updatePos();
		area.setZIndex(z);
		if (down) {
			area.addListener(listener = new InputListener() {
				@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					cb.call();
					reset();
					return true;
				}
			});
		} else {
			area.addListener(listener = new InputListener() {
				@Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				}

				@Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					cb.call();
					reset();
				}
			});
		}
	}

	/** Aligns position by the very root of the UI scene. */
	public void updatePos() {
		Group parent = area.getParent();
		area.setPosition(0, 0);
		while (parent != ui.getRoot()) {
			area.setPosition(area.getX() - parent.getX(), area.getY() - parent.getY());
			parent = parent.getParent();
		}
	}

	public void resize() {
		area.setSize(ui.getWidth(), ui.getHeight());
	}
}
