package test.java;
import junit.framework.TestCase;
import main.java.game.Dice;

import org.junit.Test;

public class DiceTest extends TestCase{

	Dice d = new Dice();
	d.roll();
        int diceValue = d.getDiceValue();

	public void test() {
		assertTrue(diceValue >= 1 || diceValue <= 6);
	}
}
