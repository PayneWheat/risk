
public class Player 
{
	String playerNum;
	int armies;
	int diceValue;
	int countriesOccupied;
	int continentsOccupied;
	
	Player(String playerNum, int armies, int diceValue, int territoriesOccupied, int continentsOccupied)
	{
		this.playerNum = playerNum;
		this.armies = armies;
		this.diceValue = diceValue;
		this.territoriesOccupied = countriesOccupied;
		this.continentsOccupied = continentsOccupied;
		
	}
}
