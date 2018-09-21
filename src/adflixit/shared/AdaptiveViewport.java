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

import static java.lang.Math.*;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * {@link Viewport} that scales the larger dimension while preserving the aspect ratio.
 * TODO: apparently doesn't scale correctly when in landscape mode.
 */
public class AdaptiveViewport extends Viewport {
	public AdaptiveViewport(Camera camera) {
		setCamera(camera);
	}

	public AdaptiveViewport() {
		this(new OrthographicCamera());
	}

	public void update(float worldWidth, float worldHeight, int screenWidth, int screenHeight, boolean centerCamera) {
		Vector2 scaled = Scaling.fit.apply(worldWidth, worldHeight, screenWidth, screenHeight);
		int viewportWidth = round(scaled.x), viewportHeight = round(scaled.y);
		if (viewportWidth < screenWidth) {
			float toViewportSpace = viewportHeight / worldHeight;
			float toWorldSpace = worldHeight / viewportHeight;
			float lengthen = (screenWidth - viewportWidth) * toWorldSpace;
			worldWidth += lengthen;
			viewportWidth += round(lengthen * toViewportSpace);
		} else if (viewportHeight < screenHeight) {
			float toViewportSpace = viewportWidth / worldWidth;
			float toWorldSpace = worldWidth / viewportWidth;
			float lengthen = (screenHeight - viewportHeight) * toWorldSpace;
			worldHeight += lengthen;
			viewportHeight += round(lengthen * toViewportSpace);
		}
		setWorldSize(worldWidth, worldHeight);
		setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
		apply(centerCamera);
	}

	public void update(float worldWidth, float worldHeight, int screenWidth, int screenHeight) {
		update(worldWidth, worldHeight, screenWidth, screenHeight, false);
	}
}
