package test.java;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;
import junit.framework.TestCase;
import main.java.game.*;

public class PlayerTest extends TestCase {
	
	protected Player one = new Player("Test1", "Blue", 25, 0);
	protected Player two = new Player("Test2", "Red", 25, 1);
	protected Board b = new Board(false);
	
	@Test
	public void testArmies() {
		one.increaseArmies(5);
		assertEquals(5, one.getArmies());
		one.decreaseArmies(5);
		assertEquals(0, one.getArmies());
		assertEquals("Test1", one.getName());
		assertEquals("Blue", one.getColor());
		//assertEquals(6, one.getDiceValue());	
	}
	
	@Test
	public void testCurrencyAndCredits() {
		assertEquals(25, one.getCurrency());
		assertEquals(1, two.getCredits());
		one.buyCredits(5);
		assertEquals(5, one.getCredits());
		one.useCurrency(5);
		assertEquals(20, one.getCurrency());
		two.useCredits(1);
		assertEquals(0, two.getCredits());
	}
	
	@Test
	public void testCard() {
		ArrayList<Card> cardset = new ArrayList<Card>();
		Card card1 = new Card("Test1", (byte)1);
		Card card2 = new Card("Test2", (byte)2);
		Card card3 = new Card("Test3", (byte)3);
		Card card4 = new Card("Test4", (byte)4);
		Player one = new Player("John", "Blue", 0, 0);
		cardset.add(card1);
		cardset.add(card2);
		cardset.add(card3);
		cardset.add(card4);
		one.addCard(card1);
		one.addCard(card2);
		one.addCard(card3);
		one.addCard(card4);
		assertNotNull(one.oneOfEachExtractor(cardset, 1, 1, 1));
		assertNotNull(one.wildSetsExtractor(cardset, 1));
		assertNotNull(one.threeOfAKindExtractor(cardset, 3, 3, 3));
	}
	
	@Test
	public void testNumberInputParser() {
		int parsed = one.numberInputParser("0");
		assertEquals(0, parsed);
		// should only accept positive numbers
		parsed = one.numberInputParser("-100");
		assertEquals(-1, parsed);
		parsed = one.numberInputParser("abc");
		assertEquals(-1, parsed);
	}
	@Test
	public void testTerritoriesThatCanAttack() {
		// Give player 1 all of north america
		for(int i = 0; i < 9; i++) {
			b.territories.get(i).setOccupant(one);
			b.territories.get(i).setArmyCount(4);
		}
		// Give player 2 the following territories:
		// Kamchatka
		b.territories.get(31).setOccupant(two);
		b.territories.get(31).setArmyCount(2);
		// Venezuela
		b.territories.get(12).setOccupant(two);
		b.territories.get(12).setArmyCount(2);
		// Iceland
		b.territories.get(14).setOccupant(two);
		b.territories.get(14).setArmyCount(2);
		
		ArrayList<Territory> at = one.territoriesThatCanAttack(b.getPlayersTerritories(one), b.territories);
		
		assertEquals(3, at.size());
	}
}
