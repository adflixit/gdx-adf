package adf.gdx;

import static org.junit.Assert.assertEquals;

import adf.gdx.console.ConVar;
import adf.gdx.console.Console;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleTest {
  private float calc(float a, float b) {
    return (a * b) / (a + b);
  }

  @Test public void testParse() {
    // redirect output
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    PrintStream out = System.out;
    System.setOut(ps);

    Console console = new Console();
    ConVar var = new ConVar("var", "true");
    console.registerCommand("calc", args -> var.set(calc(Float.parseFloat(args[0]), Float.parseFloat(args[1]))));
    console.parse("print poo\n" +
        "print 'poo poo'\n" +
        "print var\n" +
        "var false\n" +
        "print var\n" +
        "calc 5 5\n" +
        "print var");

    System.out.flush();
    System.setOut(out);
    System.out.println(baos.toString());

    assertEquals("poo\r\n" +
        "poo poo\r\n" +
        "true\r\n" +
        "false\r\n" +
        "2.5\r\n", baos.toString());
    console.dispose();
  }

}
