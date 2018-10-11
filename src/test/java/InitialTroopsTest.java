package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class InitialTroopsTest extends TestCase {
	public void testInitialTroopsCount() {
		Board b = new Board(false);
		assertTrue(b.initalArmyDispursement(2) == 40);
		assertTrue(b.initalArmyDispursement(3) == 35);
		assertTrue(b.initalArmyDispursement(4) == 30);
		assertTrue(b.initalArmyDispursement(5) == 25);
		assertTrue(b.initalArmyDispursement(6) == 20);
	}
}
