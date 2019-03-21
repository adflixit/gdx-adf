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

package adflixit.shared;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import java.util.HashMap;
import java.util.Map;

public class KeyBindings {
  public static final Map<String, Integer> keys = new HashMap<>();
  static {
    keys.put("ESCAPE",        Keys.ESCAPE);
    keys.put("F1",            Keys.F1);
    keys.put("F2",            Keys.F2);
    keys.put("F3",            Keys.F3);
    keys.put("F4",            Keys.F4);
    keys.put("F5",            Keys.F5);
    keys.put("F6",            Keys.F6);
    keys.put("F7",            Keys.F7);
    keys.put("F8",            Keys.F8);
    keys.put("F9",            Keys.F9);
    keys.put("F10",           Keys.F10);
    keys.put("F11",           Keys.F11);
    keys.put("F12",           Keys.F12);
    keys.put("`",             Keys.APOSTROPHE);
    keys.put("1",             Keys.NUM_1);
    keys.put("2",             Keys.NUM_2);
    keys.put("3",             Keys.NUM_3);
    keys.put("4",             Keys.NUM_4);
    keys.put("5",             Keys.NUM_5);
    keys.put("6",             Keys.NUM_6);
    keys.put("7",             Keys.NUM_7);
    keys.put("8",             Keys.NUM_8);
    keys.put("9",             Keys.NUM_9);
    keys.put("0",             Keys.NUM_0);
    keys.put("-",             Keys.MINUS);
    keys.put("=",             Keys.EQUALS);
    keys.put("BACKSPACE",     Keys.BACKSPACE);
    keys.put("TAB",           Keys.TAB);
    keys.put("Q",             Keys.Q);
    keys.put("W",             Keys.W);
    keys.put("E",             Keys.E);
    keys.put("R",             Keys.R);
    keys.put("T",             Keys.T);
    keys.put("Y",             Keys.Y);
    keys.put("U",             Keys.U);
    keys.put("I",             Keys.I);
    keys.put("O",             Keys.O);
    keys.put("P",             Keys.P);
    keys.put("[",             Keys.LEFT_BRACKET);
    keys.put("]",             Keys.RIGHT_BRACKET);
    keys.put("\\",            Keys.BACKSLASH);
    keys.put("A",             Keys.A);
    keys.put("S",             Keys.S);
    keys.put("D",             Keys.D);
    keys.put("F",             Keys.F);
    keys.put("G",             Keys.G);
    keys.put("H",             Keys.H);
    keys.put("J",             Keys.J);
    keys.put("K",             Keys.K);
    keys.put("L",             Keys.L);
    keys.put(";",             Keys.SEMICOLON);
    keys.put("ENTER",         Keys.ENTER);
    keys.put("SHIFT",         Keys.SHIFT_LEFT);
    keys.put("Z",             Keys.Z);
    keys.put("X",             Keys.X);
    keys.put("C",             Keys.C);
    keys.put("V",             Keys.V);
    keys.put("B",             Keys.B);
    keys.put("N",             Keys.N);
    keys.put("M",             Keys.M);
    keys.put(",",             Keys.COMMA);
    keys.put(".",             Keys.PERIOD);
    keys.put("/",             Keys.SLASH);
    keys.put("RSHIFT",        Keys.SHIFT_RIGHT);
    keys.put("CTRL",          Keys.CONTROL_LEFT);
    keys.put("ALT",           Keys.ALT_LEFT);
    keys.put("SPACE",         Keys.SPACE);
    keys.put("RALT",          Keys.ALT_RIGHT);
    keys.put("RCTRL",         Keys.CONTROL_RIGHT);
    keys.put("INS",           Keys.INSERT);
    keys.put("HOME",          Keys.HOME);
    keys.put("PGUP",          Keys.PAGE_UP);
    keys.put("DEL",           Keys.DEL);
    keys.put("END",           Keys.END);
    keys.put("PGDN",          Keys.PAGE_DOWN);
    keys.put("UPARROW",       Keys.UP);
    keys.put("LEFTARROW",     Keys.LEFT);
    keys.put("DOWNARROW",     Keys.DOWN);
    keys.put("RIGHTARROW",    Keys.RIGHT);
    keys.put("KP_7",          Keys.NUMPAD_7);
    keys.put("KP_8",          Keys.NUMPAD_8);
    keys.put("KP_9",          Keys.NUMPAD_9);
    keys.put("KP_4",          Keys.NUMPAD_4);
    keys.put("KP_5",          Keys.NUMPAD_5);
    keys.put("KP_6",          Keys.NUMPAD_6);
    keys.put("KP_1",          Keys.NUMPAD_1);
    keys.put("KP_2",          Keys.NUMPAD_2);
    keys.put("KP_3",          Keys.NUMPAD_3);
    keys.put("KP_0",          Keys.NUMPAD_0);
  }

  public KeyBindings() {

  }

  public void bind () {

  }
}
