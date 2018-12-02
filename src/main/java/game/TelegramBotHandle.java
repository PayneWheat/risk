package main.java.game;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TelegramBotHandle {
	public TelegramBot tb;
	public void StartBot() {
		
        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		
		this.tb = new TelegramBot();
        // Register our bot
		try {
			telegramBotsApi.registerBot(this.tb);
		}
		catch(TelegramApiRequestException e) {
			e.printStackTrace();
		}
	}
	public void sendAllMessage(String str) {
		this.tb.sendallplayer(str);
	}
	public void sendPlayerMessage(String str, Player p) {
		this.tb.sendplayer(str, p);
	}
	public String promptPlayer(String str, Player p) {
		String inputValue = "";
		return inputValue;
	}
	public int promptPlayerIndex(String str, Player p) {
		System.out.println("promptPlayerIndex called");
		int inputValue = -1;
		Update tempUp = tb.TempUpdate;
		sendPlayerMessage(str, p);
		// wait for response
		/*
		while(tb.TempUpdate.equals(tempUp)) {
			// do nothing
		}
		try {
			inputValue = Integer.parseInt(tb.TempUpdate.getMessage().getText());
		} catch(Exception e) {
			e.printStackTrace();
		}
		*/
		System.out.println("inputValue: " + inputValue);
		return inputValue;
	}
	public String promptPlayerSelection(String str, Player p) {
		String inputValue = "";
		return inputValue;
	}
}
