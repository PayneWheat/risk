package test.java;

import junit.framework.TestCase;
import main.java.game.*;

public class TelegramTest extends TestCase {
	Board b = new Board(false, true);
	public void testTelegram() {
		TelegramBot tb = new TelegramBot();
		assertEquals("RiskChatbot", tb.getBotUsername());
	}
}
