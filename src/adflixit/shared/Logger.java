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

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

/**
 * An abstract class that holds the basic logging functionality, such as printing debug info with the current class signature.
 */
public abstract class Logger {
	public static final void glog(String msg) {
		if (Gdx.app.getType()==ApplicationType.Android) {
			Gdx.app.log("App", msg);
		} else {
			System.out.println(msg);
		}
	}
	
	public static final void glogSetup(String msg) {
		if (Gdx.app.getType()==ApplicationType.Android) {
			Gdx.app.log("App", msg);
		} else {
			System.out.print(msg);
		}
	}

	public static final void glogDone(String msg) {
		if (Gdx.app.getType()==ApplicationType.Android) {
			Gdx.app.log("App", msg);
		} else {
			System.out.print(" -> done\n");
		}
	}

	public final void log(String msg) {
		if (Gdx.app.getType()==ApplicationType.Android) {
			Gdx.app.log("App", getClass().getSimpleName()+": "+msg);
		} else {
			System.out.println(getClass().getSimpleName()+": "+msg);
		}
	}
	
	public final void logSetup(String msg) {
		if (Gdx.app.getType()==ApplicationType.Android) {
			Gdx.app.log("App", getClass().getSimpleName()+": "+msg);
		} else {
			System.out.print(getClass().getSimpleName()+": "+msg);
		}
	}

	public final void logDone(String msg) {
		if (Gdx.app.getType()==ApplicationType.Android) {
			Gdx.app.log("App", getClass().getSimpleName()+": "+msg);
		} else {
			System.out.print(" -> done\n");
		}
	}
}
