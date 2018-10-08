package test.java;
import junit.framework.TestCase;
import main.java.game.Dice;

import org.junit.Test;

public class DiceTest extends TestCase {

	public void test() throws Exception {
		
		Dice d = new Dice();
		d.roll();
    	int value = d.getDiceValue();
		assertTrue(1 <= value && value <= 6);
	}
}
