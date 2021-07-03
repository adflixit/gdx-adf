package adf.gdx.tools;

import static adf.gdx.SceneUtil.*;

import adf.gdx.BaseAppListener;
import adf.gdx.BaseContext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class Shadow extends BaseAppListener {
  public class Screen extends BaseContext<Shadow> {
    private Group       root        = new Group();
    private Slider      slRadius    = new Slider(0, 128, 1, false, skin());
    private TextField   tfRadius    = new TextField("8", skin());
    private Slider      slOpacity   = new Slider(0, 32, 1, false, skin());
    private TextField   tfOpacity   = new TextField("8", skin());
    private TextField   tfColor     = new TextField("#000000", skin());

    public Screen(Shadow al) {
      super(al);
      addToUiLayer(UI_MENUS, root);
      addActors(root, slRadius, tfRadius, slOpacity, tfOpacity, tfColor);
    }

    @Override public void resize() {
      super.resize();
      root.setSize(screenWidth(), screenHeight());
      float width = screenWidth()/3, height = 100;
      slRadius.setPosition(screenCenterX() - width*1.5f, height/2);
      tfRadius.setPosition(screenCenterX() - width*1.5f, height/2 + height/2);
      slOpacity.setPosition(screenCenterX() - width/2, height/2);
      tfOpacity.setPosition(screenCenterX() - width/2, height/2 + height/2);
      tfColor.setPosition(screenCenterX() + width/2, height/2);
    }
  }

  @Override public void create() {
    super.create();
    loadSkin(internalFile("tools/uiskin.json"), internalFile("tools/textures.atlas"));
    convertToDropShadow(skin(), "default-vertical", "slider-knob_shd");
    convertToDropShadow(skin(), "default-horizontal", "slider-knob_shd");
    Gdx.input.setCatchKey(Input.Keys.BACK, true);
    setContext(new Screen(this));
  }

  public static void main(String[] argv) {
    new LwjglApplication(new Shadow());
  }
}
