import java.util.*;
import javax.swing.JOptionPane;


public class Main {
	public static void main(String[] args) {
		// Risk
		
		Board board = new Board();
		
		// Card Pilot: should always have a set to turn in after 5 cards are collected.
		for(int i = 0; i < 5; i++) {
			Card tempCard = board.drawCard();
			System.out.println(tempCard.territoryName + ", " + tempCard.getCardTypeName());
		}
		
		board.printTerritories();
		
		// Prompt for number of players (here in main or in board constructor/method?)
		Dice d = new Dice(); 
		//Player[]players = new Player[6];
		int numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): "));
		
		Player[] players = new Player[numOfPlayers];
		
		// Initial number of armies for each player is determined (now included in Board class)
		/*
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
		*/
		// Each player chooses their color and enters their name (here in main or in player constructor/method?)
		
		for(int i = 0; i < numOfPlayers; i++)
		{
			String name = JOptionPane.showInputDialog("Please enter the name of player " + (i+1) + ":");
			String color = JOptionPane.showInputDialog("Please enter a color for player " + (i+1) + ":");
			//int diceValue = d.getDiceValue();
			//players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
			// just patched this to get things to run. remove/edit as necessary.
			players[i] = new Player(name, color);
			//System.out.println(name + " roll " + diceValue);
		}
		
		// Sort players by descending dice value
		// OR -- I believe in the game, the players sit in a circle around the board. 
		// So, the player order is determined randomly, and the highest roller plays first.
		/*
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
		*/
		
		board.setPlayers(players, true);
		
		
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
