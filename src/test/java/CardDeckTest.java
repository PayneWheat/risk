package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class CardDeckTest extends TestCase {
	Board b = new Board(false);
	public void testDeckSize() throws Exception {
		assertEquals(b.remainingCards(), 44);
	}
	public void testCardDraw() throws Exception {
		for(int i = 0; i < 44; i++) {
			Card c = b.drawCard();
			int value = c.type;
			
			assertTrue(1 <= value && value <= 4);
			assertEquals(b.remainingCards(), 43 - i);
			assertNotNull(c.territoryName);
		}
	} 
}
