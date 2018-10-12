package test.java;
import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.game.*;
public class ActivitiesTest extends TestCase {
	@Test
	public void testActivites() throws Exception {
		PlayerActivities pa = new PlayerActivities();
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new Player("TestPlayer1", "Red", 0, 0));
		players.add(new Player("TestPlayer1", "Blue", 0, 0));
		Territory t1 = new Territory("TestTerritory1", (byte)0);
		Territory t2 = new Territory("TestTerritory2", (byte)1);
		t1.setOccupant(players.get(0));
		t2.setOccupant(players.get(1));
		
		pa.startGame(players);
		pa.attack(t1, t2);
		pa.buyCards(players.get(0));
		pa.buyCredits(players.get(0), 2);
		pa.conquerTerritory(players.get(0), players.get(1), t2);
		ArrayList<Dice> attackingDice = new ArrayList<Dice>();
		Dice d1 = new Dice();
		d1.roll();
		attackingDice.add(d1);
		ArrayList<Dice> defendingDice = new ArrayList<Dice>();
		Dice d2= new Dice();
		d2.roll();
		attackingDice.add(d2);
		pa.diceRoll(t1, t2, attackingDice, defendingDice);
		pa.endAttack(new Attack(players.get(0)));
		pa.fortify(players.get(0), t1, t2, 1);
		pa.loseArmy(players.get(0));
		pa.placeArmies(players.get(0), t1, 1);
		pa.playerDefeated(players.get(0));
		pa.playerWins(players.get(0));
		pa.receiveArmies(players.get(0), 1);
		pa.receiveRiskCard(players.get(0), new Card("Test", (byte)1));
		pa.transferCredits(players.get(0), players.get(1).toString(), "1");
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(new Card("Test1", (byte)1));
		cards.add(new Card("Test2", (byte)2));
		cards.add(new Card("Test3", (byte)3));
		pa.turnInRiskCards(players.get(0), cards, 10);
		pa.undoMove(players.get(0));
		assertNotNull(pa.getActivityOutput());
	}
}
