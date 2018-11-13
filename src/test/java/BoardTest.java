package test.java;

import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.game.*;

public class BoardTest extends TestCase {
	protected Board b = new Board(false);
	protected Player p = new Player("Test1", "Blue", 0, 0);
	protected Player p2 = new Player("Test2", "Red", 0, 0);
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
		//b.printTerritories(true, true);
		b.printTerritories(false, false);
		b.territories.get(0).setOccupant(p);
		b.territories.get(0).setArmyCount(1);
		b.printTerritories(false, true);
	}
	@Test
	public void testDiceHelper() throws Exception {
		Territory t1 = new Territory("Test1", (byte)1);
		Territory t2 = new Territory("Test2", (byte)2);
		t1.setOccupant(p);
		t1.setArmyCount(5);
		t2.setOccupant(p2);
		t2.setArmyCount(5);
		ArrayList<ArrayList<Dice>> dice = b.diceHelper(t1, t2);
		assertEquals(3, dice.get(0).size());
		assertEquals(2, dice.get(1).size());
		t1.setArmyCount(3);
		t2.setArmyCount(3);
		dice = b.diceHelper(t1, t2);
		assertEquals(2, dice.get(0).size());
		assertEquals(2, dice.get(1).size());
		t1.setArmyCount(2);
		t2.setArmyCount(2);
		dice = b.diceHelper(t1, t2);
		assertEquals(1, dice.get(0).size());
		assertEquals(2, dice.get(1).size());
		t1.setArmyCount(2);
		t2.setArmyCount(1);
		dice = b.diceHelper(t1, t2);
		assertEquals(1, dice.get(0).size());
		assertEquals(1, dice.get(1).size());
	}
	@Test
	public void testArmyAdjustment() {
		Territory t1 = new Territory("Test1", (byte)1);
		t1.setOccupant(p);
		t1.setArmyCount(4);
		Territory t2 = new Territory("Test2", (byte)1);
		t2.setOccupant(p2);
		t2.setArmyCount(2);
		Dice dA1 = new Dice();
		dA1.setCurrentValue(6);
		Dice dA2 = new Dice();
		dA2.setCurrentValue(6);
		Dice dA3 = new Dice();
		dA3.setCurrentValue(6);
		ArrayList<Dice> attackingDice = new ArrayList<Dice>();
		attackingDice.add(dA1);
		attackingDice.add(dA2);
		attackingDice.add(dA3);
		
		Dice dD1 = new Dice();
		dD1.setCurrentValue(5);
		Dice dD2 = new Dice();
		dD2.setCurrentValue(5);
		ArrayList<Dice> defendingDice = new ArrayList<Dice>();
		defendingDice.add(dD1);
		defendingDice.add(dD2);
		b.armyAdjustment(t1, attackingDice, t2, defendingDice);
		assertEquals(0, t2.getArmyCount());
	}
	@Test
	public void testInitialArmyPlacement() {
		System.out.println("------STARTING TESTINITIALARMYPLACEMENT------");
		Board b2 = new Board(false);
		Player tempP1 = new Player("Test1", "Blue", 0, 0);
		Player tempP2 = new Player("Test2", "Red", 0, 0);
		Player players[] = {tempP1, tempP2};
		b2.setPlayers(players, false);
		b2.initialPlacement(true);
		assertEquals(21, b2.playerTerritoriesCount(tempP1));
	}
}
