package test.java;
import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.game.Board;
import main.java.game.Player;

public class currentPlayerIndexTest extends TestCase {
	public void testIncrementPlayerIndex() throws Exception {
		Board b = new Board();
		Player players[] = {
				new Player("TestPlayer1", "Red"),
				new Player("TestPlayer2", "Yellow"),
				new Player("TestPlayer3", "Green"),
				new Player("TestPlayer4", "Blue")
		};
		b.setPlayers(players, false);
		b.currentPlayerIndex = 0;
		assertEquals(b.currentPlayerIndex, 0);
		b.incrementCurrentPlayerIndex();
		assertEquals(b.currentPlayerIndex, 1);
		b.incrementCurrentPlayerIndex();
		assertEquals(b.currentPlayerIndex, 2);
		b.incrementCurrentPlayerIndex();
		assertEquals(b.currentPlayerIndex, 3);
		b.incrementCurrentPlayerIndex();
		assertEquals(b.currentPlayerIndex, 0);
		
	}
}
