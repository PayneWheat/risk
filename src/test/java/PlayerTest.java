package test.java;
import junit.framework.TestCase;
import main.java.game.Player;

public class PlayerTest extends TestCase {
	
	protected Player one = new Player("John", "Blue", 35, 6, 5, 4);
      	
	public void test() {
		one.increaseArmies(5);
		assertEquals(40, one.getArmies());
		one.decreaseArmies(5);
		assertEquals(35, one.getArmies());
		assertEquals("John", one.getName());
		assertEquals("Blue", one.getColor());
		assertEquals(6, one.getDiceValue());
        //assertEquals(5, one.getTerritoriesOccupied());
        //assertEquals(4, one.continentsOccupied());
	}
}
