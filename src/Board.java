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
			// north america: index 0
			byte type = (byte)(i / 3 + 1);
			Card tempCard = new Card(this.territories.get(i).name, type);
			cards.add(tempCard);
			System.out.println("Card created: " + tempCard.territoryName + ", " + tempCard.type);
		}
		for(int i = 0; i < 3; i++) {
			// wild cards
			cards.add(new Card("Wild", (byte)4));
			System.out.println("Card created: Wild card");
		}
	}
}
