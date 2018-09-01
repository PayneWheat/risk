
public class Player 
{
	String name;
	String color;
	int armies;
	int diceValue;
	int territoriesOccupied;
	int countriesOccupied;
	int continentsOccupied;
	
	Player(String name, int armies, int diceValue, int territoriesOccupied, int continentsOccupied)
	{
		this.name = name;
		this.armies = armies;
		this.diceValue = diceValue;
		this.territoriesOccupied = countriesOccupied;
		this.continentsOccupied = continentsOccupied;	
	}
	
	public String getName() {
		return name;
	}
	
	public int getArimes() {
		return armies;
	}
	
	public void increaseArmies(int numOfArmies) {
		armies = armies + numOfArmies;
		System.out.println(name + " increased " + numOfArmies + " Armies.");
	}
	
	public void decreaseArmies(int numOfArmies) {
		armies = armies - numOfArmies;
		System.out.println(name + " losted " + numOfArmies + " Armies.");
	}
	
}
