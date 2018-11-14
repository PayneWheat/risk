package main.java.game;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TelegramBotHandle {
	
	public void StartBot() {
		
        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		
        // Register our bot
		try {
			telegramBotsApi.registerBot(new TelegramBot());
		}
		catch(TelegramApiRequestException e) {
			e.printStackTrace();
		}
		
	}
}
