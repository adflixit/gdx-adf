package adf.gdx;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Filter;

public final class Box2DUtil {
  private Box2DUtil() {}

  /**
   * @return {@link Filter#categoryBits} of a Box2D {@link Fixture}.
   */
  public static short categoryBits(Fixture fix) {
    return fix.getFilterData().categoryBits;
  }

  /**
   * @return {@link Filter#maskBits} of a Box2D {@link Fixture}.
   */
  public static short maskBits(Fixture fix) {
    return fix.getFilterData().maskBits;
  }

  /**
   * @return {@link Filter#groupIndex} of a Box2D {@link Fixture}.
   */
  public static short groupIndex(Fixture fix) {
    return fix.getFilterData().groupIndex;
  }

  public static boolean fixtureIsInContact(Contact contact, Fixture fix) {
    return contact.getFixtureA() == fix || contact.getFixtureB() == fix;
  }

  public static boolean checkContactByCategory(Fixture fixa, Fixture fixb, short cba, short cbb) {
    return categoryBits(fixa) == cba && categoryBits(fixb) == cbb;
  }

  public static boolean checkContactByMask(Fixture fixa, Fixture fixb, short mba, short mbb) {
    return maskBits(fixa) == mba && maskBits(fixb) == mbb;
  }

  public static boolean checkContactByGroup(Fixture fixa, Fixture fixb, short gia, short gib) {
    return groupIndex(fixa) == gia && groupIndex(fixb) == gib;
  }

  public static boolean checkContactByCategory(Contact contact, short cba, short cbb) {
    return checkContactByCategory(contact.getFixtureA(), contact.getFixtureB(), cba, cbb);
  }

  public static boolean checkContactByMask(Contact contact, short mba, short mbb) {
    return checkContactByMask(contact.getFixtureA(), contact.getFixtureB(), mba, mbb);
  }

  public static boolean checkContactByGroup(Contact contact, short gia, short gib) {
    return checkContactByGroup(contact.getFixtureA(), contact.getFixtureB(), gia, gib);
  }

  public static boolean checkUnorderedContactByCategory(Fixture fixa, Fixture fixb, short cba, short cbb) {
    return checkContactByCategory(fixa, fixb, cba, cbb) || checkContactByCategory(fixa, fixb, cbb, cba);
  }

  public static boolean checkUnorderedContactByMask(Fixture fixa, Fixture fixb, short mba, short mbb) {
    return checkContactByMask(fixa, fixb, mba, mbb) || checkContactByMask(fixa, fixb, mbb, mba);
  }

  public static boolean checkUnorderedContactByGroup(Fixture fixa, Fixture fixb, short gia, short gib) {
    return checkContactByGroup(fixa, fixb, gia, gib) || checkContactByMask(fixa, fixb, gib, gia);
  }

  public static boolean checkUnorderedContactByCategory(Contact contact, short cba, short cbb) {
    return checkUnorderedContactByGroup(contact.getFixtureA(), contact.getFixtureB(), cba, cbb);
  }

  public static boolean checkUnorderedContactByMask(Contact contact, short mba, short mbb) {
    return checkUnorderedContactByMask(contact.getFixtureA(), contact.getFixtureB(), mba, mbb);
  }

  public static boolean checkUnorderedContactByGroup(Contact contact, short gia, short gib) {
    return checkUnorderedContactByGroup(contact.getFixtureA(), contact.getFixtureB(), gia, gib);
  }
}
