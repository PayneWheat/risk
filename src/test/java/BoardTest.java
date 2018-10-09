package test.java;

import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.game.*;

public class BoardTest extends TestCase {
	protected Board b = new Board();
	protected Player p = new Player("Test1", "Blue", 0, 0);
	@Test
	public void testTurnInCardSetMethod() throws Exception {
		assertEquals(b.cardSetsTurnedIn, 0);
		ArrayList<Card> cards = new ArrayList<Card>();
		Card tempCard = new Card("Test1", (byte)1);
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test2", (byte)1);
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test3", (byte)1);
		cards.add(tempCard);
		p.addCard(tempCard);
		assertEquals(b.turnInCardSet(p, cards), 4);
		assertEquals(b.cardSetsTurnedIn, 1);
		
		
		cards = new ArrayList<Card>();
		tempCard = new Card("Test1", (byte)1);
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test2", (byte)2); 
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test3", (byte)3); 
		cards.add(tempCard);
		p.addCard(tempCard);
		assertEquals(b.turnInCardSet(p, cards), 6);
		assertEquals(b.cardSetsTurnedIn, 2);
		
		
		cards = new ArrayList<Card>();
		tempCard = new Card("Test1", (byte)1); 
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test2", (byte)2); 
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test3", (byte)4);
		cards.add(tempCard);
		p.addCard(tempCard);
		assertEquals(b.turnInCardSet(p, cards), 8);
		assertEquals(b.cardSetsTurnedIn, 3);
		
		
		cards = new ArrayList<Card>();
		tempCard = new Card("Test1", (byte)1);
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test2", (byte)1);
		cards.add(tempCard);
		p.addCard(tempCard);
		tempCard = new Card("Test3", (byte)4); 
		cards.add(tempCard);
		p.addCard(tempCard);
		assertEquals(b.turnInCardSet(p, cards), 10);
		assertEquals(b.cardSetsTurnedIn, 4);
		
		for(int j = 0; j < cards.size(); j++) {
			p.addCard(cards.get(j));
		}
		assertEquals(b.turnInCardSet(p, cards), 12);
		assertEquals(b.cardSetsTurnedIn, 5);
		
		for(int j = 0; j < cards.size(); j++) {
			p.addCard(cards.get(j));
		}
		assertEquals(b.turnInCardSet(p, cards), 15);
		assertEquals(b.cardSetsTurnedIn, 6);
		
		for(int j = 0; j < cards.size(); j++) {
			p.addCard(cards.get(j));
		}
		assertEquals(b.turnInCardSet(p, cards), 20);
		assertEquals(b.cardSetsTurnedIn, 7);
		
		for(int i = b.cardSetsTurnedIn; i < 100; i++) {
			for(int j = 0; j < cards.size(); j++) {
				p.addCard(cards.get(j));
			}
			int expectedNumber = 20 + (i - 6) * 5; 
			assertEquals(b.turnInCardSet(p, cards), expectedNumber);
			assertEquals(b.cardSetsTurnedIn, i + 1);
		}
	}
	@Test
	public void testMoveArmies() throws Exception {
		Territory t1 = b.territories.get(0);
		t1.setPlayer(p);
		t1.incrementArmy(5);
		Territory t2 = b.territories.get(1);
		t2.setPlayer(p);
		t2.incrementArmy(1);
		b.moveArmies(t1, t2, 3);
		assertEquals(2, t1.getArmyCount());
		assertEquals(4, t2.getArmyCount());
	}
	@Test
	public void testSortDice() throws Exception {
		ArrayList<Dice> d = new ArrayList<Dice>();
		d.add(new Dice(1));
		d.add(new Dice(2));
		d.add(new Dice(3));
		ArrayList<Dice> sorted = b.sortDice(d);
		for(int i = 0; i < 3; i++) {
			System.out.println(sorted.get(i).getCurrentValue());
		}
		int expectedValue = 3;
		for(int i = 0; i < 3; i++) {
			assertEquals(expectedValue, sorted.get(i).getCurrentValue());
			expectedValue--;
		}
	}
	@Test
	public void testArmyReplenishment() throws Exception {
		System.out.println("CURRENT TERRITORY COUNT: " + b.getPlayersTerritories(p).size());
		for(int i = 0; i < 4; i++) {
			b.territories.get(i).setOccupant(p);
		}
		//System.out.println("CURRENT TERRITORY COUNT: " + b.getPlayersTerritories(p).size());
		assertEquals(3, b.armyReplenishment(p));
		for(int i = 13; i < 19; i++) {
			b.territories.get(i).setOccupant(p);
		}
		for(int i = 20; i < 25; i++) {
			b.territories.get(i).setOccupant(p);
		}
		//System.out.println("CURRENT TERRITORY COUNT: " + b.getPlayersTerritories(p).size());
		assertEquals(5, b.armyReplenishment(p));
		b.territories.get(25).setOccupant(p);
		//System.out.println("CURRENT TERRITORY COUNT: " + b.getPlayersTerritories(p).size());
		assertEquals(8, b.armyReplenishment(p));
	}
	@Test
	public void testPrintTerritories() throws Exception {
		b.printTerritories(true, false);
		b.printTerritories(true, true);
		b.printTerritories(false, false);
		b.printTerritories(false, false);
	}
}
