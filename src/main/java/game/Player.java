package main.java.game;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Player {
	String name;
	String color;
	int armies;
	ArrayList<Card> cards;
	int currency;
	int credits;
	String attackMessage;
	ArrayList<Observer> observers;
	public Player() {
		this.name = "";
		this.color = "";
		this.armies = 0;
		this.cards = new ArrayList<Card>();
		this.currency = 0;
		this.credits = 0;
		this.attackMessage = "";
		this.observers = new ArrayList<Observer>();
	}
	public Player(String name, String color, int currency, int credits) {
		this.name = name;
		this.color = color;
		this.armies = 0;
		this.cards = new ArrayList<Card>();
		this.currency = currency;
		this.credits = credits;
		this.attackMessage = "";
		this.observers = new ArrayList<Observer>();
	}

	public String getName() {
		return name;
	}
	public String getColor(){
		return color;
	}
	public int getArmies(){
		return armies;
	}
	public int getCurrency(){
		return currency;
	}
	public int getCredits(){
		return credits;
	}
	public void increaseArmies(int numOfArmies) {
		armies = armies + numOfArmies;
		System.out.println(name + " increased " + numOfArmies + " armies.");
	}
	public void decreaseArmies(int numOfArmies) {
		armies = armies - numOfArmies;
		System.out.println(name + " lost " + numOfArmies + " armies.");
	}
	public void addCard(Card card) {
		cards.add(card);
	}
	public void useCurrency(int currency){
		this.currency = this.currency - currency;
	}
	public void buyCredits(int credits){
		this.credits = this.credits + credits;
	}
	public void useCredits(int credits){
		this.credits = this.credits - credits;
	}
	public void addObserver(Observer observer) {
        observers.add(observer);
   	 }
    public void removeObserver(Observer observer) {
        observers.remove(observer);
	}
	public String getAttackMessage(){
		return attackMessage;
	}
	public void setAttackMessage(Player p, String attackMessage){
		this.attackMessage = attackMessage;
		//JOptionPane.showMessageDialog(null, attackMessage, "warning", JOptionPane.WARNING_MESSAGE);
		System.out.println(attackMessage);
		for (Observer observer : observers) {
           	observer.update(p, this.attackMessage);
        	}
	}
	
	/**
	 * 
	 * @param initialTurns boolean flag indicating whether or not it's the initial placement step.
	 * If true, player can only place one army at a time.
	 * @return index of territory picked
	 */
	public int pickTerritory(boolean initialTurns, Board b) {
		int ti = -1;
		String territoryIndex = "";
		
		if(initialTurns) {
			//territoryIndex = JOptionPane.showInputDialog(getName() + ", input a territory index to place one army (" + getArmies() + " remaining)");
			String inputMessage = getName() + ", input a territory index to place one army (" + getArmies() + " remaining)";
			try {
				territoryIndex = b.timedPrompt(inputMessage);
			} catch(Exception e) {
				System.out.println(e.getStackTrace());
			}
			if(territoryIndex == null) {
				// automatically pick a territory
				return -1;
			}
		}
		else {
			//territoryIndex = JOptionPane.showInputDialog(getName() + ", input a territory index to place at least one army (" + getArmies() + " remaining)");
			String inputMessage = getName() + ", input a territory index to place at least one army (" + getArmies() + " remaining)";
			try {
				territoryIndex = b.timedPrompt(inputMessage);
			} catch(Exception e) {
				System.out.println(e.getStackTrace());
			}
			if(territoryIndex == null) {
				// automatically pick a territory
				return -1;
			}
		}

		ti = numberInputParser(territoryIndex);
		if(ti < 0) {
			ti = pickTerritory(initialTurns, b);
		}
		return ti;
	}
	
	public int numberInputParser(String input) {
		int number = -1;
		try {
			number = Integer.parseInt(input);
			if(number < 0) {
				number = -1;
				throw new IllegalArgumentException("Must be a positive number.");
			}
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
		} catch(Exception e) {
			System.out.println("Error: " + e + "\nTry again");
		}
		return number;
	}
	
	/**
	 * Prompts user to choose how many armies to place on a territory.
	 * @return number of armies to be placed
	 */
	public int chooseArmiesQty(Board b) {
		boolean undo = true;
		int armies = 0;
		while(undo){
			//String armyQty = JOptionPane.showInputDialog(getName() + ", how many of your " + getArmies() + " remaining armies?");
			String inputMessage = getName() + ", how many of your " + getArmies() + " remaining armies?";
			String armyQty = null;
			try {
				armyQty = b.timedPrompt(inputMessage);
			} catch(Exception e) {
				System.out.println(e.getStackTrace());
			}
			if(armyQty == null) {
				System.out.println("Null input received, placing all remaining armies on this territory.");
				return this.armies;
			}
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to place " + Integer.parseInt(armyQty) + " armies", 
			        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
			        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			if (n == JOptionPane.NO_OPTION) {
				if(getCredits() > 0){
					--credits;
				}
				else{
					JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
					undo = false;
				}
	        } 
			else if(n == JOptionPane.YES_OPTION){
				undo = false;
			}
			armies = numberInputParser(armyQty);

			if(armies > getArmies()) {
				System.out.println("You don't have that many armies.");
				armies = chooseArmiesQty(b);
				undo = true;
			} else if(armies < 0) {
				System.out.println("Choose a number between 0 and " + getArmies());
				armies = chooseArmiesQty(b);
				undo = true;
			}
		}
		return armies;
	}
	public ArrayList<Territory> territoriesThatCanAttack(ArrayList<Territory> playerTerritories, ArrayList<Territory> allTerritories) {
		System.out.println("\nChoose one of your territories to attack from:\nNote: This list only contains territories you occupy with at least 2 armies");
		ArrayList<Territory> attackTerritories = new ArrayList<Territory>();
		for(int i = 0; i < playerTerritories.size(); i++) {
			ArrayList<Territory> tempAdjacencies = playerTerritories.get(i).getAdjacentTerritories(true, false, false);
			if(playerTerritories.get(i).getArmyCount() > 1 && tempAdjacencies.size() > 0) {
				attackTerritories.add(playerTerritories.get(i));
				int tempIndex = allTerritories.indexOf(playerTerritories.get(i));
				System.out.print("[" + tempIndex + "]" + playerTerritories.get(i).getTerritoryName() + ": " + playerTerritories.get(i).getArmyCount() + " armies; -> { ");
				for(int j = 0; j < tempAdjacencies.size(); j++) {
					System.out.print(tempAdjacencies.get(j).getTerritoryName() + " (" + tempAdjacencies.get(j).getPlayer().getName() + "): " + tempAdjacencies.get(j).getArmyCount() + "; ");
				}
				System.out.println("}");
			}
		}
		return attackTerritories;
	}
	public Territory chooseAttackingTerritory(ArrayList<Territory> playerTerritories, ArrayList<Territory> allTerritories, Board b) {
		// The territory must have at least 2 troops, and needs to be
		//	 adjacent to a territory occupied by an opponent. (i.e., if 
		//	 a player's territory is completely surrounded by his own, other territories)
		ArrayList<Territory> attackTerritories = territoriesThatCanAttack(playerTerritories, allTerritories);
		
		//String attackingTerritoryInput = JOptionPane.showInputDialog(getName() + ", choose a territory to attack from.");
		String attackingTerritoryInput = null;
		try {
			String inputMessage = getName() + ", choose a territory to attack from.";
			attackingTerritoryInput = b.timedPrompt(inputMessage);
		} catch(Exception e) {
			
		}
		if(attackingTerritoryInput == null) {
			System.out.println("Null input received, default action.");
			return null;
		}
		int attackingTerritoryIndex = numberInputParser(attackingTerritoryInput);
		Territory tempTerritory = new Territory();
		if(attackingTerritoryIndex < 0)
			tempTerritory = chooseAttackingTerritory(playerTerritories, allTerritories, b);
		else
			tempTerritory = allTerritories.get(attackingTerritoryIndex);
		return tempTerritory;
	}
	
	/**
	 * Prompts player to choose a territory to attack.
	 * For use when the player is in the attack step of their turn.
	 * @param attackingTerritory Territory chosen prior to attack from
	 * @return Territory selected to be attacked
	 */
	public Territory chooseTerritoryToAttack(Territory attackingTerritory, ArrayList<Territory> territories, Board b) {
		// Display adjacent territories occupied by another player
		// and prompt the user to select one
		Territory tempTerritory = new Territory();
		ArrayList<Territory> opposingAdjacents = attackingTerritory.getAdjacentTerritories(true, false, false);
		for(int i = 0; i < opposingAdjacents.size(); i++) {
			int tempIndex = territories.indexOf(opposingAdjacents.get(i));
			System.out.println("[" + tempIndex + "]" + opposingAdjacents.get(i).getTerritoryName() + " (" + opposingAdjacents.get(i).getPlayer().getName()  + "): " + opposingAdjacents.get(i).getArmyCount() + " armies.");
		}
		//String defendingTerritoryInput = JOptionPane.showInputDialog(getName() + ", choose an opponent's territory to attack.");
		String defendingTerritoryInput = null;
		try {
			String inputMessage = getName() + ", choose an opponent's territory to attack.";
			defendingTerritoryInput = b.timedPrompt(inputMessage);
		} catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
		if(defendingTerritoryInput == null) {
			return null;
		}
		
		int defendingTerritoryIndex = numberInputParser(defendingTerritoryInput);
		if(defendingTerritoryIndex < 0) {
			System.out.println("Try again.");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories, b);
		}
		tempTerritory = territories.get(defendingTerritoryIndex);
		if(tempTerritory.getPlayer() == this) {
			System.out.println("You cannot attack your own territory. Try again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories, b);
		}
		// search opposingAdjacents for territories.get(defendingTerritoryIndex); ensure it is adjacent
		if(!opposingAdjacents.contains(tempTerritory)) {
			System.out.println("That territory is not adjacent to the attacking territory you selected.\nTry again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories, b);
		}
		// add option to cancel and go back to step before (choosing attacking territory).
		return tempTerritory;
	}
	
	/**
	 * Checks if this player has a set of risk cards to turn in
	 * If true, prompt the player which set of cards to turn in,
	 * also force the player to select a set of cards if they have more than 5 cards total. 
	 * @return set of cards to be exchanged for bonus armies
	 */
	public ArrayList<Card> cardCheck() {
		for(int i = 0; i < cards.size(); i++) {
			System.out.println("\t" + cards.get(i).getCardTypeName() + " (" + cards.get(i).getTerritoryName() + ")");
		}
		int infantryCount = 0;
		int cavalryCount = 0;
		int artilleryCount = 0;
		int wildCount = 0;
		for(int i = 0; i < cards.size(); i++) {
			if(cards.get(i).getCardTypeName() == "Infantry") {
				infantryCount++;
			}
			else if(cards.get(i).getCardTypeName() == "Cavalry") {
				cavalryCount++;
			}
			else if(cards.get(i).getCardTypeName() == "Artillery") {
				artilleryCount++;
			}
			else if(cards.get(i).getCardTypeName() == "Wild") {
				wildCount++;
			}
		}
		boolean threeOfAKind = infantryCount > 2 || cavalryCount > 2 || artilleryCount > 2;
		boolean oneOfEach = infantryCount > 0 && cavalryCount > 0 && artilleryCount > 0;
		boolean wildCardSet = wildCount > 0 && cards.size() > 2;
		if(threeOfAKind || oneOfEach || wildCardSet) {
			System.out.println("You have a set of Risk cards you can turn in.");
			// if player's card count is greater than 4, must turn cards in.
			boolean mustTurnIn = false;
			if(cards.size() > 4) {
				System.out.println("You have 5 or more cards. You MUST turn a set of cards in.");
				mustTurnIn = true;
			}
			ArrayList<ArrayList<Card>> CardSets = new ArrayList<ArrayList<Card>>();
			if(threeOfAKind) {
				ArrayList<ArrayList<Card>> tempSets = threeOfAKindExtractor(cards, infantryCount, cavalryCount, artilleryCount);
				for(int i = 0; i < tempSets.size(); i++) {
					CardSets.add(tempSets.get(i));
				}
			}
			if(oneOfEach) {
				// add each possible set to CardSet
				ArrayList<ArrayList<Card>> tempSets = oneOfEachExtractor(cards, infantryCount, cavalryCount, artilleryCount);
				for(int i = 0; i < tempSets.size(); i++) {
					CardSets.add(tempSets.get(i));
				}
			}
			if(wildCardSet) {
				// add each possible set to CardSet
				ArrayList<ArrayList<Card>> tempSets = wildSetsExtractor(cards, wildCount);
				for(int i = 0; i < tempSets.size(); i++) {
					CardSets.add(tempSets.get(i));
				}
			}
			System.out.println("Card sets that can be turned in:");
			for(int i = 0; i < CardSets.size(); i++) {
				System.out.println("Set [" + i + "]");
				for(int j = 0; j < CardSets.get(i).size(); j++) {
					System.out.println("\t" + CardSets.get(i).get(j).getCardTypeName() + " (" + CardSets.get(i).get(j).getTerritoryName() + ")");
				}
			}
			boolean tryAgain = true;
			boolean trySetAgain = true;
			int cardSetIndex = -1;
			String cardSetInput = "";
			while(tryAgain) {
				try {
					if(mustTurnIn) {
						while(trySetAgain){
							cardSetInput = JOptionPane.showInputDialog(getName() + ", select a set of cards to turn in. (You must select a set)");
							if(cardSetInput.equals("0") || cardSetInput.equals("1") || cardSetInput.equals("2") || cardSetInput.equals("3") || cardSetInput.equals("4")|| cardSetInput.equals("5") || cardSetInput.equals("6") || cardSetInput.equals("7")){
									trySetAgain = false;
							}
							else{
									JOptionPane.showMessageDialog(null, "You have entered an invalid set. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
							}
						}
						if(cardSetInput != null) {
							cardSetIndex = Integer.parseInt(cardSetInput);
						}
					} else {
						cardSetInput = JOptionPane.showInputDialog(getName() + ", select a set of cards to turn in. Click cancel to wait.");
						if(cardSetInput == null) {
							tryAgain = false;
							break;
						}
						cardSetIndex = Integer.parseInt(cardSetInput);
					}
						
					tryAgain = false;
				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
				}	
			}
			return CardSets.get(cardSetIndex);
		}
		return null;
	}
	
	/**
	 * Adds all possible subsets of supplied cards to the subsets parameter
	 * @param organizedCards An ArrayList of cards to check for each unique permutation. 
	 * In practice, this only needs to be called when the player has more than one possible set of cards to turn in.
	 * Additionally, this only needs to be called when the player has three of a kind or one or more wild cards
	 * In the case the player has one or more of each card, the oneOfEachExtractor method will be called. 
	 * @param n organizedCards size
	 * @param r Subset size
	 * @param index
	 * @param data Temp array of the current set of cards being produced
	 * @param i 
	 * @param subsets 2D ArrayList of all subsets of size r
	 */
	private void getSubsetsUtil(ArrayList<Card> organizedCards, int n, int r, int index, Card[] data, int i, ArrayList<ArrayList<Card>> subsets) {
		if(index == r) {
			for(int j = 0; j < r; j++) {
				ArrayList<Card> tempList = new ArrayList<Card>(Arrays.asList(data));
				subsets.add(tempList);
				return;
			}
		}
		if(i >= n)
			return;
		data[index] = organizedCards.get(i);
		getSubsetsUtil(organizedCards, n, r, index + 1, data, i + 1, subsets);
		
		getSubsetsUtil(organizedCards, n, r, index, data, i + 1, subsets);
	}
	/**
	 * Returns all possible subsets from supplied, organized cards
	 * @param organizedCards needs to contain only the cards that make up all of the three of a kind subsets
	 * @return 2D ArrayList of all possible subsets of size 3
	 */
	private ArrayList<ArrayList<Card>> threeOfAKindSubsets(ArrayList<Card> organizedCards) {
		Card[] data = new Card[3];
		ArrayList<ArrayList<Card>> subsets = new ArrayList<ArrayList<Card>>();
		getSubsetsUtil(organizedCards, organizedCards.size(), 3, 0, data, 0, subsets);
		return subsets;
	}
	/**
	 * Organizes the player's cards to be supplied to the threeOfAKindSubsets method
	 * @param playerCards Current player's cards
	 * @param infantryCount The count of infantry cards the player is in possession of
	 * @param cavalryCount The count of cavalry cards the player is in possession of
	 * @param artilleryCount The count of artillery cards the player is in possession of
	 * @return 2D ArrayList of all possible three of a kind subsets
	 */
	public ArrayList<ArrayList<Card>> threeOfAKindExtractor(ArrayList<Card> playerCards, int infantryCount, int cavalryCount, int artilleryCount) {
		ArrayList<ArrayList<Card>> CardSets = new ArrayList<ArrayList<Card>>();
		if(infantryCount >= 3) {
			// add each possible set to CardSet
			ArrayList<Card> tempSet = new ArrayList<Card>();
			for(int i = 0; i < playerCards.size(); i++) {
				if(playerCards.get(i).getCardTypeName() == "Infantry") {
					tempSet.add(playerCards.get(i));
				}
			}
			ArrayList<ArrayList<Card>> temp2DSet = threeOfAKindSubsets(tempSet);
			for(int i = 0; i < temp2DSet.size(); i++) {
				CardSets.add(temp2DSet.get(i));
			}
		}
		if(cavalryCount >= 3) {
			// add each possible set to CardSet

			ArrayList<Card> tempSet = new ArrayList<Card>();
			for(int i = 0; i < playerCards.size(); i++) {
				if(playerCards.get(i).getCardTypeName() == "Cavalry") {
					tempSet.add(playerCards.get(i));
				}
			}
			ArrayList<ArrayList<Card>> temp2DSet = threeOfAKindSubsets(tempSet);
			for(int i = 0; i < temp2DSet.size(); i++) {
				CardSets.add(temp2DSet.get(i));
			}		
		}
		
		if(artilleryCount >= 3) {
			ArrayList<Card> tempSet = new ArrayList<Card>();
			for(int i = 0; i < playerCards.size(); i++) {
				if(playerCards.get(i).getCardTypeName() == "Artillery") {
					tempSet.add(playerCards.get(i));
				}
			}
			ArrayList<ArrayList<Card>> temp2DSet = threeOfAKindSubsets(tempSet);
			for(int i = 0; i < temp2DSet.size(); i++) {
				CardSets.add(temp2DSet.get(i));
			}
		}
		
		return CardSets;
	}
	/**
	 * Extracts all combinations of risk cards where there are one of each card type (excluding wilds)
	 * @param playerCards Current player's cards
	 * @param infantryCount The count of infantry cards the player is in possession of
	 * @param cavalryCount The count of cavalry cards the player is in possession of
	 * @param artilleryCount The count of artillery cards the player is in possession of
	 * @return 2D ArrayList of all possible three of a kind subsets
	 */
	public ArrayList<ArrayList<Card>> oneOfEachExtractor(ArrayList<Card> playerCards, int infantryCount, int cavalryCount, int artilleryCount) {
		ArrayList<ArrayList<Card>> CardSets = new ArrayList<ArrayList<Card>>();
		for(int i = 0; i < infantryCount; i++) {
			for(int c = 0; c < cavalryCount; c++) {
				for(int a = 0; a < artilleryCount; a++) {
					ArrayList<Card> tempSet = new ArrayList<Card>();
					int ic = 0;
					int cc = 0;
					int ac = 0;
					for(int k = 0; k < playerCards.size(); k++) {
						if(playerCards.get(k).getCardTypeName() == "Infantry") {
							if(ic == i) {
								tempSet.add(playerCards.get(k));
							}
							ic++;
						}
						if(playerCards.get(k).getCardTypeName() == "Cavalry") {
							if(cc == c) {
								tempSet.add(playerCards.get(k));
							}
							cc++;
						}
						if(playerCards.get(k).getCardTypeName() == "Artillery") {
							if(ac == a) {
								tempSet.add(playerCards.get(k));
							}
							ac++;
						}
					}
					CardSets.add(tempSet);
				}
			}
		}
		return CardSets;
	}
	
	
	/**
	 * Returns all possible subsets of cards with a wildcard from a properly organized list of cards 
	 * @param organizedCards
	 * @return subsets each subset from the supplied cards as a list
	 */
	private ArrayList<ArrayList<Card>> wildSubsets(ArrayList<Card> organizedCards) {
		ArrayList<ArrayList<Card>> subsets = new ArrayList<ArrayList<Card>>();
		Card[] data = new Card[2];
		getSubsetsUtil(organizedCards, organizedCards.size(), 2, 0, data, 0, subsets);
		return subsets;
	}
	
	/**
	 * Organizes cards to be passed to wildSubsets method
	 * @param playerCards
	 * @param wildCount
	 * @return 
	 */
	public ArrayList<ArrayList<Card>> wildSetsExtractor(ArrayList<Card> playerCards, int wildCount) {
		ArrayList<ArrayList<Card>> tempSets = new ArrayList<ArrayList<Card>>();
		// create a set with all other possible cards
		//ArrayList<Card> finalSets = new ArrayList<Card>();
		Card commonCard = new Card();
		ArrayList<Card> organizedCards = new ArrayList<Card>();
		// excluding the first wild card, create every subset of size 2
		boolean noWilds = true;
		for(int j = 0; j < playerCards.size(); j++) {
			if(playerCards.get(j).getCardTypeName() == "Wild" && noWilds) {
				commonCard = playerCards.get(j);
				noWilds = false;
			} else {
				organizedCards.add(playerCards.get(j));
			}
		}
		ArrayList<ArrayList<Card>> subsets = wildSubsets(organizedCards);
		for(int i = 0; i < subsets.size(); i++) {
			subsets.get(i).add(commonCard);
			tempSets.add(subsets.get(i));
		}
		return tempSets;
	}
	
}
