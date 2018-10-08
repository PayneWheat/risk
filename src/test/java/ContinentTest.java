package test.java;
import junit.framework.TestCase;
import main.java.game.Continent;
import main.java.game.Territory;


public class ContinentTest extends TestCase {
	
  protected Continent con = new Continent("North America", 5);
      	
	public void testGetFunctions() {
		assertEquals("North America", con.getContinentName());
		assertEquals(5, con.getBonusArmies());
	}
	public void testTerritoryOperations() {
		con.addToContinent(new Territory("USA", (byte)0));
		assertEquals(con.getTerritories().size(), 1);
	}
}
