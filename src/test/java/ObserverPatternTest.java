package test.java;
import junit.framework.TestCase;
import main.java.game.Player;
import main.java.game.Observer;

import org.junit.Test;

public class ObserverPatternTest extends TestCase {

	public void ObserverTest() throws Exception {
		
		Player p = new Player();
    		Board b = new Board();
    		p.addObserver(b);
		p.setAttackMessage(p, "You have been attacked!");
		assertEquals(p.getAttackMessage(), "You have been attacked!");
	}
}
