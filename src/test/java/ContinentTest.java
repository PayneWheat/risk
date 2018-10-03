package test.java;
import junit.framework.TestCase;
import main.java.game.Continent;


public class ContinentTest extends TestCase {
	
  protected Continent con = new Continent("North America", 5);
      	
	public void test() {
		assertEquals("North America", con.getContinentName());
		assertEquals(5, con.getBonusArmies());
	}
}
