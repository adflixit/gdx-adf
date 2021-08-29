package adf.gdx.console;

@FunctionalInterface public interface ConCmd {
  public void exec(String... args);
}
