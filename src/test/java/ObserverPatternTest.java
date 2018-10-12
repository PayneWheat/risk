package test.java;
import junit.framework.TestCase;
import org.junit.Test;
import main.java.game.*;

public class ObserverPatternTest extends TestCase {
	@Test
	public void testObserver() throws Exception {
		Player p = new Player();
		Player p2 = new Player();
    	Board b = new Board();
    	p.addObserver(b);
		p.setAttackMessage(p2, "You have been attacked!");
		assertEquals(p.getAttackMessage(), "You have been attacked!");
	}
}
