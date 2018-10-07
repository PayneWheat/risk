package test.java;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import main.java.game.Card;
import main.java.game.Player;

public class CardSetTest {

	@Test
	public void test() {
		ArrayList<Card> cardset = new ArrayList<Card>();
		Card card1 = new Card("Test1", (byte)1);
		Card card2 = new Card("Test2", (byte)2);
		Card card3 = new Card("Test3", (byte)3);
		Card card4 = new Card("Test4", (byte)4);
		Player one = new Player("John", "Blue", 35, 6, 5, 4);
		cardset.add(card1);
		cardset.add(card2);
		cardset.add(card3);
		cardset.add(card4);
		one.addCard(card1);
		one.addCard(card2);
		one.addCard(card3);
		one.addCard(card4);
		assertNotNull(one.oneOfEachExtractor(cardset, 1, 1, 1));
		assertNotNull(one.wildSetsExtractor(cardset, 1));
		assertNotNull(one.threeOfAKindExtractor(cardset, 3, 3, 3));
	}

}
