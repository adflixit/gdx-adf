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

import static adflixit.shared.BaseGame.internalFile;

import adflixit.shared.BaseScreen;

public class TestAppScreen extends BaseScreen<TestApp> {
	public TestAppScreen(TestApp game) {
		super(game);
		String dir = "assets/data/";
		blur.load(internalFile(dir+"hblur.vert"), internalFile(dir+"blur.frag"),
				internalFile(dir+"vblur.vert"), internalFile(dir+"blur.frag"));
	}

	@Override public void goBack() {
	}
}
