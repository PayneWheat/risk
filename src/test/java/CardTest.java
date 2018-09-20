package test.java;
import junit.framework.TestCase;

import org.junit.Test;

import game.Card;

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
}
