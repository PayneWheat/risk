import java.util.*;

import javax.swing.JOptionPane;

public class Board {
	public ArrayList<Territory> territories = new ArrayList<Territory>();
	private ArrayList<Card> cards = new ArrayList<Card>();
	public ArrayList<Player> players = new ArrayList<Player>();
	public int currentPlayerIndex;
	public int initialArmies;
	
	public Board() {
		this.territories = generateGraph();
		this.cards = createCardDeck();
	}
	
	private ArrayList<Card> createCardDeck() {
		ArrayList<Card> deck = new ArrayList<Card>();
		// This automatically generates the deck of cards
		// based off the list of territory names.
		for(int i = 0; i < this.territories.size(); i++) {
			byte type = (byte)(i % 3 + 1);
			Card tempCard = new Card(this.territories.get(i).name, type);
			deck.add(tempCard);
			System.out.println("Card created[" + i + "]: " + tempCard.territoryName + ", " + tempCard.getCardTypeName());
		}
		for(int i = 0; i < 2; i++) {
			// wild cards
			deck.add(new Card("Wild", (byte)4));
			System.out.println("Card created: Wild card");
		}
		// shuffles risk card deck
		System.out.println("Total cards: " + deck.size() + "\n");
		Collections.shuffle(deck);
		return deck;
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
	
	public void printTerritories(boolean onlyUnoccupied, boolean showAdjacent) {
		
		if(onlyUnoccupied == true) {
			System.out.println("Available territories:");
			for(int i = 0; i < this.territories.size(); i++) {
				if(!this.territories.get(i).isOccupied()) {
					System.out.print("[" + i + "]" + this.territories.get(i).name + ", ");
				}
			}
		} else {
			for(int i = 0; i < this.territories.size(); i++) {
				String playerName = ""; 
				if(territories.get(i).isOccupied()) {
					playerName = territories.get(i).getPlayer().getName();
				} else {
					playerName = "UNOCCUPIED";
				}
				System.out.print("[" + i + "]" + territories.get(i).getContinent() + ", " + territories.get(i).name + "(" + playerName +  ", " + territories.get(i).getArmyCount() + " armies)->{ ");
				if(showAdjacent) {
					ArrayList<Territory> adjs = territories.get(i).getAdjacentTerritories();
					for(int j = 0; j < adjs.size(); j++) {
						System.out.print(adjs.get(j).name + ", ");
					}
					System.out.print("}");
				}
				System.out.print("\n");				
			}
		}
	}
	
	public void setPlayers(Player[] players, boolean sortByInitRoll) {
		int numOfPlayers = players.length;
		System.out.println("Number of players: " + numOfPlayers);
		// roll dice for each player
		Dice d = new Dice();
		int initRolls[] = new int[numOfPlayers];
		int maxIndex = 0;
		initRolls[0] = d.getDiceValue();
		System.out.println(players[0].getName() + " rolled a " + initRolls[0]);
		for(int i = 1; i < numOfPlayers; i++) {
			initRolls[i] = d.getDiceValue();
			System.out.println(players[i].getName() + " rolled a " + initRolls[i]);
			if(initRolls[maxIndex] < initRolls[i])
				maxIndex = i;
		}
		System.out.println(players[maxIndex].getName() + " goes first.");
		switch(numOfPlayers)
		{
			case 2:
				initialArmies = 40;
				break;
			case 3:
				initialArmies = 35;
				break;
			case 4:
				initialArmies = 30;
				break;
			case 5 :
				initialArmies = 25;
				break;
			case 6: 
				initialArmies = 20;
				break;
		
		}
		if(sortByInitRoll == true) {
			Player temp;
			for(int i = 0; i < numOfPlayers; i++) {
				for (int j = i; j > 0; j--) {
					// changed the conditional statement below to work with the initRolls array
					// how to tiebreak?
					if (initRolls[j] >  initRolls[j - 1]) {
						temp = players[j];
						players[j] = players[j - 1];
						players[j - 1] = temp;
					}
				}
			}
			currentPlayerIndex = 0;
		} else {
			// return max from dice rolls
			currentPlayerIndex = maxIndex;
		}
		this.players = new ArrayList<Player>(Arrays.asList(players));
		for(int i = 0; i < numOfPlayers; i++) {
			System.out.println(" The order of players to play is " + players[i].getName());
		}
		/*
		for(int i = 0; i < this.players.size(); i++) {
			System.out.println(players[i].name + " (" + players[i].color + "):" + diceRoll);
		}
		*/
	}
	
	public void initialPlacement() {
		// Show map with each territory's occupying player and army count
		// Display player's available armies/turns remaining
		
		// Disperse initial troops
		for(int i = 0; i < players.size(); i++) {
			players.get(i).increaseArmies(initialArmies);
		}
		
		// Player selects either an unoccupied territory (until all territories are occupied)
		// 	or one of their territories to place ONE army (after all territories have been chosen)
		printTerritories(true, true);
		//System.out.println(players.get(currentPlayerIndex).getName() + ", choose a territory index to place one army: ");
		while(players.get(currentPlayerIndex).armies > 0) {
			if(unoccupiedTerritoriesCount() > 0)
				printTerritories(true, false);
			else
				printTerritories(false, false);
			int ti = pickTerritory();
			Territory tempTerritory = territories.get(ti);
			tempTerritory.setOccupant(players.get(currentPlayerIndex));
			tempTerritory.incrementArmy(1);
			// End turn and begin player to the left's turn. Continue until no remaining initial armies for any player.
			incrementCurrentPlayerIndex();
		}
		
	}
	
	private int pickTerritory() {
		String territoryIndex = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", input a territory index to place one army");
		int ti = -1;
		try {
			ti = Integer.parseInt(territoryIndex);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			ti = pickTerritory();
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			ti = pickTerritory();
		}
		if(ti > territories.size() - 1) {
			System.out.println("Out of range. Try again");
			ti = pickTerritory();
		}
		if(unoccupiedTerritoriesCount() > 0) {
			if(territories.get(ti).isOccupied()) {
				System.out.println("Territory already occupied. Try again");
				ti = pickTerritory();
			}
		} else {
			if(territories.get(ti).getPlayer() != players.get(currentPlayerIndex)) {
				System.out.println("You do not control this territory. Try again");
				ti = pickTerritory();
			}
		}
		return ti;
	}
	
	public void incrementCurrentPlayerIndex() {
		if(currentPlayerIndex == players.size() - 1)
			currentPlayerIndex = 0;
		else
			currentPlayerIndex++;
	}
	
	public int unoccupiedTerritoriesCount() {
		int count = 0;
		for(int i = 0; i < territories.size(); i++) {
			if(!territories.get(i).isOccupied()) {
				count++;
			}
		}
		return count;
	}
	
	public int armyReplenishment(Player currentPlayer) {
		// Army replenishment at beginning of player's turn.
		int armies = 0;
		if(currentPlayer.territoriesOccupied < 9)
			armies = 3;
		else
			armies = currentPlayer.countriesOccupied / 3;
		// check if player has control of any continent
		return armies;
	}
	
	private ArrayList<Territory> generateGraph() {
		ArrayList<Territory> territories = new ArrayList<Territory>();
		System.out.println("We will put Risk here.");
		String[] northAmerica = {"Alaska", "Alberta (Western Canada)", "Central America", "Eastern United States", "Greenland", "Northwest Territory", "Ontario (Central Canada)", "Quebec (Eastern Canada)", "Western United States"};
		for(int i = 0; i < northAmerica.length; i++) {
			territories.add(new Territory(northAmerica[i], (byte)1));
		}
		
		String[] southAmerica = {"Argentina", "Brazil", "Peru", "Venezuela"};
		for(int i = 0; i < southAmerica.length; i++) {
			territories.add(new Territory(southAmerica[i], (byte)2));
		}
		
		String[] europe = {"Great Britain", "Iceland", "Northern Europe", "Scandinavia", "Southern Europe", "Ukraine", "Western Europe"};
		for(int i = 0; i < europe.length; i++) {
			territories.add(new Territory(europe[i], (byte)3));
		}
		
		String[] africa = {"Congo", "East Africa", "Egypt", "Madagascar", "North Africa", "South Africa"};
		for(int i = 0; i < africa.length; i++) {
			territories.add(new Territory(africa[i], (byte)4));
		}
		
		String[] asia = {"Afghanistan", "China", "India", "Irkutsk", "Japan", "Kamchatka", "Middle East", "Mongolia", "Siam", "Siberia", "Ural", "Yakutsk"};
		for(int i = 0; i < asia.length; i++) {
			territories.add(new Territory(asia[i], (byte)5));
		}
				
		String[] australia = {"Eastern Australia", "Indonesia", "New Guinea", "Western Australia"};
		for(int i = 0; i < australia.length; i++) {
			territories.add(new Territory(australia[i], (byte)6));
		}
		
		//set each XY coordinates for each territories
		territories.get(0).setX(55);
		territories.get(0).setY(107);
		territories.get(1).setX(116);
		territories.get(1).setY(149);
		territories.get(2).setX(128);
		territories.get(2).setY(265);
		territories.get(3).setX(179);
		territories.get(3).setY(221);
		territories.get(4).setX(268);
		territories.get(4).setY(77);
		territories.get(5).setX(126);
		territories.get(5).setY(109);
		territories.get(6).setX(169);
		territories.get(6).setY(163);
		territories.get(7).setX(222);
		territories.get(7).setY(160);
		territories.get(8).setX(124);
		territories.get(8).setY(203);
		
		// link territory nodes... any better idea on how to do this?
		// Alaska
		territories.get(0).setAdjacentTerritory(territories.get(1));
		territories.get(0).setAdjacentTerritory(territories.get(5));
		territories.get(0).setAdjacentTerritory(territories.get(31));
		// Alberta
		territories.get(1).setAdjacentTerritory(territories.get(0));
		territories.get(1).setAdjacentTerritory(territories.get(5));
		territories.get(1).setAdjacentTerritory(territories.get(6));
		territories.get(1).setAdjacentTerritory(territories.get(8));
		// Central America
		territories.get(2).setAdjacentTerritory(territories.get(3));
		territories.get(2).setAdjacentTerritory(territories.get(8));
		territories.get(2).setAdjacentTerritory(territories.get(12));
		// Eastern United States
		territories.get(3).setAdjacentTerritory(territories.get(6));
		territories.get(3).setAdjacentTerritory(territories.get(7));
		territories.get(3).setAdjacentTerritory(territories.get(8));
		territories.get(3).setAdjacentTerritory(territories.get(2));
		// Greenland
		territories.get(4).setAdjacentTerritory(territories.get(7));
		territories.get(4).setAdjacentTerritory(territories.get(14));
		// Northwest Territory
		territories.get(5).setAdjacentTerritory(territories.get(0));
		territories.get(5).setAdjacentTerritory(territories.get(1));
		territories.get(5).setAdjacentTerritory(territories.get(6));
		// Ontario
		territories.get(6).setAdjacentTerritory(territories.get(5));
		territories.get(6).setAdjacentTerritory(territories.get(1));
		territories.get(6).setAdjacentTerritory(territories.get(7));
		territories.get(6).setAdjacentTerritory(territories.get(8));
		territories.get(6).setAdjacentTerritory(territories.get(3));
		// Quebec
		territories.get(7).setAdjacentTerritory(territories.get(4));
		territories.get(7).setAdjacentTerritory(territories.get(6));
		territories.get(7).setAdjacentTerritory(territories.get(3));
		// Western United States
		territories.get(8).setAdjacentTerritory(territories.get(1));
		territories.get(8).setAdjacentTerritory(territories.get(6));
		territories.get(8).setAdjacentTerritory(territories.get(3));
		territories.get(8).setAdjacentTerritory(territories.get(2));
		// Argentina
		territories.get(9).setAdjacentTerritory(territories.get(11));
		territories.get(9).setAdjacentTerritory(territories.get(10));
		// Brazil
		territories.get(10).setAdjacentTerritory(territories.get(9));
		territories.get(10).setAdjacentTerritory(territories.get(11));
		territories.get(10).setAdjacentTerritory(territories.get(12));
		territories.get(10).setAdjacentTerritory(territories.get(24));
		// Peru
		territories.get(11).setAdjacentTerritory(territories.get(9));
		territories.get(11).setAdjacentTerritory(territories.get(10));
		territories.get(11).setAdjacentTerritory(territories.get(12));
		// Venezuela
		territories.get(12).setAdjacentTerritory(territories.get(10));
		territories.get(12).setAdjacentTerritory(territories.get(11));
		territories.get(12).setAdjacentTerritory(territories.get(2));
		// Great Britain
		territories.get(13).setAdjacentTerritory(territories.get(14));
		territories.get(13).setAdjacentTerritory(territories.get(16));
		territories.get(13).setAdjacentTerritory(territories.get(15));
		territories.get(13).setAdjacentTerritory(territories.get(19));
		// Iceland
		territories.get(14).setAdjacentTerritory(territories.get(13));
		territories.get(14).setAdjacentTerritory(territories.get(16));
		territories.get(14).setAdjacentTerritory(territories.get(4));
		// Northern Europe
		territories.get(15).setAdjacentTerritory(territories.get(13));
		territories.get(15).setAdjacentTerritory(territories.get(16));
		territories.get(15).setAdjacentTerritory(territories.get(18));
		territories.get(15).setAdjacentTerritory(territories.get(17));
		territories.get(15).setAdjacentTerritory(territories.get(19));
		// Scandinavia
		territories.get(16).setAdjacentTerritory(territories.get(14));
		territories.get(16).setAdjacentTerritory(territories.get(13));
		territories.get(16).setAdjacentTerritory(territories.get(15));
		territories.get(16).setAdjacentTerritory(territories.get(18));
		// Southern Europe
		territories.get(17).setAdjacentTerritory(territories.get(19));
		territories.get(17).setAdjacentTerritory(territories.get(15));
		territories.get(17).setAdjacentTerritory(territories.get(18));
		territories.get(17).setAdjacentTerritory(territories.get(24));
		territories.get(17).setAdjacentTerritory(territories.get(22));
		territories.get(17).setAdjacentTerritory(territories.get(32));
		// Ukraine
		territories.get(18).setAdjacentTerritory(territories.get(16));
		territories.get(18).setAdjacentTerritory(territories.get(15));
		territories.get(18).setAdjacentTerritory(territories.get(32));
		territories.get(18).setAdjacentTerritory(territories.get(17));
		territories.get(18).setAdjacentTerritory(territories.get(26));
		territories.get(18).setAdjacentTerritory(territories.get(36));
		// Western Europe
		territories.get(19).setAdjacentTerritory(territories.get(13));
		territories.get(19).setAdjacentTerritory(territories.get(15));
		territories.get(19).setAdjacentTerritory(territories.get(17));
		territories.get(19).setAdjacentTerritory(territories.get(24));
		// Congo
		territories.get(20).setAdjacentTerritory(territories.get(21));
		territories.get(20).setAdjacentTerritory(territories.get(24));
		territories.get(20).setAdjacentTerritory(territories.get(25));
		// East Africa
		territories.get(21).setAdjacentTerritory(territories.get(20));
		territories.get(21).setAdjacentTerritory(territories.get(22));
		territories.get(21).setAdjacentTerritory(territories.get(23));
		territories.get(21).setAdjacentTerritory(territories.get(25));
		territories.get(21).setAdjacentTerritory(territories.get(24));
		territories.get(21).setAdjacentTerritory(territories.get(32));
		// Egypt
		territories.get(22).setAdjacentTerritory(territories.get(24));
		territories.get(22).setAdjacentTerritory(territories.get(21));
		territories.get(22).setAdjacentTerritory(territories.get(17));
		territories.get(22).setAdjacentTerritory(territories.get(32));
		// Madagascar
		territories.get(23).setAdjacentTerritory(territories.get(21));
		territories.get(23).setAdjacentTerritory(territories.get(25));
		// North Africa
		territories.get(24).setAdjacentTerritory(territories.get(10));
		territories.get(24).setAdjacentTerritory(territories.get(17));
		territories.get(24).setAdjacentTerritory(territories.get(19));
		territories.get(24).setAdjacentTerritory(territories.get(22));
		territories.get(24).setAdjacentTerritory(territories.get(21));
		territories.get(24).setAdjacentTerritory(territories.get(20));
		// South Africa
		territories.get(25).setAdjacentTerritory(territories.get(20));
		territories.get(25).setAdjacentTerritory(territories.get(21));
		territories.get(25).setAdjacentTerritory(territories.get(23));
		// Afghanistan
		territories.get(26).setAdjacentTerritory(territories.get(18));
		territories.get(26).setAdjacentTerritory(territories.get(27));
		territories.get(26).setAdjacentTerritory(territories.get(28));
		territories.get(26).setAdjacentTerritory(territories.get(32));
		territories.get(26).setAdjacentTerritory(territories.get(36));
		// China
		territories.get(27).setAdjacentTerritory(territories.get(26));
		territories.get(27).setAdjacentTerritory(territories.get(28));
		territories.get(27).setAdjacentTerritory(territories.get(34));
		territories.get(27).setAdjacentTerritory(territories.get(33));
		territories.get(27).setAdjacentTerritory(territories.get(35));
		territories.get(27).setAdjacentTerritory(territories.get(36));
		// India
		territories.get(28).setAdjacentTerritory(territories.get(32));
		territories.get(28).setAdjacentTerritory(territories.get(26));
		territories.get(28).setAdjacentTerritory(territories.get(27));
		territories.get(28).setAdjacentTerritory(territories.get(34));
		// Irkutsk
		territories.get(29).setAdjacentTerritory(territories.get(33));
		territories.get(29).setAdjacentTerritory(territories.get(35));
		territories.get(29).setAdjacentTerritory(territories.get(31));
		territories.get(29).setAdjacentTerritory(territories.get(37));
		// Japan
		territories.get(30).setAdjacentTerritory(territories.get(31));
		territories.get(30).setAdjacentTerritory(territories.get(33));
		// Kamchatka
		territories.get(31).setAdjacentTerritory(territories.get(30));
		territories.get(31).setAdjacentTerritory(territories.get(33));
		territories.get(31).setAdjacentTerritory(territories.get(29));
		territories.get(31).setAdjacentTerritory(territories.get(37));
		territories.get(31).setAdjacentTerritory(territories.get(0));
		// Middle East
		territories.get(32).setAdjacentTerritory(territories.get(26));
		territories.get(32).setAdjacentTerritory(territories.get(28));
		territories.get(32).setAdjacentTerritory(territories.get(22));
		territories.get(32).setAdjacentTerritory(territories.get(21));
		territories.get(32).setAdjacentTerritory(territories.get(17));
		territories.get(32).setAdjacentTerritory(territories.get(18));
		// Mongolia
		territories.get(33).setAdjacentTerritory(territories.get(27));
		territories.get(33).setAdjacentTerritory(territories.get(30));
		territories.get(33).setAdjacentTerritory(territories.get(29));
		territories.get(33).setAdjacentTerritory(territories.get(35));
		territories.get(33).setAdjacentTerritory(territories.get(31));
		// Siam
		territories.get(34).setAdjacentTerritory(territories.get(28));
		territories.get(34).setAdjacentTerritory(territories.get(27));
		territories.get(34).setAdjacentTerritory(territories.get(39));
		// Siberia
		territories.get(35).setAdjacentTerritory(territories.get(36));
		territories.get(35).setAdjacentTerritory(territories.get(27));
		territories.get(35).setAdjacentTerritory(territories.get(33));
		territories.get(35).setAdjacentTerritory(territories.get(29));
		territories.get(35).setAdjacentTerritory(territories.get(37));
		// Ural
		territories.get(36).setAdjacentTerritory(territories.get(18));
		territories.get(36).setAdjacentTerritory(territories.get(26));
		territories.get(36).setAdjacentTerritory(territories.get(27));
		territories.get(36).setAdjacentTerritory(territories.get(35));
		// Yakutsk
		territories.get(37).setAdjacentTerritory(territories.get(35));
		territories.get(37).setAdjacentTerritory(territories.get(29));
		territories.get(37).setAdjacentTerritory(territories.get(31));
		// Eastern Australia
		territories.get(38).setAdjacentTerritory(territories.get(40));
		territories.get(38).setAdjacentTerritory(territories.get(41));
		// Indonesia
		territories.get(39).setAdjacentTerritory(territories.get(34));
		territories.get(39).setAdjacentTerritory(territories.get(40));
		territories.get(39).setAdjacentTerritory(territories.get(41));
		// New Guinea
		territories.get(40).setAdjacentTerritory(territories.get(39));
		territories.get(40).setAdjacentTerritory(territories.get(38));
		territories.get(40).setAdjacentTerritory(territories.get(41));
		// Western Australia
		territories.get(41).setAdjacentTerritory(territories.get(39));
		territories.get(41).setAdjacentTerritory(territories.get(40));
		territories.get(41).setAdjacentTerritory(territories.get(38));
		return territories;
	}
}
