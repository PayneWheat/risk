
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
		roll();
		return diceValue;
	}
}
