import java.util.*;
import javax.swing.JOptionPane;


public class Main {
	public static void main(String[] args) {
		// Risk
		
		// defining territories
		ArrayList<Territory> territories = new ArrayList<Territory>();
		System.out.println("We will put Risk here.");
		String[] northAmerica = {"Alaska", "Alberta (Western Canada)", "Central America", "Eastern United States", "Greenland", "Northwest Territory", "Ontario (Central Canada)", "Quebec (Eastern Canada)", "Western United States"};
		//int ti = 0;
		for(int i = 0; i < northAmerica.length; i++) {
			territories.add(new Territory(northAmerica[i], (byte)1));
			//ti++;
		}
		
		String[] southAmerica = {"Argentina", "Brazil", "Peru", "Venezuela"};
		for(int i = 0; i < southAmerica.length; i++) {
			territories.add(new Territory(southAmerica[i], (byte)2));
			//ti++;
		}
		
		String[] europe = {"Great Britain", "Iceland", "Northern Europe", "Scandinavia", "Southern Europe", "Ukraine", "Western Europe"};
		for(int i = 0; i < europe.length; i++) {
			territories.add(new Territory(europe[i], (byte)3));
		}
		
		String[] africa = {"Congo", "East Africa", "Egypt", "Madagascar", "North Africa", "South Africa"};
		for(int i = 0; i < africa.length; i++) {
			territories.add(new Territory(africa[i], (byte)3));
		}
		
		String[] asia = {"Afghanistan", "China", "India", "Irkutsk", "Japan", "Kamchatka", "Middle East", "Mongolia", "Siam", "Siberia", "Ural", "Yakutsk"};
		for(int i = 0; i < asia.length; i++) {
			territories.add(new Territory(asia[i], (byte)3));
		}
				
		String[] australia = {"Eastern Australia", "Indonesia", "New Guinea", "Western Australia"};
		for(int i = 0; i < australia.length; i++) {
			territories.add(new Territory(australia[i], (byte)3));
		}		
		
		// link territory nodes... any better idea on how to do this?
		// Alaska
		territories.get(0).setAdjacentTerritory(territories.get(1));
		territories.get(0).setAdjacentTerritory(territories.get(5));
		territories.get(0).setAdjacentTerritory(territories.get(31));
		// Alberta
		territories.get(1).setAdjacentTerritory(territories.get(0));
		territories.get(1).setAdjacentTerritory(territories.get(5));
		territories.get(1).setAdjacentTerritory(territories.get(6));
		territories.get(1).setAdjacentTerritory(territories.get(8));
		// Central America
		territories.get(2).setAdjacentTerritory(territories.get(3));
		territories.get(2).setAdjacentTerritory(territories.get(8));
		territories.get(2).setAdjacentTerritory(territories.get(12));
		// Eastern United States
		territories.get(3).setAdjacentTerritory(territories.get(6));
		territories.get(3).setAdjacentTerritory(territories.get(7));
		territories.get(3).setAdjacentTerritory(territories.get(8));
		territories.get(3).setAdjacentTerritory(territories.get(2));
		// Greenland
		territories.get(4).setAdjacentTerritory(territories.get(7));
		territories.get(4).setAdjacentTerritory(territories.get(14));
		// Northwest Territory
		territories.get(5).setAdjacentTerritory(territories.get(0));
		territories.get(5).setAdjacentTerritory(territories.get(1));
		territories.get(5).setAdjacentTerritory(territories.get(6));
		// Ontario
		territories.get(6).setAdjacentTerritory(territories.get(5));
		territories.get(6).setAdjacentTerritory(territories.get(1));
		territories.get(6).setAdjacentTerritory(territories.get(7));
		territories.get(6).setAdjacentTerritory(territories.get(8));
		territories.get(6).setAdjacentTerritory(territories.get(3));
		// Quebec
		territories.get(7).setAdjacentTerritory(territories.get(4));
		territories.get(7).setAdjacentTerritory(territories.get(6));
		territories.get(7).setAdjacentTerritory(territories.get(3));
		// Western United States
		territories.get(8).setAdjacentTerritory(territories.get(1));
		territories.get(8).setAdjacentTerritory(territories.get(6));
		territories.get(8).setAdjacentTerritory(territories.get(3));
		territories.get(8).setAdjacentTerritory(territories.get(2));
		// Argentina
		territories.get(9).setAdjacentTerritory(territories.get(11));
		territories.get(9).setAdjacentTerritory(territories.get(10));
		// Brazil
		territories.get(10).setAdjacentTerritory(territories.get(9));
		territories.get(10).setAdjacentTerritory(territories.get(11));
		territories.get(10).setAdjacentTerritory(territories.get(12));
		territories.get(10).setAdjacentTerritory(territories.get(24));
		// Peru
		territories.get(11).setAdjacentTerritory(territories.get(9));
		territories.get(11).setAdjacentTerritory(territories.get(10));
		territories.get(11).setAdjacentTerritory(territories.get(12));
		// Venezuela
		territories.get(12).setAdjacentTerritory(territories.get(10));
		territories.get(12).setAdjacentTerritory(territories.get(11));
		territories.get(12).setAdjacentTerritory(territories.get(2));
		// Great Britain
		territories.get(13).setAdjacentTerritory(territories.get(14));
		territories.get(13).setAdjacentTerritory(territories.get(16));
		territories.get(13).setAdjacentTerritory(territories.get(15));
		territories.get(13).setAdjacentTerritory(territories.get(19));
		// Iceland
		territories.get(14).setAdjacentTerritory(territories.get(13));
		territories.get(14).setAdjacentTerritory(territories.get(16));
		territories.get(14).setAdjacentTerritory(territories.get(4));
		// Northern Europe
		territories.get(15).setAdjacentTerritory(territories.get(13));
		territories.get(15).setAdjacentTerritory(territories.get(16));
		territories.get(15).setAdjacentTerritory(territories.get(18));
		territories.get(15).setAdjacentTerritory(territories.get(17));
		territories.get(15).setAdjacentTerritory(territories.get(19));
		// Scandinavia
		territories.get(16).setAdjacentTerritory(territories.get(14));
		territories.get(16).setAdjacentTerritory(territories.get(13));
		territories.get(16).setAdjacentTerritory(territories.get(15));
		territories.get(16).setAdjacentTerritory(territories.get(18));
		// Southern Europe
		territories.get(17).setAdjacentTerritory(territories.get(19));
		territories.get(17).setAdjacentTerritory(territories.get(15));
		territories.get(17).setAdjacentTerritory(territories.get(18));
		territories.get(17).setAdjacentTerritory(territories.get(24));
		territories.get(17).setAdjacentTerritory(territories.get(22));
		territories.get(17).setAdjacentTerritory(territories.get(32));
		// Ukraine
		territories.get(18).setAdjacentTerritory(territories.get(16));
		territories.get(18).setAdjacentTerritory(territories.get(15));
		territories.get(18).setAdjacentTerritory(territories.get(32));
		territories.get(18).setAdjacentTerritory(territories.get(17));
		territories.get(18).setAdjacentTerritory(territories.get(26));
		territories.get(18).setAdjacentTerritory(territories.get(36));
		// Western Europe
		territories.get(19).setAdjacentTerritory(territories.get(13));
		territories.get(19).setAdjacentTerritory(territories.get(15));
		territories.get(19).setAdjacentTerritory(territories.get(17));
		territories.get(19).setAdjacentTerritory(territories.get(24));
		// Congo
		territories.get(20).setAdjacentTerritory(territories.get(21));
		territories.get(20).setAdjacentTerritory(territories.get(24));
		territories.get(20).setAdjacentTerritory(territories.get(25));
		// East Africa
		territories.get(21).setAdjacentTerritory(territories.get(20));
		territories.get(21).setAdjacentTerritory(territories.get(22));
		territories.get(21).setAdjacentTerritory(territories.get(23));
		territories.get(21).setAdjacentTerritory(territories.get(25));
		territories.get(21).setAdjacentTerritory(territories.get(24));
		territories.get(21).setAdjacentTerritory(territories.get(32));
		// Egypt
		territories.get(22).setAdjacentTerritory(territories.get(24));
		territories.get(22).setAdjacentTerritory(territories.get(21));
		territories.get(22).setAdjacentTerritory(territories.get(17));
		territories.get(22).setAdjacentTerritory(territories.get(32));
		// Madagascar
		territories.get(23).setAdjacentTerritory(territories.get(21));
		territories.get(23).setAdjacentTerritory(territories.get(25));
		// North Africa
		territories.get(24).setAdjacentTerritory(territories.get(10));
		territories.get(24).setAdjacentTerritory(territories.get(17));
		territories.get(24).setAdjacentTerritory(territories.get(19));
		territories.get(24).setAdjacentTerritory(territories.get(22));
		territories.get(24).setAdjacentTerritory(territories.get(21));
		territories.get(24).setAdjacentTerritory(territories.get(20));
		// South Africa
		territories.get(25).setAdjacentTerritory(territories.get(20));
		territories.get(25).setAdjacentTerritory(territories.get(21));
		territories.get(25).setAdjacentTerritory(territories.get(23));
		// Afghanistan
		territories.get(26).setAdjacentTerritory(territories.get(18));
		territories.get(26).setAdjacentTerritory(territories.get(27));
		territories.get(26).setAdjacentTerritory(territories.get(28));
		territories.get(26).setAdjacentTerritory(territories.get(32));
		territories.get(26).setAdjacentTerritory(territories.get(36));
		// China
		territories.get(27).setAdjacentTerritory(territories.get(26));
		territories.get(27).setAdjacentTerritory(territories.get(28));
		territories.get(27).setAdjacentTerritory(territories.get(34));
		territories.get(27).setAdjacentTerritory(territories.get(33));
		territories.get(27).setAdjacentTerritory(territories.get(35));
		territories.get(27).setAdjacentTerritory(territories.get(36));
		// India
		territories.get(28).setAdjacentTerritory(territories.get(32));
		territories.get(28).setAdjacentTerritory(territories.get(26));
		territories.get(28).setAdjacentTerritory(territories.get(27));
		territories.get(28).setAdjacentTerritory(territories.get(34));
		// Irkutsk
		territories.get(29).setAdjacentTerritory(territories.get(33));
		territories.get(29).setAdjacentTerritory(territories.get(35));
		territories.get(29).setAdjacentTerritory(territories.get(31));
		territories.get(29).setAdjacentTerritory(territories.get(37));
		// Japan
		territories.get(30).setAdjacentTerritory(territories.get(31));
		territories.get(30).setAdjacentTerritory(territories.get(33));
		// Kamchatka
		territories.get(31).setAdjacentTerritory(territories.get(30));
		territories.get(31).setAdjacentTerritory(territories.get(33));
		territories.get(31).setAdjacentTerritory(territories.get(29));
		territories.get(31).setAdjacentTerritory(territories.get(37));
		territories.get(31).setAdjacentTerritory(territories.get(0));
		// Middle East
		territories.get(32).setAdjacentTerritory(territories.get(26));
		territories.get(32).setAdjacentTerritory(territories.get(28));
		territories.get(32).setAdjacentTerritory(territories.get(22));
		territories.get(32).setAdjacentTerritory(territories.get(21));
		territories.get(32).setAdjacentTerritory(territories.get(17));
		territories.get(32).setAdjacentTerritory(territories.get(18));
		// Mongolia
		territories.get(33).setAdjacentTerritory(territories.get(27));
		territories.get(33).setAdjacentTerritory(territories.get(30));
		territories.get(33).setAdjacentTerritory(territories.get(29));
		territories.get(33).setAdjacentTerritory(territories.get(35));
		territories.get(33).setAdjacentTerritory(territories.get(31));
		// Siam
		territories.get(34).setAdjacentTerritory(territories.get(28));
		territories.get(34).setAdjacentTerritory(territories.get(27));
		territories.get(34).setAdjacentTerritory(territories.get(39));
		// Siberia
		territories.get(35).setAdjacentTerritory(territories.get(36));
		territories.get(35).setAdjacentTerritory(territories.get(27));
		territories.get(35).setAdjacentTerritory(territories.get(33));
		territories.get(35).setAdjacentTerritory(territories.get(29));
		territories.get(35).setAdjacentTerritory(territories.get(37));
		// Ural
		territories.get(36).setAdjacentTerritory(territories.get(18));
		territories.get(36).setAdjacentTerritory(territories.get(26));
		territories.get(36).setAdjacentTerritory(territories.get(27));
		territories.get(36).setAdjacentTerritory(territories.get(35));
		// Yakutsk
		territories.get(37).setAdjacentTerritory(territories.get(35));
		territories.get(37).setAdjacentTerritory(territories.get(29));
		territories.get(37).setAdjacentTerritory(territories.get(31));
		// Eastern Australia
		territories.get(38).setAdjacentTerritory(territories.get(40));
		territories.get(38).setAdjacentTerritory(territories.get(41));
		// Indonesia
		territories.get(39).setAdjacentTerritory(territories.get(34));
		territories.get(39).setAdjacentTerritory(territories.get(40));
		territories.get(39).setAdjacentTerritory(territories.get(41));
		// New Guinea
		territories.get(40).setAdjacentTerritory(territories.get(39));
		territories.get(40).setAdjacentTerritory(territories.get(38));
		territories.get(40).setAdjacentTerritory(territories.get(41));
		// Western Australia
		territories.get(41).setAdjacentTerritory(territories.get(39));
		territories.get(41).setAdjacentTerritory(territories.get(40));
		territories.get(41).setAdjacentTerritory(territories.get(38));
		// Create board object
		// Risk cards are generated and shuffled
		
		Board board = new Board(territories);
		
		// Card Pilot:
		for(int i = 0; i < 5; i++) {
			Card tempCard = board.drawCard();
			System.out.println(tempCard.territoryName + ", " + tempCard.getCardTypeName());
		}
		board.printTerritories();
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
		}
		// Sort players by descending dice value
		// OR -- I believe in the game, the players sit in a circle around the board. 
		// So, the player order is determined randomly, and the highest roller plays first.
		
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
