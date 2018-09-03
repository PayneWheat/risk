//import java.util.*;
import javax.swing.JOptionPane;


public class Main {
	public static void main(String[] args) {
		// Risk
		System.out.println("We will put Risk here.");
		Board board = new Board();
		
		// Card Pilot: should always have a set to turn in after 5 cards are collected.
		for(int i = 0; i < 5; i++) {
			Card tempCard = board.drawCard();
			System.out.println(tempCard.territoryName + ", " + tempCard.getCardTypeName());
		}
		
		board.printTerritories(false, true);
		
		// Prompt for number of players (here in main or in board constructor/method?)
		// Dice d = new Dice(); Changed this up, see Board class.
		//Player[]players = new Player[6];
		
		int numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): "));
		Player[] players = new Player[numOfPlayers];
		
		// Initial number of armies for each player is determined 
		// (now included in Board class (setPlayers method)

		// Each player chooses their color and enters their name
		
		for(int i = 0; i < numOfPlayers; i++)
		{
			String name = JOptionPane.showInputDialog("Please enter the name of player " + (i+1) + ":");
			String color = JOptionPane.showInputDialog("Please enter a color for player " + (i+1) + ":");

			//int diceValue = d.getDiceValue();
			//players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
			/*
			 * Changed this up a little, this initial roll is part 
			 * of the setPlayers board method now.
			 * See Board class
			int diceValue = d.getDiceValue();
			players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
			*/
			players[i] = new Player(name, color);
		}
		
		// Sort players by descending dice value
		// OR -- I believe in the game, the players sit in a circle around the board. 
		// So, the player order is determined randomly, and the highest roller plays first.
		
		// Put the sorting in setPlayers method for Board.
		board.setPlayers(players, false);
		
		
		// Players begin initial army placement and continue until all armies have been placed
		board.initialPlacement();

		// Players take turns until one player controls the whole board
			// Should start with player that went first in the initial stage.
		boolean continueGame = true;
		while(continueGame) {
			continueGame = board.currentPlayerTurn();
		}
		
	}
}
