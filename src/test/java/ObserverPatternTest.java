package test.java;
import junit.framework.TestCase;
import main.java.game.Player;
import main.java.game.Observer;

import org.junit.Test;

public class ObserverPatternTest extends TestCase {

	public void test() throws Exception {
		Player p = new Player();
    		Observer o = new Observer();
    		p.addObserver(o);
		p.setAttackMessage(p, "You have been attacked!");
		assertTrue(p.getAttackMessage(), "You have been attacked!");
	}
}
