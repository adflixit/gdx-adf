/*
 * Copyright 2019 Adflixit
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

import static adflixit.shared.Logger.*;

import adflixit.shared.BaseContext;

public class TestAppContext extends BaseContext<TestApp> {
  private class Context extends TestAppContext {
    public Context(TestApp game) {
      super(game);
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      super.touchDown(screenX, screenY, pointer, button);
      log(touch.x+" "+touch.y);
      return true;
    }
  }

  public TestAppContext(TestApp game) {
    super(game);
    blur.loadDefault();
  }

  @Override public void goBackAction() {
  }
}
