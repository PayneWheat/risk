package test.java;
import junit.framework.TestCase;
import java.util.*;
import main.java.game.Card;
import main.java.game.Board;
import main.java.game.Player;

import org.junit.Test;

public class CardTest extends TestCase {
	/*
	public CardTest(String name) {
		super(name);
	}
	*/
	@Test
	public void testGetCardName() throws Exception {
		
		Card card = new Card("Test", (byte)1);
		assertTrue(card.getTerritoryName() == "Test" && card.getCardTypeName() == "Infantry");
		
		card = new Card("Test", (byte)2);
		assertTrue(card.getTerritoryName() == "Test" && card.getCardTypeName() == "Cavalry");
		
		card = new Card("Test", (byte)3);
		assertTrue(card.getTerritoryName() == "Test" && card.getCardTypeName() == "Artillery");
		
		card = new Card("Test", (byte)4);
		assertTrue(card.getTerritoryName() == "Test" && card.getCardTypeName() == "Wild");
		
		card = new Card("Test", (byte)5);
		assertTrue(card.getTerritoryName() == "Test" && card.getCardTypeName() == "Unknown");
	}
	/*
	@Test
	public void testTurnCardsIn() throws Exception {
		Board b = new Board(false);
		//ArrayList<Card> cards = new ArrayList<Card>();
		Player p = new Player("Test1", "Blue", 0, 0);
		// One of each
		for(int i = 1; i <= 3; i++) {
			p.addCard(new Card("Test" + i, (byte)i));
			//cards.add(new Card("Test" + i, (byte)i));
		}
		assertEquals(1, p.cardCheck().size());

	}
	*/
}
