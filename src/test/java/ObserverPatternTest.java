package test.java;
import junit.framework.TestCase;
import main.java.game.Board;
import main.java.game.Player;
import main.java.game.Observer;


public class ObserverPatternTest extends TestCase {
  Player p = new Player();
  Board b = new Board();
	public void testObserver() throws Exception {
    p.addObserver(b);
    p.setAttackMessage("You got attacked!");
    assertEquals(p.getAttackMessage(), "You got attacked!");
    assertEquals(b.getBoardAttackMessage(), "You got attacked!");
}
