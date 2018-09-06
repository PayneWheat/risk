import java.util.*;

import javax.swing.JOptionPane;

public class Board {
	public ArrayList<Territory> territories = new ArrayList<Territory>();
	public ArrayList<Continent> continents = new ArrayList<Continent>();
	private ArrayList<Card> cards = new ArrayList<Card>();
	public ArrayList<Player> players = new ArrayList<Player>();
	public int currentPlayerIndex;
	public int initialArmies;
	
	public Board() {
		generateGraph();
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
			System.out.println("");
		} else {
			for(int i = 0; i < this.territories.size(); i++) {
				String playerName = ""; 
				if(territories.get(i).isOccupied()) {
					playerName = territories.get(i).getPlayer().getName();
				} else {
					playerName = "UNOCCUPIED";
				}
				System.out.print("[" + i + "]" + territories.get(i).getContinent() + ", " + territories.get(i).name + "(" + playerName +  ", " + territories.get(i).getArmyCount() + " armies)");
				if(showAdjacent) {
					System.out.print("->{ ");
					ArrayList<Territory> adjs = territories.get(i).getAdjacentTerritories();
					for(int j = 0; j < adjs.size(); j++) {
						System.out.print(adjs.get(j).name + ", ");
					}
					System.out.print("}");
				}
				System.out.println("");		
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param players the array of players initialized in main
	 * @param sortByInitRoll sorts player order by dice roll if true
	 * otherwise, the players are ordered as they entered their names.
	 */
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
	/**
	 * @param automatic automatically places initial troops if true
	 */
	public void initialPlacement(boolean automatic) {
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
		int i = 1;
		while(players.get(currentPlayerIndex).armies > 0) {
			int ti = -1;
			if(!automatic) {
				if(unoccupiedTerritoriesCount() > 0)
					printTerritories(true, false);
				else
					printTerritories(false, true);
				ti = pickTerritory(true);
			} else {
				ti = currentPlayerIndex * (territories.size() / players.size()) + (i / players.size());
				if(ti == 42) {
					i = 1;
					continue;
				}
				i++;
			}
			Territory tempTerritory = territories.get(ti);
			tempTerritory.setOccupant(players.get(currentPlayerIndex));
			tempTerritory.incrementArmy(1);
			players.get(currentPlayerIndex).decreaseArmies(1);
			// End turn and begin player to the left's turn. Continue until no remaining initial armies for any player.
			incrementCurrentPlayerIndex();
		}

	}
	
	private int pickTerritory(boolean initialTurns) {
		int ti = -1;
		String territoryIndex = "";
		if(initialTurns)
			territoryIndex = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", input a territory index to place one army");
		else
			territoryIndex = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", input a territory index to place at least one army");
		try {
			ti = Integer.parseInt(territoryIndex);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			ti = pickTerritory(initialTurns);
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			ti = pickTerritory(initialTurns);
		}
		if(ti > territories.size() - 1) {
			System.out.println("Out of range. Try again");
			ti = pickTerritory(initialTurns);
		}
		
		if(unoccupiedTerritoriesCount() > 0) {
			if(territories.get(ti).isOccupied()) {
				System.out.println("Territory already occupied. Try again");
				ti = pickTerritory(initialTurns);
			}
		} else {
			if(territories.get(ti).getPlayer() != players.get(currentPlayerIndex)) {
				System.out.println("You do not control this territory. Try again");
				ti = pickTerritory(initialTurns);
			}
		}
		
		return ti;
	}
	
	private int placeArmies() {
		String armyQty = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", how many of your " + players.get(currentPlayerIndex).getArmies() + " remaining armies?");
		int armies = 0;
		try {
			armies = Integer.parseInt(armyQty);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			armies = placeArmies();
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			armies = placeArmies();
		}
		if(armies > players.get(currentPlayerIndex).getArmies()) {
			System.out.println("You don't have that many armies.");
			armies = placeArmies();
		} else if(armies < 0) {
			System.out.println("Choose a positive number less than " + players.get(currentPlayerIndex).getArmies());
			armies = placeArmies();
		}
		return armies;
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
	public int getCurPlayerTerritoriesCount() {
		int count = 0;
		for(int i = 0; i < territories.size(); i++) {
			if(territories.get(i).getPlayer() == players.get(currentPlayerIndex)) {
				count++;
			}
		}
		return count;
	}
	public int armyReplenishment() {
		// Army replenishment at beginning of player's turn.
		//Player curPlayer = players.get(currentPlayerIndex);
		int armies = 0;
		int territoryCount = getCurPlayerTerritoriesCount();
		if(territoryCount < 9)
			armies = 3;
		else
			armies = territoryCount / 3;
		System.out.println(players.get(currentPlayerIndex).name + " has " + territoryCount + " territories which yields " + armies + " armies.");
		int contArmies = 0;
		int contCount = 0;
		// check if player has control of any continent
		for(int i = 0; i < continents.size(); i++) {
			if(continents.get(i).bonusArmiesAwarded() == players.get(currentPlayerIndex)) {
				contArmies += continents.get(i).getBonusArmies();
				contCount++;
			}
		}
		System.out.println(players.get(currentPlayerIndex).name + " has control of " + contCount + " continents for an additional " + contArmies + " armies.");
		armies += contArmies;
		System.out.println(players.get(currentPlayerIndex).name + " receives " + armies + " armies.");
		return armies;
	}
	
	private Territory chooseAttackingTerritory() {
		// The territory must have at least 2 troops, and needs to be
		//	 adjacent to a territory occupied by an opponent. (i.e., if 
		//	 a player's territory is completely surrounded by his own, other territories)
		ArrayList<Territory> attackTerritories = new ArrayList<Territory>();
		for(int i = 0; i < territories.size(); i++) {
			if(territories.get(i).getPlayer() == players.get(currentPlayerIndex) && territories.get(i).getArmyCount() > 1) {
				attackTerritories.add(territories.get(i));
				System.out.println("[" + i + "]" + territories.get(i).getTerritoryName() + ": " + territories.get(i).getArmyCount() + " armies");
			}
		}
		String attackingTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose a territory to attack from.");
		int attackingTerritoryIndex = -1;
		Territory tempTerritory = new Territory();
		try {
			attackingTerritoryIndex = Integer.parseInt(attackingTerritoryInput);
			tempTerritory = territories.get(attackingTerritoryIndex);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			tempTerritory = chooseAttackingTerritory();
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			tempTerritory = chooseAttackingTerritory();
		}
		if(tempTerritory.getPlayer() != players.get(currentPlayerIndex)) {
			System.out.println("You do not occupy supplied territory. Try again");
			tempTerritory = chooseAttackingTerritory();
		}
		return tempTerritory;
	}
	private Territory chooseTerritoryToAttack(Territory attackingTerritory) {
		// Display adjacent territories occupied by another player
		// and prompt the user to select one
		Territory tempTerritory = new Territory();
		ArrayList<Territory> opposingAdjacents = attackingTerritory.getAdjacentTerritories(true);
		String defendingTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose an opponent's territory to attack.");
		int defendingTerritoryIndex = -1;
		try {
			defendingTerritoryIndex = Integer.parseInt(defendingTerritoryInput);
			tempTerritory = territories.get(defendingTerritoryIndex);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory);
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory);
		}
		if(tempTerritory.getPlayer() == players.get(currentPlayerIndex)) {
			System.out.println("You cannot attack your own territory. Try again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory);
		}
		// search opposingAdjacents for territories.get(defendingTerritoryIndex); ensure it is adjacent
		if(!opposingAdjacents.contains(tempTerritory)) {
			System.out.println("That territory is not adjacent to the attacking territory you selected.\nTry again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory);
		}
		// add option to cancel and go back to step before (choosing attacking territory).
		return tempTerritory;
	}
	private boolean attack(Territory attackingTerritory, Territory defendingTerritory) {
		boolean continueAttack = false;
		System.out.println("\n" + attackingTerritory.getPlayer().getName() + " is attacking " + defendingTerritory.getPlayer().getName());
		System.out.println(attackingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ") vs "+ defendingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ")");
		// Prompt player to roll dice, with the number of dice determined
		// for both players by the total armies present on either territory.
		// OR allow player to "retreat" -- or stop attack
		boolean continueAttacking = true;
		while(continueAttacking) {
			// If attacker has 2 armies, they roll one die.
			// If attacker has 3 armies, they roll two dice.
			// If attacker has 4 or more armies, they roll three dice.
			int attackingDiceTotal = 0;
			if(attackingTerritory.getArmyCount() > 3) {
				attackingDiceTotal = 3;
			} else if (attackingTerritory.getArmyCount() == 3) {
				attackingDiceTotal = 2;
			} else if (attackingTerritory.getArmyCount() == 2) {
				attackingDiceTotal = 1;
			}
			// If defender has 1 army, they roll one die.
			// If defender has 2 or more armies, they roll two dice.
			int defendingDiceTotal = 0;
			if(defendingTerritory.getArmyCount() > 1) {
				defendingDiceTotal = 2;
			} else if(defendingTerritory.getArmyCount() == 1) {
				defendingDiceTotal = 1;
			}
			System.out.println(attackingTerritory.getPlayer().getName() + " rolls " + attackingDiceTotal + " die/dice.");
			System.out.println(defendingTerritory.getPlayer().getName() + " rolls " + defendingDiceTotal + " die/dice.");
			
			ArrayList<Dice> attackingDice = new ArrayList<Dice>();
			for(int i = 0; i < attackingDiceTotal; i++) {
				attackingDice.add(new Dice());
				attackingDice.get(i).roll();
			}
			System.out.print(attackingTerritory.getPlayer().getName() + " rolled ");
			for(int i = 0; i < attackingDiceTotal; i++) {
				System.out.print(attackingDice.get(i).getCurrentValue() + " ");
			}
			System.out.println();
			ArrayList<Dice> defendingDice = new ArrayList<Dice>();
			for(int i = 0; i < defendingDiceTotal; i++) {
				defendingDice.add(new Dice());
				defendingDice.get(i).roll();
			}
			System.out.print(defendingTerritory.getPlayer().getName() + " rolled ");
			for(int i = 0; i < defendingDiceTotal; i++) {
				System.out.print(defendingDice.get(i).getCurrentValue() + " ");
			}
			System.out.println();
			// Sort dice descending by value for both players. 
			// E.g., if attacking player A rolls a 3, 6, and 2 
			// the dice should be sorted 6, 3, 2
			
			// Find the minimum of number of dice rolled between the two players
			// (it must either be 1 or 2), then compare each of the 1 or 2 dice
			// to the opposing player's dice.
			// If the defender's die is greater than or equal to the attacker's,
			// the attacker loses an army
			// Else if the defender's die is less than the attacker's, 
			// the defender loses an army
			// This is done with the dice sorted from high to low for both players
			
			// Display results
			
			// Repeat until:
			// A) the attacking player retreats,
			// B) has only 1 remaining army on their territory, or
			// C) has wiped out the defending players armies on their territory.
			String[] values = {"Yes", "No"};
			Object selected = JOptionPane.showInputDialog(null, "Continue attacking " + attackingTerritory.getTerritoryName() + "?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
			if ( selected != null ) {//null if the user cancels. 
			    String selectedString = selected.toString();
			    //do something
			} else {
			    System.out.println("User cancelled");
			}
			if(selected == "No") {
				continueAttacking = false;
			}
		}
		// If case C, prompt user to choose how many armies to move into the
		// newly acquired territory from the attacking territory.
		
		// Prompt the player either attack another territory
		// or end the attack phase of their turn
		return continueAttack;
	}
	public boolean currentPlayerTurn() {
		boolean continueGame = false;
		//	Each turn consists of three parts:
		//	-- 1. Placing new troops
		//	-- 2. Attacking
		//	-- 3. Fortifying
		
		// 1. Placing new troops
		
		// Check if a set of Risk cards can be turned in
		
		// Determine total armies received by board
		players.get(currentPlayerIndex).increaseArmies(armyReplenishment());
		
		// Prompt player to select a territory
		while(players.get(currentPlayerIndex).armies > 0) {
			printTerritories(false, true);
			int ti = pickTerritory(false);
			Territory tempTerritory = territories.get(ti);
			// Prompt player to place at least 1 army on selected territory
			int armies = placeArmies();
			tempTerritory.incrementArmy(armies);
			players.get(currentPlayerIndex).decreaseArmies(armies);
			// Repeat until no armies remaining for player.
		}
		printTerritories(false, true);
		
		// 2. Attacking
		// Prompt player to choose a territory to attack from
		Territory attackingTerritory = chooseAttackingTerritory();
		System.out.println("Attacking from " + attackingTerritory.getTerritoryName());
		// Prompt player to choose a territory to attack
		Territory defendingTerritory = chooseTerritoryToAttack(attackingTerritory);
		System.out.println("Defending from " + defendingTerritory.getTerritoryName());
		boolean continueAttack = true;
		while(continueAttack) {
			// Continue until player decides to end attack phase
			continueAttack = attack(attackingTerritory, defendingTerritory);
		}
		
		// 3. Fortifying
		
		return continueGame;
	}
	private void generateGraph() {
		//ArrayList<Territory> territories = new ArrayList<Territory>();

		byte continentIndex = 0;
		Continent tempContinent = new Continent("North America", 5);
		continents.add(tempContinent);
		String[] northAmerica = {"Alaska", "Alberta (Western Canada)", "Central America", "Eastern United States", "Greenland", "Northwest Territory", "Ontario (Central Canada)", "Quebec (Eastern Canada)", "Western United States"};
		for(int i = 0; i < northAmerica.length; i++) {
			Territory tempTerritory = new Territory(northAmerica[i], continentIndex);
			this.territories.add(tempTerritory);
			tempContinent.addToContinent(tempTerritory);
		}
		System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();
		
		continentIndex++;
		tempContinent = new Continent("South America", 2);
		continents.add(tempContinent);
		String[] southAmerica = {"Argentina", "Brazil", "Peru", "Venezuela"};
		for(int i = 0; i < southAmerica.length; i++) {
			Territory tempTerritory = new Territory(southAmerica[i], continentIndex);
			this.territories.add(tempTerritory);
			tempContinent.addToContinent(tempTerritory);
		}
		System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();
		
		continentIndex++;
		tempContinent = new Continent("Europe", 5);
		continents.add(tempContinent);
		String[] europe = {"Great Britain", "Iceland", "Northern Europe", "Scandinavia", "Southern Europe", "Ukraine", "Western Europe"};
		for(int i = 0; i < europe.length; i++) {
			Territory tempTerritory = new Territory(europe[i], continentIndex);
			this.territories.add(tempTerritory);
			tempContinent.addToContinent(tempTerritory);
		}
		System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();
		
		continentIndex++;
		tempContinent = new Continent("Africa", 3);
		continents.add(tempContinent);
		String[] africa = {"Congo", "East Africa", "Egypt", "Madagascar", "North Africa", "South Africa"};
		for(int i = 0; i < africa.length; i++) {
			Territory tempTerritory = new Territory(africa[i], continentIndex);
			this.territories.add(tempTerritory);
			tempContinent.addToContinent(tempTerritory);
		}
		System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();
		
		continentIndex++;
		tempContinent = new Continent("Asia", 7);
		continents.add(tempContinent);
		String[] asia = {"Afghanistan", "China", "India", "Irkutsk", "Japan", "Kamchatka", "Middle East", "Mongolia", "Siam", "Siberia", "Ural", "Yakutsk"};
		for(int i = 0; i < asia.length; i++) {
			Territory tempTerritory = new Territory(asia[i], continentIndex);
			this.territories.add(tempTerritory);
			tempContinent.addToContinent(tempTerritory);
		}
		System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();
		
		continentIndex++;
		tempContinent = new Continent("Australia", 2);
		continents.add(tempContinent);
		String[] australia = {"Eastern Australia", "Indonesia", "New Guinea", "Western Australia"};
		for(int i = 0; i < australia.length; i++) {
			Territory tempTerritory = new Territory(australia[i], continentIndex);
			this.territories.add(tempTerritory);
			tempContinent.addToContinent(tempTerritory);
		}
		System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();

		printTerritories(true, false);
		System.out.println("Territories size: " + this.territories.size());
		System.out.println(this.territories.get(0).getTerritoryName());
		
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
		this.territories.get(0).setAdjacentTerritory(this.territories.get(1));
		this.territories.get(0).setAdjacentTerritory(this.territories.get(5));
		this.territories.get(0).setAdjacentTerritory(this.territories.get(31));
		// Alberta
		this.territories.get(1).setAdjacentTerritory(this.territories.get(0));
		this.territories.get(1).setAdjacentTerritory(this.territories.get(5));
		this.territories.get(1).setAdjacentTerritory(this.territories.get(6));
		this.territories.get(1).setAdjacentTerritory(this.territories.get(8));
		// Central America
		this.territories.get(2).setAdjacentTerritory(this.territories.get(3));
		this.territories.get(2).setAdjacentTerritory(this.territories.get(8));
		this.territories.get(2).setAdjacentTerritory(this.territories.get(12));
		// Eastern United States
		this.territories.get(3).setAdjacentTerritory(this.territories.get(6));
		this.territories.get(3).setAdjacentTerritory(this.territories.get(7));
		this.territories.get(3).setAdjacentTerritory(this.territories.get(8));
		this.territories.get(3).setAdjacentTerritory(this.territories.get(2));
		// Greenland
		this.territories.get(4).setAdjacentTerritory(this.territories.get(7));
		this.territories.get(4).setAdjacentTerritory(this.territories.get(14));
		// Northwest Territory
		this.territories.get(5).setAdjacentTerritory(this.territories.get(0));
		this.territories.get(5).setAdjacentTerritory(this.territories.get(1));
		this.territories.get(5).setAdjacentTerritory(this.territories.get(6));
		// Ontario
		this.territories.get(6).setAdjacentTerritory(this.territories.get(5));
		this.territories.get(6).setAdjacentTerritory(this.territories.get(1));
		this.territories.get(6).setAdjacentTerritory(this.territories.get(7));
		this.territories.get(6).setAdjacentTerritory(this.territories.get(8));
		this.territories.get(6).setAdjacentTerritory(this.territories.get(3));
		// Quebec
		this.territories.get(7).setAdjacentTerritory(this.territories.get(4));
		this.territories.get(7).setAdjacentTerritory(this.territories.get(6));
		this.territories.get(7).setAdjacentTerritory(this.territories.get(3));
		// Western United States
		this.territories.get(8).setAdjacentTerritory(this.territories.get(1));
		this.territories.get(8).setAdjacentTerritory(this.territories.get(6));
		this.territories.get(8).setAdjacentTerritory(this.territories.get(3));
		this.territories.get(8).setAdjacentTerritory(this.territories.get(2));
		// Argentina
		this.territories.get(9).setAdjacentTerritory(this.territories.get(11));
		this.territories.get(9).setAdjacentTerritory(this.territories.get(10));
		// Brazil
		this.territories.get(10).setAdjacentTerritory(this.territories.get(9));
		this.territories.get(10).setAdjacentTerritory(this.territories.get(11));
		this.territories.get(10).setAdjacentTerritory(this.territories.get(12));
		this.territories.get(10).setAdjacentTerritory(this.territories.get(24));
		// Peru
		this.territories.get(11).setAdjacentTerritory(this.territories.get(9));
		this.territories.get(11).setAdjacentTerritory(this.territories.get(10));
		this.territories.get(11).setAdjacentTerritory(this.territories.get(12));
		// Venezuela
		this.territories.get(12).setAdjacentTerritory(this.territories.get(10));
		this.territories.get(12).setAdjacentTerritory(this.territories.get(11));
		this.territories.get(12).setAdjacentTerritory(this.territories.get(2));
		// Great Britain
		this.territories.get(13).setAdjacentTerritory(this.territories.get(14));
		this.territories.get(13).setAdjacentTerritory(this.territories.get(16));
		this.territories.get(13).setAdjacentTerritory(this.territories.get(15));
		this.territories.get(13).setAdjacentTerritory(this.territories.get(19));
		// Iceland
		this.territories.get(14).setAdjacentTerritory(this.territories.get(13));
		this.territories.get(14).setAdjacentTerritory(this.territories.get(16));
		this.territories.get(14).setAdjacentTerritory(this.territories.get(4));
		// Northern Europe
		this.territories.get(15).setAdjacentTerritory(this.territories.get(13));
		this.territories.get(15).setAdjacentTerritory(this.territories.get(16));
		this.territories.get(15).setAdjacentTerritory(this.territories.get(18));
		this.territories.get(15).setAdjacentTerritory(this.territories.get(17));
		this.territories.get(15).setAdjacentTerritory(this.territories.get(19));
		// Scandinavia
		this.territories.get(16).setAdjacentTerritory(this.territories.get(14));
		this.territories.get(16).setAdjacentTerritory(this.territories.get(13));
		this.territories.get(16).setAdjacentTerritory(this.territories.get(15));
		this.territories.get(16).setAdjacentTerritory(this.territories.get(18));
		// Southern Europe
		this.territories.get(17).setAdjacentTerritory(this.territories.get(19));
		this.territories.get(17).setAdjacentTerritory(this.territories.get(15));
		this.territories.get(17).setAdjacentTerritory(this.territories.get(18));
		this.territories.get(17).setAdjacentTerritory(this.territories.get(24));
		this.territories.get(17).setAdjacentTerritory(this.territories.get(22));
		this.territories.get(17).setAdjacentTerritory(this.territories.get(32));
		// Ukraine
		this.territories.get(18).setAdjacentTerritory(this.territories.get(16));
		this.territories.get(18).setAdjacentTerritory(this.territories.get(15));
		this.territories.get(18).setAdjacentTerritory(this.territories.get(32));
		this.territories.get(18).setAdjacentTerritory(this.territories.get(17));
		this.territories.get(18).setAdjacentTerritory(this.territories.get(26));
		this.territories.get(18).setAdjacentTerritory(this.territories.get(36));
		// Western Europe
		this.territories.get(19).setAdjacentTerritory(this.territories.get(13));
		this.territories.get(19).setAdjacentTerritory(this.territories.get(15));
		this.territories.get(19).setAdjacentTerritory(this.territories.get(17));
		this.territories.get(19).setAdjacentTerritory(this.territories.get(24));
		// Congo
		this.territories.get(20).setAdjacentTerritory(this.territories.get(21));
		this.territories.get(20).setAdjacentTerritory(this.territories.get(24));
		this.territories.get(20).setAdjacentTerritory(this.territories.get(25));
		// East Africa
		this.territories.get(21).setAdjacentTerritory(this.territories.get(20));
		this.territories.get(21).setAdjacentTerritory(this.territories.get(22));
		this.territories.get(21).setAdjacentTerritory(this.territories.get(23));
		this.territories.get(21).setAdjacentTerritory(this.territories.get(25));
		this.territories.get(21).setAdjacentTerritory(this.territories.get(24));
		this.territories.get(21).setAdjacentTerritory(this.territories.get(32));
		// Egypt
		this.territories.get(22).setAdjacentTerritory(this.territories.get(24));
		this.territories.get(22).setAdjacentTerritory(this.territories.get(21));
		this.territories.get(22).setAdjacentTerritory(this.territories.get(17));
		this.territories.get(22).setAdjacentTerritory(this.territories.get(32));
		// Madagascar
		this.territories.get(23).setAdjacentTerritory(this.territories.get(21));
		this.territories.get(23).setAdjacentTerritory(this.territories.get(25));
		// North Africa
		this.territories.get(24).setAdjacentTerritory(this.territories.get(10));
		this.territories.get(24).setAdjacentTerritory(this.territories.get(17));
		this.territories.get(24).setAdjacentTerritory(this.territories.get(19));
		this.territories.get(24).setAdjacentTerritory(this.territories.get(22));
		this.territories.get(24).setAdjacentTerritory(this.territories.get(21));
		this.territories.get(24).setAdjacentTerritory(this.territories.get(20));
		// South Africa
		this.territories.get(25).setAdjacentTerritory(this.territories.get(20));
		this.territories.get(25).setAdjacentTerritory(this.territories.get(21));
		this.territories.get(25).setAdjacentTerritory(this.territories.get(23));
		// Afghanistan
		this.territories.get(26).setAdjacentTerritory(this.territories.get(18));
		this.territories.get(26).setAdjacentTerritory(this.territories.get(27));
		this.territories.get(26).setAdjacentTerritory(this.territories.get(28));
		this.territories.get(26).setAdjacentTerritory(this.territories.get(32));
		this.territories.get(26).setAdjacentTerritory(this.territories.get(36));
		// China
		this.territories.get(27).setAdjacentTerritory(this.territories.get(26));
		this.territories.get(27).setAdjacentTerritory(this.territories.get(28));
		this.territories.get(27).setAdjacentTerritory(this.territories.get(34));
		this.territories.get(27).setAdjacentTerritory(this.territories.get(33));
		this.territories.get(27).setAdjacentTerritory(this.territories.get(35));
		this.territories.get(27).setAdjacentTerritory(this.territories.get(36));
		// India
		this.territories.get(28).setAdjacentTerritory(this.territories.get(32));
		this.territories.get(28).setAdjacentTerritory(this.territories.get(26));
		this.territories.get(28).setAdjacentTerritory(this.territories.get(27));
		this.territories.get(28).setAdjacentTerritory(this.territories.get(34));
		// Irkutsk
		this.territories.get(29).setAdjacentTerritory(this.territories.get(33));
		this.territories.get(29).setAdjacentTerritory(this.territories.get(35));
		this.territories.get(29).setAdjacentTerritory(this.territories.get(31));
		this.territories.get(29).setAdjacentTerritory(this.territories.get(37));
		// Japan
		this.territories.get(30).setAdjacentTerritory(this.territories.get(31));
		this.territories.get(30).setAdjacentTerritory(this.territories.get(33));
		// Kamchatka
		this.territories.get(31).setAdjacentTerritory(this.territories.get(30));
		this.territories.get(31).setAdjacentTerritory(this.territories.get(33));
		this.territories.get(31).setAdjacentTerritory(this.territories.get(29));
		this.territories.get(31).setAdjacentTerritory(this.territories.get(37));
		this.territories.get(31).setAdjacentTerritory(this.territories.get(0));
		// Middle East
		this.territories.get(32).setAdjacentTerritory(this.territories.get(26));
		this.territories.get(32).setAdjacentTerritory(this.territories.get(28));
		this.territories.get(32).setAdjacentTerritory(this.territories.get(22));
		this.territories.get(32).setAdjacentTerritory(this.territories.get(21));
		this.territories.get(32).setAdjacentTerritory(this.territories.get(17));
		this.territories.get(32).setAdjacentTerritory(this.territories.get(18));
		// Mongolia
		this.territories.get(33).setAdjacentTerritory(this.territories.get(27));
		this.territories.get(33).setAdjacentTerritory(this.territories.get(30));
		this.territories.get(33).setAdjacentTerritory(this.territories.get(29));
		this.territories.get(33).setAdjacentTerritory(this.territories.get(35));
		this.territories.get(33).setAdjacentTerritory(this.territories.get(31));
		// Siam
		this.territories.get(34).setAdjacentTerritory(this.territories.get(28));
		this.territories.get(34).setAdjacentTerritory(this.territories.get(27));
		this.territories.get(34).setAdjacentTerritory(this.territories.get(39));
		// Siberia
		this.territories.get(35).setAdjacentTerritory(this.territories.get(36));
		this.territories.get(35).setAdjacentTerritory(this.territories.get(27));
		this.territories.get(35).setAdjacentTerritory(this.territories.get(33));
		this.territories.get(35).setAdjacentTerritory(this.territories.get(29));
		this.territories.get(35).setAdjacentTerritory(this.territories.get(37));
		// Ural
		this.territories.get(36).setAdjacentTerritory(this.territories.get(18));
		this.territories.get(36).setAdjacentTerritory(this.territories.get(26));
		this.territories.get(36).setAdjacentTerritory(this.territories.get(27));
		this.territories.get(36).setAdjacentTerritory(this.territories.get(35));
		// Yakutsk
		this.territories.get(37).setAdjacentTerritory(this.territories.get(35));
		this.territories.get(37).setAdjacentTerritory(this.territories.get(29));
		this.territories.get(37).setAdjacentTerritory(this.territories.get(31));
		// Eastern Australia
		this.territories.get(38).setAdjacentTerritory(this.territories.get(40));
		this.territories.get(38).setAdjacentTerritory(this.territories.get(41));
		// Indonesia
		this.territories.get(39).setAdjacentTerritory(this.territories.get(34));
		this.territories.get(39).setAdjacentTerritory(this.territories.get(40));
		this.territories.get(39).setAdjacentTerritory(this.territories.get(41));
		// New Guinea
		this.territories.get(40).setAdjacentTerritory(this.territories.get(39));
		this.territories.get(40).setAdjacentTerritory(this.territories.get(38));
		this.territories.get(40).setAdjacentTerritory(this.territories.get(41));
		// Western Australia
		this.territories.get(41).setAdjacentTerritory(this.territories.get(39));
		this.territories.get(41).setAdjacentTerritory(this.territories.get(40));
		this.territories.get(41).setAdjacentTerritory(this.territories.get(38));
	}
}
