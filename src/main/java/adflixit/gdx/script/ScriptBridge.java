package adflixit.gdx.script;

/**
 * Part of the script host used to interact with the environment.
 * API of functions not accessible through scripts, unlike {@link ScriptApi}.
 */
public interface ScriptBridge {
  public void handleException(Exception e);
}
