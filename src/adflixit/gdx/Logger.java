package adflixit.gdx;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public class Logger {
  private Logger() {}

  public static final void log(String msg) {
    if (Gdx.app.getType() == ApplicationType.Android) {
      Gdx.app.log("App", msg);
    } else {
      System.out.println(msg);
    }
  }

  public static final void logSetup(String msg) {
    if (Gdx.app.getType() == ApplicationType.Android) {
      Gdx.app.log("App", msg);
    } else {
      System.out.print(msg);
    }
  }

  public static final void logDone() {
    if (Gdx.app.getType() == ApplicationType.Android) {
      Gdx.app.log("App", "Done");
    } else {
      System.out.print(" -> done\n");
    }
  }

  public static final void warning(String msg) {
    log("\u001B[31m"+msg+"\u001B[0m");
  }
}
