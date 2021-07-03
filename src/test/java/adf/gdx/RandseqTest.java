package adf.gdx;

import org.junit.Test;

public class RandseqTest {
  public static boolean find(int a, int[] array) {
    for (int i : array) {
      if (i == a) {
        return true;
      }
    }
    return false;
  }

  @Test public void testRandSeq() {
    RandSeq rs = new RandSeq(6);
    System.out.println(rs.get());
    System.out.println(rs.next());
    System.out.println(rs.next());
    System.out.println(rs.next());
    System.out.println(rs.next());
    System.out.println(rs.next());
    System.out.println(rs.next());
    System.out.println(rs.next());
    System.out.println(rs.next());
    //assertEquals(alignX(20, 40, left));
  }
}
