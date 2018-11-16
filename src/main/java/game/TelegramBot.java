package main.java.game;

import java.util.ArrayList;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {
	private String botToken = "743293931:AAGPE--EyWkDE7fQtXXnkSgb18ScYLkjG-U";
	public static ArrayList<String> username = new ArrayList<String>();
	private ArrayList<Long> userID = new ArrayList<Long>();
	public Update TempUpdate = null;
	private String[] color = {"Red","Blue","Yellow"};
	//boolean msgRcvd = false;
	
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
		sendcurrentplayer("Echoing: " + update.getMessage().getText());
		String command = update.getMessage().getText();
		Board b = Board.getInstance();
		if(command.equals("/joinuser") && b.players.size() < 3) {
			username.add(update.getMessage().getFrom().getFirstName());
			userID.add(update.getMessage().getChatId());
			System.out.println(username);
			System.out.println(userID);
			sendallplayer(update.getMessage().getFrom().getFirstName() + " had join the game");
			sendallplayer("Now the room have " + username.size() + " player. They are: " + username);
			// Add user to board instance
			b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1], update.getMessage().getChatId());
		}
		if(command.equals("/joinuser") && b.players.size()==3) {
			sendcurrentplayer("The room is full now.");
			// Add user to board instance
			//b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1]);
		}
		if(command.equals("/startgame") && b.players.size() >= 2) {
			sendallplayer(update.getMessage().getFrom().getFirstName() + " had start the game");
			b.startGame();
			
		} else if (command.equals("/startgame") && b.players.size() < 2) {
			sendallplayer("Not enough players to start the game.");
		}
		//System.out.println("Substring:" + command.substring(0, 9));
		// Enables a user to add additional players for debugging purposes.
		if(command.length() > 10 && command.substring(0, 9).equals("/adduser ") && command.substring(9).length() > 0 && b.players.size() < 3) {
			System.out.println("Adding player " + command.substring(9));
			b.addPlayer(command.substring(9), color[b.players.size() - 1], update.getMessage().getChatId());
		}
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return botToken;
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
	public void sendcurrentplayer(String str) {
		SendMessage MSG = new SendMessage().setChatId(TempUpdate.getMessage().getChatId()).setText(str);
		try {
			execute(MSG);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	public void sendplayer(String str, Player player) {
		//SendMessage MSG = new SendMessage().setChatId(TempUpdate.getMessage().getChatId()).setText(str);
		System.out.println("Sending message to player " + player.getName());
		Board b = Board.getInstance();
		int usernameIndex = -1;
		usernameIndex = userID.indexOf(player.getChatId());
		System.out.println("usernameIndex = " + usernameIndex);
		/*
		for(int i = 0; i < b.players.size(); i++) {
			if(player.getName() == username.get(i)) {
				usernameIndex = i;
				break;
			}
		}
		*/
		SendMessage MSG = new SendMessage().setChatId(userID.get(usernameIndex)).setText(str);
		try {
			execute(MSG);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
