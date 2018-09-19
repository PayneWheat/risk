//import java.util.*;
import javax.swing.JOptionPane;


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
		int numOfPlayers = 0;
		while(incorrectPlayers)
		{
			numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): "));
			if(numOfPlayers == 2 || numOfPlayers == 3 || numOfPlayers == 4 || numOfPlayers == 5 || numOfPlayers == 6)
			{
				incorrectPlayers = false;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You have entered an invalid number. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
			}
		}
		Player[] players = new Player[numOfPlayers];
		
		// Initial number of armies for each player is determined 
		// (now included in Board class (setPlayers method)

		// Each player chooses their color and enters their name
		
// Each player chooses their color and enters their name
		//String[] colors = {"Red", "Yellow", "Green", "Blue", "Purple"};
		boolean[] colorTaken = {false, false, false, false, false};
		for(int i = 0; i < numOfPlayers; i++)
		{
			String name = JOptionPane.showInputDialog("Please enter the name of player " + (i+1) + ":");
			boolean incorrectColor = true;
			String color = "";
			while(incorrectColor)
			{
				color = JOptionPane.showInputDialog("Please enter a color (Red, Yellow, Green, Blue, Purple) for player" + (i+1) + ":");
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
				else if(color.equals("Yellow"))
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
				else if(color.equals("Green"))
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
				else if(color.equals("Blue"))
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
					JOptionPane.showMessageDialog(null, "You have entered an incorrect. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
				}
				
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
