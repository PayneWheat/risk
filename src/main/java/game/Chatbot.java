package main.java.game;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Chatbot extends TelegramLongPollingBot {
    @Override
    
  public void onUpdateReceived(Update update) {
    	// We check if the update has a message and the message has text
	    //This method (currently) will echo the message sent by the user
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            
            //Determines content of message
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(message_text);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public String getBotUsername() {
        // TODO
        return "Chatbot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "12345:qwertyuiopASDGFHKMK";
    }
}
