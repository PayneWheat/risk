package test.java;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;
import junit.framework.TestCase;
import main.java.game.*;

public class CreditTest extends TestCase {
	
	protected Player one = new Player("Test1", "Blue", 25, 0);
	protected Player two = new Player("Test2", "Red", 25, 1);

	@Test
	public void testCurrencyAndCredits() {
		assertEquals(25, one.getCurrency());
		assertEquals(1, two.getCredits());
		one.buyCredits(5);
		assertEquals(5, one.getCredits());
		one.useCurrency(5);
		assertEquals(20, one.getCurrency());
		two.useCredits(1);
		assertEquals(0, two.getCredits());
	}
  
}
