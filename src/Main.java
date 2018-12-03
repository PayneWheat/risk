
//import java.util.*;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;

import org.glassfish.grizzly.utils.ArrayUtils;

import main.java.game.*;
import java.io.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("RISK");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean replayGame = false;
		while(true) {
			System.out.print("Replay game? Y/N: ");
			try {
				String input = br.readLine();
				//System.out.println(input);
				if(input.equals("Y") || input.equals("y")) {
					replayGame = true;
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
		if(replayGame == true) {
			System.out.print("Enter game key to replay: ");
			try {
				String input = br.readLine();
				//System.out.println(input);
				S3 s3 = new S3(false);
				s3.replayGameData(input);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		boolean useTelegram = false;
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
		/*
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
		*/
		// Risk
		Board board = new Board(useAPIs, true);
		
		//board.printTerritories(false, true);
		
		//board.askplaywithbot();
		if(useTelegram) {
			board.playwithbot = true;
		} else {
			board.playwithbot = false;
		}
		
		if(useAPIs) {
			board.setAPITrue();
		}
		/*
		if(consoleOnly) {
			board.setConsoleOnlyTrue();
		}
		*/
		
		if(board.getplaywithbot() == false) {
			boolean tryAgain = true;
			int playerTotalInput = 0;
			while(tryAgain) {
				int response = board.untimedIntPrompt("Select number of players", "", 2, 6);
				if(response < 0) {
					System.out.println("Error reading number. Try again.");
				}
				if(response > 6) {
					System.out.println("Error, number exceeds maximum.");
				}
				if(response < 2) {
					System.out.println("Error, number is below minimum.");
				}
				if(response >= 2 && response <= 6) {
					playerTotalInput = response;
					System.out.println("Number of players: " + playerTotalInput);
					tryAgain = false;
				}
			}
			String[] colors = {"Red", "Yellow", "Green", "Blue", "Purple"};
			for(int i = 0; i < playerTotalInput; i++) {
				String playerName = board.untimedStringPrompt("Player " + (i + 1) + ", enter your name:");
				try {
					Object color = board.timedSelectionPrompt("Select a color: ", colors);
					System.out.println(color);
					board.addPlayer(playerName, color.toString());
					colors = ArrayUtils.remove(colors, color);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			board.startGame();
		
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
		}
	}
}
