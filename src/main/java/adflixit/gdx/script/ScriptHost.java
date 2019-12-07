package adflixit.gdx.script;

import com.badlogic.gdx.files.FileHandle;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptHost {
  private boolean                   active  = true;
  private Thread                    thread;
  private final ScriptEngineManager manager = new ScriptEngineManager();
  private final ScriptEngine        engine;     // current scripting language interpreter
  private final Invocable           invEngine;  // method invocation unit
  private final Bindings            bindings;   //
  private final ScriptBridge        bridge;     //

  public ScriptHost(ScriptApi api, ScriptBridge bridge, String engineName) {
    engine = manager.getEngineByName(engineName);
    invEngine = (Invocable)engine;
    bindings = engine.createBindings();
    bindings.put("Api", api);
    engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    this.bridge = bridge;

    thread = new Thread("Script Host") {
      @Override public void run() {
        
      }
    };
    thread.start();
  }

  public ScriptHost(ScriptApi api, ScriptBridge bridge) {
    this(api, bridge, "JavaScript");
  }

  public void load(String script) {
    try {
      engine.eval(script);
    } catch (Exception e) {
      bridge.handleException(e);
    }
  }

  public void load(FileHandle file) {
    try {
      load(file.readString());
    } catch (Exception e) {
      bridge.handleException(e);
    }
  }

  public void call(String name, Object... args) {
    try {
      invEngine.invokeFunction(name, args);
    } catch (Exception e) {
      bridge.handleException(e);
    }
  }
}
