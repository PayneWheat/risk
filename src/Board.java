import java.util.*;

public class Board {
	public ArrayList<Territory> territories;
	private ArrayList<Card> cards = new ArrayList<Card>();
	// Needs a graph data structure with territories for nodes

	
	public Board(ArrayList<Territory> territories) {
		this.territories = territories;
		createCardDeck();
	}
	private void createCardDeck() {
		// This automatically generates the deck of cards
		// based off the list of territory names.
		for(int i = 0; i < 9; i++) {
			byte type = (byte)(i / 3 + 1);
			Card tempCard = new Card(this.territories.get(i).name, type);
			cards.add(tempCard);
			System.out.println("Card created: " + tempCard.territoryName + ", " + tempCard.type);
		}
		for(int i = 0; i < 2; i++) {
			// wild cards
			cards.add(new Card("Wild", (byte)4));
			System.out.println("Card created: Wild card");
		}
		Collections.shuffle(cards);
	}
	public Card drawCard() {
		Card tempCard = cards.get(0);
		cards.remove(0);
		System.out.println(cards.size());
		return tempCard;
	}
}
