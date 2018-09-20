package main.java.game;

public class Card {
	public String territoryName;
	public byte type; // infantry = 1, cavalry = 2, artillery = 3, wild = 4
	public Card() {
		territoryName = "";
		type = (byte)0;
	}
	public Card(String territoryName, byte type) {
		this.territoryName = territoryName;
		this.type = type;
	}
	public String getTerritoryName(){
		return territoryName;
	}	
	public String getCardTypeName() {
		if(type == (byte)1)
			return "Infantry";
		if(type == (byte)2)
			return "Cavalry";
		if(type == (byte)3)
			return "Artillery";
		if(type == (byte)4)
			return "Wild";
		else
			return "Unknown";
	}
}
