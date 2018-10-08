package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class CardDeckTest extends TestCase {
	Board b = new Board();
	public void testDeckSize() throws Exception {
		assertEquals(b.remainingCards(), 44);
	}
	public void testCardDraw() throws Exception {
		int value = b.drawCard().type;
		assertTrue(1 <= value && value <= 4);
		assertEquals(b.remainingCards(), 43);
	} 
}
