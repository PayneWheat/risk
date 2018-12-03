package test.java;

import static org.junit.Assert.assertNotNull;
import java.sql.Timestamp;


import java.util.ArrayList;

import org.junit.Test;
import junit.framework.TestCase;
import main.java.game.*;
/*
public class TimerTest extends TestCase {
	
	protected Player one = new Player("Test1", "Blue", 25, 0);
	protected Board b = new Board(false, true);
	@Test
	public void testTimedButtonPrompt() {
		String values[] = {"one", "two"};
		long time = System.currentTimeMillis();
		try {
			b.timedButtonPrompt("Test", "Instruction", values);
		} catch(Exception e) {
			System.out.println("Timeout occurred.");
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Test time: " + time);
		assertTrue(time >= 30000);
	}
	@Test
	public void testTimedPrompt() {
		long time = System.currentTimeMillis();
		try {
			b.timedPrompt("Test");
		} catch(Exception e) {
			System.out.println("Timeout occurred.");
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Test time: " + time);
		assertTrue(time >= 30000);
	}
	@Test
	public void testTimedSelectionPrompt() {
		long time = System.currentTimeMillis();
		String values[] = {"one", "two", "three"};
		try {
			b.timedSelectionPrompt("Test", values);
		} catch(Exception e) {
			System.out.println("Timeout occurred.");
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Test time: " + time);
		assertTrue(time >= 30000);
	}
	
}
*/