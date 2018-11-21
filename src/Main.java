
//import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.java.game.*;
import java.io.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("RISK");
		boolean useTelegram = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.print("Run TelegramBot Server? Y/N: ");
			try {
				String input = br.readLine();
				//System.out.println(input);
				if(input.equals("Y") || input.equals("y")) {
					useTelegram = true;
					break;
				} else if (input.equals("N") || input.equals("n")) {
					break;
				} else {
					System.out.println("Could not parse input. Try again.");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		boolean useAPIs = false;
		while(true) {
			System.out.print("Use AWS replay and Twitter? Y/N: ");
			try {
				String input = br.readLine();
				if(input.equals("Y") || input.equals("y")) {
					useAPIs = true;
					break;
				} else if (input.equals("N") || input.equals("n")) {
					break;
				} else {
					System.out.println("Could not parse input. Try again.");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		boolean consoleOnly = false;
		while(true) {
			System.out.print("Use GUI? Y/N: ");
			try {
				String input = br.readLine();
				if(input.equals("Y") || input.equals("y")) {
					break;
				} else if (input.equals("N") || input.equals("n")) {
					consoleOnly = true;
					break;
				} else {
					System.out.println("Could not parse input. Try again.");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// Risk
		Board board = new Board(useAPIs, consoleOnly);
		//Map map = new Map();
		
		board.printTerritories(false, true);
		
		// Prompt for number of players (here in main or in board constructor/method?)
		// Dice d = new Dice(); Changed this up, see Board class.
		//Player[]players = new Player[6];
		
		//board.askplaywithbot();
		if(useTelegram) {
			board.playwithbot = true;

		} else {
			board.playwithbot = false;
		}
		
		if(useAPIs) {
			board.setAPITrue();
		}
		
		if(consoleOnly) {
			board.setConsoleOnlyTrue();
		}
		
		boolean incorrectPlayers = true;
		int numOfPlayers;
		String userInput = "";
		if(board.getplaywithbot()==false) {
			while(incorrectPlayers)
			{
				userInput = JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): ");
				if(userInput.equals("2") || userInput.equals("3") || userInput.equals("4") || userInput.equals("5") || userInput.equals("6")){
					incorrectPlayers = false;
				}
				else{
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
							if(!(name.equals(board.players.get(j).getName())))
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
				
				/* Each player is given 25 units of currency at the start of the game.
				 At the beginning of each turn, players can use their currency to purchase in-game credits.
				 It costs 1 credit to undo a move.
				 It costs 5 credits to purchase a card.
				 They can also transfer credits to another player for no additional fee.*/
				//players[i] = new Player(name, color, 25, 0);
				board.addPlayer(name, color);
			}
			
			// Sort players by descending dice value
			// OR -- I believe in the game, the players sit in a circle around the board. 
			// So, the player order is determined randomly, and the highest roller plays first.
			
			//board.setPlayers(players, false);
			board.startGame();
			/*
			for(int i = 0; i < players.length; i++) {
				board.addPlayer(players[i].getName(), players[i].getColor());
			}
			*/
			
			// Players begin initial army placement and continue until all armies have been placed
			board.initialPlacement(true);

			// Players take turns until one player controls the whole board
				// Should start with player that went first in the initial stage.
			boolean continueGame = true;
			while(continueGame) {
				continueGame = board.currentPlayerTurn();
			}
		} else {
			System.out.println("Starting Telegram Bot server...");
			TelegramBotHandle RunBot = new TelegramBotHandle();
			RunBot.StartBot();
			System.out.println("Waiting for players to join.");
			while(TelegramBot.gameon==false) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(TelegramBot.gameon==true) {
				Board.getInstance().startGame();
				Board.getInstance().initialPlacement(true);
				boolean continueGame = true;
				while(continueGame) {
					continueGame = Board.getInstance().botcurrentPlayerTurn();
				}
			}
			/*
			String[] color = {"Red","Blue","Yellow"};
			Player[] player = new Player[3];
			for(int i=0; i<player.length;i++) {
				player[i] = new Player(TelegramBot.username.get(i),color[i],25,0);
			}
			board.setPlayers(player, false);
			board.initialPlacement(true);
			boolean continueGame = true;
			while(continueGame) {
				continueGame = board.currentPlayerTurn();
			}
			*/
		}
	}
}
