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
	private String[] color = {"Red","Blue","Yellow"};
	public static String message;
	public static boolean gameon = false;
	
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
		if(command.equals("/joinuser") && username.size()<3) {
			username.add(update.getMessage().getFrom().getFirstName());
			userID.add(update.getMessage().getChatId());
			System.out.println(username);
			System.out.println(userID);
			sendallplayer(update.getMessage().getFrom().getFirstName() + " had join the game");
			sendallplayer("Now the room have " + username.size() + " player. They are: " + username);
			// Add user to board instance
			b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1], update.getMessage().getChatId());
		}
		if(command.equals("/joinuser") && username.size()==3) {
			sendmessage("The room is full now.");
			// Add user to board instance
			//b.addPlayer(update.getMessage().getFrom().getFirstName(), color[username.size() - 1], update.getMessage().getChatId());
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
		return "618055658:AAG_N7h09eTVd97tzDreToL1gj6_NDGLgaE";
	}
	
	public String getmessage() {
		return message;
	}
	
	public void cleanmessage() {
		message = null;
	}
	
	public void sendplayer(String str) {
		SendMessage MSG = new SendMessage().setChatId(Board.currentplayerID).setText(str);
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
