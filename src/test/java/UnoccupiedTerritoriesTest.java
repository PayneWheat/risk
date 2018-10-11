package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class UnoccupiedTerritoriesTest extends TestCase {
	Board b = new Board(false);
	public void testUnoccupiedTerritoriesCount() throws Exception {
		assertEquals(b.unoccupiedTerritoriesCount(), 42);
	}

}
