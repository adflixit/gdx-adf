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

package adflixit.shared.console;

import static adflixit.shared.Util.arrayToStringf;

import com.badlogic.gdx.files.FileHandle;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {
  private boolean                   active  = true;
  private Thread                    thread;
  private InputStream               in;
  private PrintStream               out;
  private final Scanner             scanner;
  private final Map<String, ConCmd> cmds    = new HashMap<>();
  private final Map<String, ConVar> vars    = new HashMap<>();
  private final List<String>        parsed  = new ArrayList<>();

  public Console(InputStream sin, PrintStream sout) {
    in = sin;
    out = sout;
    scanner = new Scanner(in);

    thread = new Thread("Console") {
      @Override public void run() {
        read();
      }
    };
    thread.start();

    registerCommand("print", args -> print(arrayToStringf("%s ", args)));
    registerCommand("reset", args -> var(args[0]).reset());
    registerCommand("help", args -> print(cmds.keySet().toString()));
  }

  public Console() {
    this(System.in, System.out);
  }

  public void registerCommand(String name, ConCmd cmd) {
    if (cmds.get(name) != null) {
      throw new RuntimeException("Command '"+name+"' already exists.");
    } else {
      cmds.put(name, cmd);
    }
  }

  public void registerVariable(String name, ConVar var) {
    if (vars.get(name) != null) {
      throw new RuntimeException("Variable '"+name+"' already exists.");
    } else {
      vars.put(name, var);
    }
  }

  public ConCmd cmd(String name) {
    return cmds.get(name);
  }

  public ConVar var(String name) {
    return vars.get(name);
  }

  /**
   * Loads a file and evaluates it's contents as a command sequence.
   * @see {@link #eval(String)}
   */
  public void load(FileHandle file) {
    try {
      parse(file.readString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Interprets given text as a command.
   * Reads the first word (a sequence of characters between the beginning of the line and a whitespace)
   * as a command or variable name.
   * The rest of of the words or sentences (text bound by two quotation marks)
   * are used as arguments to the specified command.
   * If no command found with the given name, if a variable with the specified name exists
   * it will be set to the first argument.
   */
  private synchronized void eval(String line) {
    if (line == null) {
      throw new IllegalArgumentException("An evaluated line cannot be null.");
    }

    // parsing the input into either whole words or text bounded by quotation marks
    parsed.clear();
    Matcher m = Pattern.compile("([^\"']\\S*|[\"'].+?[\"'])\\s*").matcher(line);
    // removing quotation marks from grouped text
    while (m.find()) {
      parsed.add(m.group(1).replaceAll("[\"']", ""));
    }

    // searching for a command or a variable matching the name
    ConCmd cmd = cmds.get(parsed.get(0));
    ConVar var = vars.get(parsed.get(0));

    if (cmd == null && var == null) {
      // if nothing found
      print("Unknown command: "+parsed.get(0));
    } else if (cmd != null) {
      // if a command with this name is found, it will be called
      cmd.exec(parsed.subList(1, parsed.size()).toArray(new String[0]));
    } else {
      // if no command found, it will be used to set a variable
      var.set(parsed.get(1));
    }
  }

  /**
   * Evaluates given text as a command sequence.
   */
  public synchronized void parse(String data) {
    String[] spl = data.split("\\r?\\n|;");
    for (String s : spl) {
      eval(s.trim());
    }
  }

  /**
   * Reads input from {@link #in}.
   */
  private synchronized void read() {
    while (active) {
      if (!scanner.hasNextLine()) {
        continue;
      }

      String line = scanner.nextLine();
      if (line.equals("")) {
        continue;
      }

      parse(line);
    }
  }

  public void dispose() {
    active = false;
  }

  public void print(String msg) {
    out.println(msg);
  }
}
