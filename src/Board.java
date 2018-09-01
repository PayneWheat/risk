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
		for(int i = 0; i < this.territories.size(); i++) {
			byte type = (byte)(i % 3 + 1);
			Card tempCard = new Card(this.territories.get(i).name, type);
			cards.add(tempCard);
			System.out.println("Card created[" + i + "]: " + tempCard.territoryName + ", " + tempCard.getCardTypeName());
		}
		for(int i = 0; i < 2; i++) {
			// wild cards
			cards.add(new Card("Wild", (byte)4));
			System.out.println("Card created: Wild card");
		}
		// shuffles risk card deck
		System.out.println("Total cards: " + cards.size() + "\n");
		Collections.shuffle(cards);
	}
	public Card drawCard() {
		// When a player has conquered at least one territory during their turn,
		// they will draw a risk card.
		Card tempCard = cards.get(0);
		cards.remove(0);
		return tempCard;
	}
	public int remainingCards() {
		return cards.size();
	}
	public void printTerritories() {
		for(int i = 0; i < this.territories.size(); i++) {
			System.out.print("[" + i + "]" + territories.get(i).name + "->{ ");
			ArrayList<Territory> adjs = territories.get(i).getAdjacentTerritories();
			for(int j = 0; j < adjs.size(); j++) {
				System.out.print(adjs.get(j).name + ", ");
			}
			System.out.println("}");
		}
	}
}
