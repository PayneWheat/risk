package test.java;



import org.junit.Test;

import junit.framework.TestCase;
import main.java.game.*;

public class TerritoryTest extends TestCase {
	@Test
	public void testGetMethods() {
		Territory index0 = new Territory("Alaska", (byte)0);
		Territory Empty = new Territory();
		Territory index1 = new Territory("Test1", (byte)0);
		index0.setAdjacentTerritory(Empty);
		index0.setAdjacentTerritory(index1);
		index0.setX(10);
		index0.setY(20);
		assertNotNull(index0.getAdjacentTerritories());
		assertEquals("Alaska", index0.getTerritoryName());
		assertEquals((byte)0, index0.getcontinentIndex());
		assertEquals("North America", index0.getContinent());
		Territory t1 = new Territory("Brazil", (byte)1);
		assertEquals("South America", t1.getContinent());
		t1 = new Territory("Great Britain", (byte)2);
		assertEquals("Europe", t1.getContinent());
		t1 = new Territory("Congo", (byte)3);
		assertEquals("Africa", t1.getContinent());
		t1 = new Territory("Japan", (byte)4);
		assertEquals("Asia", t1.getContinent());
		t1 = new Territory("New Guinea", (byte)5);
		assertEquals("Australia", t1.getContinent());
		Player one = new Player("John", "Blue", 0, 0);
		index0.setPlayer(one);
		index0.setOccupant(one);
		assertEquals(index0.getPlayer(), one);
		assertNotNull(index0.getAdjacentTerritories(false, true, true));
		assertNotNull(index0.getAdjacentTerritories(false, false, true));
		assertNotNull(index0.getAdjacentTerritories(true, false, false));
		assertNotNull(index0.getAdjacentTerritories(true, false, true));
		assertTrue(index0.isOccupied());
		index0.setArmyCount(1);
		assertTrue(index0.getArmyCount() == 1);
		index0.incrementArmy(4);
		assertTrue(index0.getArmyCount() == 5);
		index0.decrementArmy(2);
		assertTrue(index0.getArmyCount() == 3);
		assertEquals(index0.getArmyCount(), 3);
		assertEquals(index0.getX(), 10);
		assertEquals(index0.getY(), 20);
	}
}
