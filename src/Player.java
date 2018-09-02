
public class Player 
{
	String name;
	String color;
	int armies;
	int diceValue;
	int territoriesOccupied;
	int countriesOccupied;
	int continentsOccupied;
	
	Player() {
		this.name = "";
		this.color = "";
		this.armies = 0;
		this.diceValue = 0;
		this.territoriesOccupied = 0;
		this.continentsOccupied = 0;
		this.countriesOccupied = 0;
	}
	Player(String name, String color) {
		this.name = name;
		this.color = color;
		this.armies = 0;
		this.diceValue = 0;
		this.territoriesOccupied = 0;
		this.countriesOccupied = 0;
		this.continentsOccupied = 0;
	}
	Player(String name, String color, int armies, int diceValue, int territoriesOccupied, int continentsOccupied){

		this.name = name;
		this.color = color;
		this.armies = armies;
		this.diceValue = diceValue;
		this.territoriesOccupied = countriesOccupied;
		this.continentsOccupied = continentsOccupied;	
	}
	
	public String getName() {
		return name;
	}
	public String getColor(){
		return color;
	}
	public int getArmies(){
		return armies;
	}
	public int getDiceValue(){
		return diceValue;
	}
	public int getTerritoriesOccupied(){
		return territoriesOccupied;
	}
	public int continentsOccupied(){
		return continentsOccupied;
	}
	public void increaseArmies(int numOfArmies) {
		armies = armies + numOfArmies;
		System.out.println(name + " increased " + numOfArmies + " armies.");
	}
	public void decreaseArmies(int numOfArmies) {
		armies = armies - numOfArmies;
		System.out.println(name + " lost " + numOfArmies + " armies.");
	}
	
}
