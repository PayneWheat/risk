package test.java;
import junit.framework.TestCase;
import main.java.game.Attack;
import main.java.game.Player;


public class AttackTest extends TestCase {
	Player p = new Player();
	public void testAttackObject() throws Exception {
		Attack a = new Attack(p);
		assertTrue(a.continueAttack);
		assertEquals(a.receiveCard, false);
		assertEquals(a.attackingPlayer, p);
	}
}
