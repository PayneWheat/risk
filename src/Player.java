
public class Player 
{
	String name;
	String color;
	int armies;
	int diceValue;
	int territoriesOccupied;
	int countriesOccupied;
	int continentsOccupied;
	
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
		reutnr diceValue;
	}
	public int getTerritoriesOccupied(){
		reutrn territoriesOccupied;
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
