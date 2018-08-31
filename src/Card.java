
public class Card {
	String territoryName;
	byte type; // infantry = 1, cavalry = 2, artillery = 3, wild = 4
	public Card(String territoryName, byte type) {
		this.territoryName = territoryName;
		this.type = type;
	}
}
