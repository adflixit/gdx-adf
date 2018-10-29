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

package adflixit.shared.tests.app;

import adflixit.shared.BaseGame;
import adflixit.shared.XApi;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TestApp extends BaseGame {
  public TestApp() {
    super();
  }

  public TestApp(XApi xApi) {
    super(xApi);
  }

  @Override public void create() {
    super.create();
    initSkin(internalFile("tests/data/uiskin.json"), new TextureAtlas("tests/data/textures.atlas"));
    loadProps(localFile("tests/data/cfg.properties"));
    loadPrefs("test");
    //Gdx.input.setCatchMenuKey(true);
    Gdx.input.setCatchBackKey(true);
  }

  public static void launch(TestApp app) {
    new LwjglApplication(app, "Test App", 360, 640);
  }
}
