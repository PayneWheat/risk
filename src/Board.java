import java.util.*;

public class Board {
	public Territory[] territories;
	public String[] continents;
	private ArrayList<Card> cards;
	// Needs a graph data structure with territories for nodes
	public Board(Territory[] territories) {
		this.territories = territories;
		this.createCardDeck();
	}
	private void createCardDeck() {
		for(int i = 0; i < this.territories.length; i++) {
			byte type = (byte)(i / 3 + 1);
			Card tempCard = new Card(this.territories[i].name, type);
			cards.add(tempCard);
		}
		for(int i = 0; i < 3; i++) {
			cards.add(new Card("Wild", (byte)4));
		}
	}
}
