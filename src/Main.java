import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		// Risk
		
		// defining territories
		ArrayList<Territory> territories = new ArrayList<Territory>();
		System.out.println("We will put Risk here.");
		String[] northAmerica = {"Alaska", "Alberta (Western Canada)", "Central America", "Eastern United States", "Greenland", "Northwest Territory", "Ontario (Central Canada)", "Quebec (Eastern Canada)", "Western United States"};
		int ti = 0;
		for(int i = 0; i < northAmerica.length; i++) {
			territories.add(new Territory(northAmerica[i], (byte)1));
			ti++;
		}
		
		// link territory nodes... any better idea on how to do this?
		territories.get(0).setAdjacentTerritory(territories.get(1));
		territories.get(0).setAdjacentTerritory(territories.get(5));
		ArrayList<Territory> adjTers = territories.get(0).getAdjacentTerritories();
		System.out.print("Adjacents for " + territories.get(0).name + ": ");
		for(int i = 0; i < adjTers.size(); i++) {
			System.out.print(adjTers.get(i).name + ", ");
		}
		System.out.print("\n");
		territories.get(1).setAdjacentTerritory(territories.get(0));
		territories.get(1).setAdjacentTerritory(territories.get(5));
		territories.get(1).setAdjacentTerritory(territories.get(6));
		adjTers = territories.get(1).getAdjacentTerritories();
		System.out.print("Adjacents for " + territories.get(1).name + ": ");
		for(int i = 0; i < adjTers.size(); i++) {
			System.out.print(adjTers.get(i).name + ", ");
		}
		System.out.print("\n");
		
		
		// Create board object
		// Risk cards are generated and shuffled
		
		// Card Pilot:
		Board board = new Board(territories);
		for(int i = 0; i < 5; i++) {
			Card tempCard = board.drawCard();
			System.out.println(tempCard.territoryName + " " + tempCard.type);
		}
		
		// Prompt for number of players (here in main or in board constructor/method?)
		Dice d = new Dice(); 
		Player[]players = new Player[6];
		int numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): "));
		
		// Initial number of armies for each player is determined
		int initialArmies = 0;
		switch(numOfPlayers)
		{
			case 2:
				initialArmies = 40;
				break;
			case 3:
				initialArmies = 35;
				break;
			case 4:
				initialArmies = 30;
				break;
			case 5 :
				initialArmies = 25;
				break;
			case 6: 
				initialArmies = 20;
				break;
		
		}
		
		// Each player chooses their color and enters their name (here in main or in player constructor/method?)
		for(int i = 0; i < numOfPlayers; i++)
		{
			String name = JOptionPane.showInputDialog("Please enter the name of player " + (i+1) + ":");
			String color = JOptionPane.showInputDialog("Please enter a color for player " + (i+1) + ":");
			int diceValue = d.getDiceValue();
			//players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
			// just patched this to get things to run. remove/edit as necessary.
			players[i] = new Player(name, initialArmies, diceValue, 0, 0);
			System.out.println(name + " roll " + diceValue);
		}
		
		// Sort players by descending dice value
		// OR -- I believe in the game, the players sit in a circle around the board. 
		// So, the player order is determined randomly, and the highest roller plays first.
		
		Player temp;
		for(int i = 0; i < numOfPlayers; i++) {
			for (int j = i; j > 0; j--) {
				if (players[j].diceValue >  players[j - 1].diceValue) {
					temp = players[j];
					players[j] = players[j - 1];
					players[j - 1] = temp;
				}
			}
		}
		
		for(int i = 0; i < numOfPlayers; i++) {
			System.out.println(" The order of players to play is " + players[i].getName());
		}
		
		// Players begin initial army placement and continue until all armies have been placed
			// Show map with each territory's occupying player and army count
			// Display Player's available armies/turns remaining
			// Player selects either an unoccupied territory (until all territories are occupied)
			// 	or one of their territories to place ONE army (after all territories have been chosen)
			// End turn and begin player to the left's turn. Continue until no remaining initial armies for any player.
		
		// Players take turns until one player controls the whole board
			// Should start with player that went first in the initial stage.
			//	Each turn consists of three parts:
			//	-- 1. Placing new troops
			//	-- 2. Attacking
			//	-- 3. Fortifying
		
	}
}
