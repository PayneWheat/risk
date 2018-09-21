package test.java;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.TestCase;
import main.java.game.*;

public class TerritoryTest extends TestCase {

	@Test
	public void test() {
		Territory index0 = new Territory("Alaska", (byte)0);
		assertEquals("Alaska", index0.getTerritoryName());
		assertEquals((byte)0, index0.getcontinentIndex());
		assertEquals("North America", index0.getContinent());
		Player one = new Player("John", "Blue", 35, 6, 5, 4);
		index0.setPlayer(one);
		index0.setOccupant(one);
		assertTrue(index0.isOccupied());
		index0.setArmyCount(1);
		assertTrue(index0.getArmyCount() == 1);
		index0.incrementArmy(4);
		assertTrue(index0.getArmyCount() == 5);
		index0.decrementArmy(2);
		assertTrue(index0.getArmyCount() == 3);
		
	}

}
