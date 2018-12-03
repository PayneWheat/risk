package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class TwitterTest extends TestCase {
	Board b = new Board(false, true);
	public void testTwitter() {
		Twitter4j tw = new Twitter4j();
		tw.PostTwitter("Testing");
	}
}
