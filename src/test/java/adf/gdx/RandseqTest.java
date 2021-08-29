package adf.gdx;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class RandseqTest {
  @Test public void testRandSeq() {
    RandSeq rs = new RandSeq(10);
    for (int i=0; i < 20; i++) {
      assertNotEquals(rs.get(), rs.next());
    }
  }
}
