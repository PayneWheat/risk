//import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.java.game.*;

public class Main {
	public static void main(String[] args) {
		// Risk
		Board board = new Board();
		Map map = new Map();
		
		board.printTerritories(false, true);
		
		// Prompt for number of players (here in main or in board constructor/method?)
		// Dice d = new Dice(); Changed this up, see Board class.
		//Player[]players = new Player[6];
		
		boolean incorrectPlayers = true;
		int numOfPlayers;;
		String userInput = "";
		while(incorrectPlayers)
		{
			userInput = JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): ");
			if(userInput.equals("2") || userInput.equals("3") || userInput.equals("4") || userInput.equals("5") || userInput.equals("6"))
			{
				incorrectPlayers = false;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You have entered an invalid number. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
			}
		}
		numOfPlayers = Integer.parseInt(userInput);
		Player[] players = new Player[numOfPlayers];
		
		// Initial number of armies for each player is determined 
		// (now included in Board class (setPlayers method)

		// Each player chooses their color and enters their name
		//String[] colors = {"Red", "Yellow", "Green", "Blue", "Purple"};
		boolean[] colorTaken = {false, false, false, false, false, false};
		for(int i = 0; i < numOfPlayers; i++)
		{
			boolean nameTaken = true;
			String name = "";
			while(nameTaken)
			{
				name = JOptionPane.showInputDialog("Please enter the name of player " + (i+1) + ":");
				if(i == 0)
				{
					nameTaken = false;
				}
				else
				{
					for(int j = 0; j < i; j++)
					{
						if(!(name.equals(players[j].getName())))
						{
							nameTaken = false;
						}
					}
				}
				
				if(nameTaken)
				{
					JOptionPane.showMessageDialog(null, "This name has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
				}
			}
			boolean incorrectColor = true;
			String color = "";
			while(incorrectColor)
			{
				color = JOptionPane.showInputDialog("Please enter a color (Red, Orange, Yellow, Green, Blue, Purple) for player" + (i+1) + ":");
				if(color.equals("Red"))
				{
					if (colorTaken[0] == false)
					{
						colorTaken[0] = true;
						incorrectColor = false;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "This color has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(color.equals("Orange"))
				{
					if(colorTaken[1] == false)
					{
						colorTaken[1] = true;
						incorrectColor = false;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "This color has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(color.equals("Yellow"))
				{
					if(colorTaken[2] == false)
					{
						colorTaken[2] = true;
						incorrectColor = false;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "This color has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(color.equals("Green"))
				{
					if(colorTaken[3] == false)
					{
						colorTaken[3] = true;
						incorrectColor = false;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "This color has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(color.equals("Blue"))
				{
					if(colorTaken[4] == false)
					{
						colorTaken[4] = true;
						incorrectColor = false;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "This color has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(color.equals("Purple"))
				{
					if(colorTaken[4] == false)
					{
						colorTaken[4] = true;
						incorrectColor = false;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "This color has already been taken. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "You have entered an invalid color. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
			
			//int diceValue = d.getDiceValue();
			//players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
			/*
			 * Changed this up a little, this initial roll is part 
			 * of the setPlayers board method now.
			 * See Board class
			int diceValue = d.getDiceValue();
			players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
			*/
			/* Each player is given 25 units of currency at the start of the game
			 Throughout the game, they can use their currency to purchase in-game credits*/
			int credits = 0;
			int currency = 25;
			boolean invalidCredits = true;
			int c = JOptionPane.showOptionDialog(new JFrame(),"Would you like to purchase in-game credits?", 
			        "In-Game Credits", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
			        null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
			if (c == JOptionPane.YES_OPTION) {
				while(invalidCredits){
					credits = Integer.parseInt(JOptionPane.showInputDialog(null, "How many credits would you like to purchase? You are given " + currency + " units of currency"));
					if(credits >=currency && currency <= currency){
						invalidCredits = false;
						currency = currency - credits;
					}
					else{
						JOptionPane.showMessageDialog(null, "You have entered an invalid number. Please Try again.");
					}
				}
	        }
			players[i] = new Player(name, color, currency, credits);
		}
		
		// Sort players by descending dice value
		// OR -- I believe in the game, the players sit in a circle around the board. 
		// So, the player order is determined randomly, and the highest roller plays first.
		
		// Put the sorting in setPlayers method for Board.
		board.setPlayers(players, false);
		
		
		// Players begin initial army placement and continue until all armies have been placed
		board.initialPlacement(true);

		// Players take turns until one player controls the whole board
			// Should start with player that went first in the initial stage.
		boolean continueGame = true;
		while(continueGame) {
			continueGame = board.currentPlayerTurn();
		}
		
	}
}
