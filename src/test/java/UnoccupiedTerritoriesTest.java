package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class UnoccupiedTerritoriesTest extends TestCase {
	Board b = new Board();
	public void testUnoccupiedTerritoriesCount() throws Exception {
		assertEquals(b.unoccupiedTerritoriesCount(), 42);
	}
	public void testCardDeck() throws Exception {
		int value = b.drawCard().type;
		assertTrue(1 <= value && value <= 4);
	} 
}
