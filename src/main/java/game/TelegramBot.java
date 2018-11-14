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
	public static Update TempUpdate;
	
	
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
		
		String command = update.getMessage().getText();
		if(command.equals("/joinuser") && username.size()<3) {
			username.add(update.getMessage().getFrom().getFirstName());
			userID.add(update.getMessage().getChatId());
			System.out.println(username);
			System.out.println(userID);
			sendallplayer(update.getMessage().getFrom().getFirstName() + " had join the game");
			sendallplayer("Now the room have " + username.size() + " player. They are: " + username);
		}
		if(command.equals("/joinuser") && username.size()==3) {
			sendplayer("The room is full now.");
		}
		if(command.equals("/startgame")) {
			sendallplayer(update.getMessage().getFrom().getFirstName() + " had start the game");
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
	
	public void sendplayer(String str) {
		SendMessage MSG = new SendMessage().setChatId(TempUpdate.getMessage().getChatId()).setText(str);
		try {
			execute(MSG);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
