package main.java.game;

public class Dice 
{
	private int diceValue;
	private final int SIDES = 6;
	
	Dice()
	{
		
	}
	
	public void roll()
	{
		diceValue = (int) (Math.random() * SIDES) + 1;
	}
	
	public int getDiceValue()
	{
		// might want to call roll() separately
		// because we'll have to compare dice in a weird way
		// where we'll need to check the value multiple times
		roll();
		return diceValue;
	}
	public int getCurrentValue() {
		return diceValue;
	}
}
