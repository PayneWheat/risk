package main.java.game;
import java.util.*;

import javax.swing.JOptionPane;
public class Player 
{
	String name;
	String color;
	int armies;
	int diceValue;
	int territoriesOccupied;
	int countriesOccupied;
	int continentsOccupied;
	ArrayList<Card> cards;
	
	Player() {
		this.name = "";
		this.color = "";
		this.armies = 0;
		this.diceValue = 0;
		this.territoriesOccupied = 0;
		this.continentsOccupied = 0;
		this.countriesOccupied = 0;
		this.cards = new ArrayList<Card>();
	}
	public Player(String name, String color) {
		this.name = name;
		this.color = color;
		this.armies = 0;
		this.diceValue = 0;
		this.territoriesOccupied = 0;
		this.countriesOccupied = 0;
		this.continentsOccupied = 0;
		this.cards = new ArrayList<Card>();
	}
	public Player(String name, String color, int armies, int diceValue, int territoriesOccupied, int continentsOccupied){

		this.name = name;
		this.color = color;
		this.armies = armies;
		this.diceValue = diceValue;
		this.territoriesOccupied = countriesOccupied;
		this.continentsOccupied = continentsOccupied;	
		this.cards = new ArrayList<Card>();
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
	public int getDiceValue(){
		return diceValue;
	}
	public int getTerritoriesOccupied(){
		return territoriesOccupied;
	}
	public int continentsOccupied(){
		return continentsOccupied;
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
	/**
	 * 
	 * @param initialTurns boolean flag indicating whether or not it's the initial placement step.
	 * If true, player can only place one army at a time.
	 * @return index of territory picked
	 */
	public int pickTerritory(boolean initialTurns) {
		int ti = -1;
		String territoryIndex = "";
		if(initialTurns)
			territoryIndex = JOptionPane.showInputDialog(getName() + ", input a territory index to place one army (" + getArmies() + " remaining)");
		else
			territoryIndex = JOptionPane.showInputDialog(getName() + ", input a territory index to place at least one army (" + getArmies() + " remaining)");
		
		// TODO: Abstract out a function for parsing input from JOptionPane 
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
		return ti;
	}
	
	//TODO: move to Player class
	/**
	 * Prompts user to choose how many armies to place on a territory.
	 * @return number of armies to be placed
	 */
	public int chooseArmiesQty() {
		String armyQty = JOptionPane.showInputDialog(getName() + ", how many of your " + getArmies() + " remaining armies?");
		int armies = 0;
		try {
			armies = Integer.parseInt(armyQty);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			armies = chooseArmiesQty();
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			armies = chooseArmiesQty();
		}
		if(armies > getArmies()) {
			System.out.println("You don't have that many armies.");
			armies = chooseArmiesQty();
		} else if(armies < 0) {
			System.out.println("Choose a number between 0 and " + getArmies());
			armies = chooseArmiesQty();
		}
		return armies;
	}

	
	public Territory chooseAttackingTerritory(ArrayList<Territory> allTerritories, ArrayList<Territory> territories) {
		// The territory must have at least 2 troops, and needs to be
		//	 adjacent to a territory occupied by an opponent. (i.e., if 
		//	 a player's territory is completely surrounded by his own, other territories)
		//ArrayList<Territory> allTerritories = getPlayersTerritories();
		ArrayList<Territory> attackTerritories = new ArrayList<Territory>();
		System.out.println("\nChoose one of your territories to attack from:\nNote: This list only contains territories you occupy with at least 2 armies");
		for(int i = 0; i < allTerritories.size(); i++) {
			if(allTerritories.get(i).getArmyCount() > 1) {
				// TODO: check if territory is completely surrounded by current player's
				//			territories (i.e., nowhere to attack)
				ArrayList<Territory> tempAdjacencies = allTerritories.get(i).getAdjacentTerritories(true, false, false);
				attackTerritories.add(allTerritories.get(i));
				int tempIndex = territories.indexOf(allTerritories.get(i));
				System.out.print("[" + tempIndex + "]" + allTerritories.get(i).getTerritoryName() + ": " + allTerritories.get(i).getArmyCount() + " armies; -> { ");
				for(int j = 0; j < tempAdjacencies.size(); j++) {
					System.out.print(tempAdjacencies.get(j).getTerritoryName() + " (" + tempAdjacencies.get(j).getPlayer().getName() + "): " + tempAdjacencies.get(j).getArmyCount() + "; ");
				}
				System.out.println("}");
			}
		}
		String attackingTerritoryInput = JOptionPane.showInputDialog(getName() + ", choose a territory to attack from.");
		int attackingTerritoryIndex = -1;
		Territory tempTerritory = new Territory();
		// TODO: abstract out a function to parse JOptionPane input
		try {
			attackingTerritoryIndex = Integer.parseInt(attackingTerritoryInput);
			tempTerritory = territories.get(attackingTerritoryIndex);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			tempTerritory = chooseAttackingTerritory(allTerritories, territories);
		} catch(Exception e) {
			System.out.println("Error: " + e + "\nTry again");
			tempTerritory = chooseAttackingTerritory(allTerritories, territories);
		}
		/*
		// TODO: Change these to try/catch blocks and throw proper exceptions
		if(tempTerritory.getPlayer() != this) {
			System.out.println("You do not occupy selected territory. Try again");
			attackingTerritoryIndex = chooseAttackingTerritory(allTerritories, territories);
		}
		*/
		return tempTerritory;
	}
	/**
	 * Prompts player to choose a territory to attack.
	 * For use when the player is in the attack step of their turn.
	 * @param attackingTerritory Territory chosen prior to attack from
	 * @return Territory selected to be attacked
	 */
	public Territory chooseTerritoryToAttack(Territory attackingTerritory, ArrayList<Territory> territories) {
		// Display adjacent territories occupied by another player
		// and prompt the user to select one
		Territory tempTerritory = new Territory();
		ArrayList<Territory> opposingAdjacents = attackingTerritory.getAdjacentTerritories(true, false, false);
		for(int i = 0; i < opposingAdjacents.size(); i++) {
			int tempIndex = territories.indexOf(opposingAdjacents.get(i));
			System.out.println("[" + tempIndex + "]" + opposingAdjacents.get(i).getTerritoryName() + " (" + opposingAdjacents.get(i).getPlayer().getName()  + "): " + opposingAdjacents.get(i).getArmyCount() + " armies.");
		}
		String defendingTerritoryInput = JOptionPane.showInputDialog(getName() + ", choose an opponent's territory to attack.");
		int defendingTerritoryIndex = -1;
		try {
			defendingTerritoryIndex = Integer.parseInt(defendingTerritoryInput);
			tempTerritory = territories.get(defendingTerritoryIndex);
		} catch(NumberFormatException e) {
			// not an int
			System.out.println("Could not parse number. Try again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories);
		} catch(Exception e) {
			// not a territory index
			System.out.println("Error: " + e + "\nTry again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories);
		}
		if(tempTerritory.getPlayer() == this) {
			System.out.println("You cannot attack your own territory. Try again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories);
		}
		// search opposingAdjacents for territories.get(defendingTerritoryIndex); ensure it is adjacent
		if(!opposingAdjacents.contains(tempTerritory)) {
			System.out.println("That territory is not adjacent to the attacking territory you selected.\nTry again");
			tempTerritory = chooseTerritoryToAttack(attackingTerritory, territories);
		}
		// add option to cancel and go back to step before (choosing attacking territory).
		return tempTerritory;
	}
	
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
							if(cardSetInput.equals("0") || cardSetInput.equals("1") || cardSetInput.equals("2") || cardSetInput.equals("3")){
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
					/*
					int bonusArmies = turnInCardSet(this, CardSets.get(cardSetIndex));
					System.out.println(getName() + " turned in a set of Risk Cards and was awarded " + bonusArmies + " armies.");
					increaseArmies(bonusArmies);
					*/
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
	 * In practice, this only needs to be called when the player has more than one possible sets of cards to turn in.
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
	 * If the player has three of a kind, find all the possible subsets of the card type 
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
	 * Organizes the player's cards to be supplied to the proper subset function
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
	 * Nested loop to determine all possible combinations of sets of Risk cards where the player has one of each card type
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
	 * Returns all possible subsets of cards with a wildcard
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
	 * 
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
