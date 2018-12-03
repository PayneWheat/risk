package main.java.game;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
	private String botToken = null;
	public static ArrayList<String> username = new ArrayList<String>();
	private ArrayList<Long> userID = new ArrayList<Long>();
	public static Update TempUpdate;
	private String[] color = {"Red","Blue","Yellow"};
	public static String message;
	public static boolean gameon = false;
	
	public TelegramBot() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("secrets_PHP.prop");
			prop.load(input);
			botToken = prop.getProperty("telegramtoken");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return "RiskChatbot";
	}

	@Override
	public void onUpdateReceived(Update update) {
		// TODO Auto-generated method stub
		TempUpdate = update;
		System.out.println(update.getMessage().getFrom().getFirstName()+": "+update.getMessage().getText());
		if (update.hasMessage() && update.getMessage().hasText()) {
			message = update.getMessage().getText();
		}
		String command = update.getMessage().getText();
		Board b = Board.getInstance();
		if(command.equals("/creategame")) {
			// return a gameId
			username.add(update.getMessage().getFrom().getFirstName());
			userID.add(update.getMessage().getChatId());
			String gameId = update.getMessage().getChatId().toString();
			gameId += new java.util.Date().toString();
			gameId = gameId.replaceAll("\\s","");
			sendallplayer(update.getMessage().getFrom().getFirstName() + " has created the game");
			sendallplayer("Game ID: " + gameId);
			System.out.println("Game id: " + gameId);
			b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1], update.getMessage().getChatId());
			sendallplayer(update.getMessage().getFrom().getFirstName() + " has joined the game as the color " + color[username.size() - 1]);

		}
		if(command.equals("/joinuser") && username.size()<3) {
			username.add(update.getMessage().getFrom().getFirstName());
			userID.add(update.getMessage().getChatId());
			System.out.println(username);
			System.out.println(userID);
			sendallplayer(update.getMessage().getFrom().getFirstName() + " has joined the game as the color " + color[username.size() - 1]);
			sendallplayer("Now the room have " + username.size() + " player. They are: " + username);
			// Add user to board instance
			b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1], update.getMessage().getChatId());
		}
		if(command.equals("/joinuser") && username.size()==3) {
			sendmessage("The room is full now.");
			// Add user to board instance
			//b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1], update.getMessage().getChatId());
		}
		if(command.length() > 9 && command.substring(0, 9).equals("/adduser ") && command.substring(9).length() > 0 && b.players.size() < 3) {
			String un = command.substring(9);
			System.out.println("Adding player " + un);
			username.add(un);
			userID.add(update.getMessage().getChatId());
			//b.addPlayer(command.substring(9), color[b.players.size() - 1]);
			b.addPlayer(un, color[username.size() - 1], update.getMessage().getChatId());
			sendallplayer(un + " has joined the game as the color " + color[username.size() - 1]);
			sendallplayer("Now the room have " + username.size() + " player. They are: " + username);
		}
		if(command.equals("/startgame")) {
			sendallplayer(update.getMessage().getFrom().getFirstName() + " had start the game");
			gameon=true;
			//b.startGame();
			//if(gameon==true) {
				//b.startGame();
				//b.initialPlacement(true);
				//b.botcurrentPlayerTurn();
				/*boolean continueGame = true;
				continueGame = b.currentPlayerTurn();
				while(continueGame) {
					continueGame = b.currentPlayerTurn();
				}*/
				//}
		}
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return botToken;
	}
	
	public String getmessage() {
		return message;
	}
	
	public void cleanmessage() {
		message = null;
	}
	
	public void sendplayer(String str){
		SendMessage MSG = new SendMessage().setChatId(Board.currentplayerID).setText(str);
		//SendMessage MSG = new SendMessage().setChatId(p.getChatID()).setText(str);
		try {
			execute(MSG);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	public void sendplayer(String str, Player p){
		//SendMessage MSG = new SendMessage().setChatId(Board.currentplayer).setText(str);
		SendMessage MSG = new SendMessage().setChatId(p.getChatID()).setText(str);
		try {
			execute(MSG);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}	
	public void sendallplayer(String str) {
		for (int i=0; i<userID.size();i++) {
			SendMessage MSG = new SendMessage().setChatId(userID.get(i)).setText(str);
			try {
				execute(MSG);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendmessage(String str) {
		SendMessage MSG = new SendMessage().setChatId(TempUpdate.getMessage().getChatId()).setText(str);
		try {
			execute(MSG);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
