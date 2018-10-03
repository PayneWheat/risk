package main.java.game;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Board {
	public ArrayList<Territory> territories = new ArrayList<Territory>();
	public ArrayList<Continent> continents = new ArrayList<Continent>();
	private ArrayList<Card> cards = new ArrayList<Card>();
	public int cardSetsTurnedIn;
	public ArrayList<Player> players = new ArrayList<Player>();
	public int currentPlayerIndex;
	public int initialArmies;
	
	public Board() {
		generateGraph();
		this.cards = createCardDeck();
		this.cardSetsTurnedIn = 0;
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
		
		// Roll dice for each player to determine who goes first
		Dice d = new Dice();
		int initRolls[] = new int[numOfPlayers];
		int maxIndex = 0;
		initRolls[0] = d.getDiceValue();
		
		// Sorting and displaying results
		System.out.println(players[0].getName() + " rolled a " + initRolls[0]);
		for(int i = 1; i < numOfPlayers; i++) {
			initRolls[i] = d.getDiceValue();
			System.out.println(players[i].getName() + " rolled a " + initRolls[i]);
			if(initRolls[maxIndex] < initRolls[i])
				maxIndex = i;
		}
		System.out.println(players[maxIndex].getName() + " goes first.");
		
		// Determine the number of initial armies to place by the number of players
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
		
		// TODO: Find a way to tiebreak the dice rolls in this situation
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
		
		// Converting array parameter Players into an ArrayList
		this.players = new ArrayList<Player>(Arrays.asList(players));
		for(int i = 0; i < numOfPlayers; i++) {
			System.out.println(" The order of players to play is " + players[i].getName());
		}

		// CARD PILOT -- just checking that the cards are working
		for(int i = 0; i < numOfPlayers; i++) {
			for(int j = 0; j < 5; j++) {
				this.players.get(i).addCard(drawCard());
			}
		}
	}
	
	/**
	 * @param automatic automatically places initial troops if true
	 * The first step in a game of Risk: initial troop placement.
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
		//System.out.println(currentPlayer.getName() + ", choose a territory index to place one army: ");
		int i = 0;
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
					i = 0;
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
	/**
	 * 
	 * @param initialTurns boolean flag indicating whether or not it's the initial placement step.
	 * If true, player can only place one army at a time.
	 * @return index of territory picked
	 */
	private int pickTerritory(boolean initialTurns) {
		int ti = -1;
		String territoryIndex = "";
		boolean undo = true;
		while(undo){
			if(initialTurns){
				territoryIndex = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", input a territory index to place one army (" + players.get(currentPlayerIndex).getArmies() + " remaining)");
			}
		else{
			territoryIndex = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", input a territory index to place at least one army (" + players.get(currentPlayerIndex).getArmies() + " remaining)");
		}    
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen " + territories.get(Integer.parseInt(territoryIndex)).getTerritoryName(), 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			 if (n == JOptionPane.YES_OPTION) {
		            undo = false;
		        } else if (n == JOptionPane.NO_OPTION) {
		            undo = true;
		        }
		}
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
		
		// TODO: change these to a try/catch block. Throw proper exceptions
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
	
	/**
	 * Prompts user to choose how many armies to place on a territory.
	 * @return number of armies to be placed
	 */
	
	private int placeArmies() {
		boolean undo = true;
		int armies = 0;
		while(undo){
			String armyQty = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", how many of your " + players.get(currentPlayerIndex).getArmies() + " remaining armies?");
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to place " + armyQty + " armies", 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			 if (n == JOptionPane.YES_OPTION) {
		            undo = false;
		        } else if (n == JOptionPane.NO_OPTION) {
		            undo = true;
		        }
				try {
					armies = Integer.parseInt(armyQty);
				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
					armies = placeArmies();
					undo = true;
				} catch(Exception e) {
					// not a territory index
					System.out.println("Error: " + e + "\nTry again");
					armies = placeArmies();
					undo = true;
				}
				if(armies > players.get(currentPlayerIndex).getArmies()) {
					System.out.println("You don't have that many armies.");
					armies = placeArmies();
					undo = true;
				} else if(armies < 0) {
					System.out.println("Choose a positive number less than " + players.get(currentPlayerIndex).getArmies());
					armies = placeArmies();
					undo = true;
				}
		}
		return armies;
	}
	
	/**
	 * Increments the current player index value.
	 * If the value is equal to the total number of players - 1, reset the index to 0
	 */
	public void incrementCurrentPlayerIndex() {
		if(currentPlayerIndex == players.size() - 1)
			currentPlayerIndex = 0;
		else
			currentPlayerIndex++;
	}
	
	/**
	 * Returns the number of unoccupied territories remaining on the board
	 * For use during the initial army placements.
	 * @return the number of unoccupied territories remaining
	 */
	public int unoccupiedTerritoriesCount() {
		int count = 0;
		for(int i = 0; i < territories.size(); i++) {
			if(!territories.get(i).isOccupied()) {
				count++;
			}
		}
		return count;
	}
	/**
	 * Returns the current player's territory count
	 * For use when calculating replenishment armies
	 * @return count of territories occupied by current player
	 */
	public int getCurPlayerTerritoriesCount() {
		int count = 0;
		for(int i = 0; i < territories.size(); i++) {
			if(territories.get(i).getPlayer() == players.get(currentPlayerIndex)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calculates how many armies are to be given to the player at the beginning of the turn
	 * @return number of armies gained
	 */
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
	
	/**
	 * Returns an ArrayList of the specified player's territories.
	 * @param player which player to fetch territories for
	 * @return ArrayList of the player's territories
	 */
	private ArrayList<Territory> getPlayersTerritories(Player player) {
		ArrayList<Territory> playersTerritories = new ArrayList<Territory>();
		for(int i = 0; i < territories.size(); i++) {
			if(territories.get(i).getPlayer() == player) {
				playersTerritories.add(territories.get(i));
			}
		}
		return playersTerritories;
	}
	
	/**
	 * Prompts user to select one of their territories to attack from
	 * For use during the attack step of a player's turn
	 * @return Territory selected
	 */
	private Territory chooseAttackingTerritory() {
		// The territory must have at least 2 troops, and needs to be
		//	 adjacent to a territory occupied by an opponent. (i.e., if 
		//	 a player's territory is completely surrounded by his own, other territories)
		ArrayList<Territory> allTerritories = getPlayersTerritories(players.get(currentPlayerIndex));
		ArrayList<Territory> attackTerritories = new ArrayList<Territory>();
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
		boolean undo = true;
		Territory tempTerritory = new Territory();
		while(undo){
			String attackingTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose a territory to attack from.");
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to attack from " + territories.get(Integer.parseInt(attackingTerritoryInput)).getTerritoryName(), 
			        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
			        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			 if (n == JOptionPane.YES_OPTION) {
		            undo = false;
		        } else if (n == JOptionPane.NO_OPTION) {
		            undo = true;
		        }
			int attackingTerritoryIndex = -1;
			// TODO: abstract out a function to parse JOptionPane input
			try {
				attackingTerritoryIndex = Integer.parseInt(attackingTerritoryInput);
				tempTerritory = territories.get(attackingTerritoryIndex);
			} catch(NumberFormatException e) {
				// not an int
				System.out.println("Could not parse number. Try again");
				tempTerritory = chooseAttackingTerritory();
				undo = false;
			} catch(Exception e) {
				// not a territory index
				System.out.println("Error: " + e + "\nTry again");
				tempTerritory = chooseAttackingTerritory();
				undo = false;
			}
			// TODO: Change these to try/catch blocks and throw proper exceptions
			if(tempTerritory.getPlayer() != players.get(currentPlayerIndex)) {
				System.out.println("You do not occupy selected territory. Try again");
				tempTerritory = chooseAttackingTerritory();
				undo = false;
			}	
		}
		return tempTerritory;
	}
	
	/**
	 * Prompts player to choose a territory to attack.
	 * For use when the player is in the attack step of their turn.
	 * @param attackingTerritory Territory chosen prior to attack from
	 * @return Territory selected to be attacked
	 */
	private Territory chooseTerritoryToAttack(Territory attackingTerritory) {
		// Display adjacent territories occupied by another player
		// and prompt the user to select one
		Territory tempTerritory = new Territory();
		ArrayList<Territory> opposingAdjacents = attackingTerritory.getAdjacentTerritories(true, false, false);
		for(int i = 0; i < opposingAdjacents.size(); i++) {
			int tempIndex = territories.indexOf(opposingAdjacents.get(i));
			System.out.println("[" + tempIndex + "]" + opposingAdjacents.get(i).getTerritoryName() + " (" + opposingAdjacents.get(i).getPlayer().getName()  + "): " + opposingAdjacents.get(i).getArmyCount() + " armies.");
		}
		boolean undo = true;
		while(undo){
			String defendingTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose an opponent's territory to attack.");
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to attack " + territories.get(Integer.parseInt(defendingTerritoryInput)).getTerritoryName(), 
			        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
			        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			 if (n == JOptionPane.YES_OPTION) {
		            undo = false;
		        } else if (n == JOptionPane.NO_OPTION) {
		            undo = true;
		        }
			 int defendingTerritoryIndex = -1;
				try {
					defendingTerritoryIndex = Integer.parseInt(defendingTerritoryInput);
					tempTerritory = territories.get(defendingTerritoryIndex);
				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
					tempTerritory = chooseTerritoryToAttack(attackingTerritory);
					undo = false;
				} catch(Exception e) {
					// not a territory index
					System.out.println("Error: " + e + "\nTry again");
					tempTerritory = chooseTerritoryToAttack(attackingTerritory);
					undo = false;
				}
				if(tempTerritory.getPlayer() == players.get(currentPlayerIndex)) {
					System.out.println("You cannot attack your own territory. Try again");
					tempTerritory = chooseTerritoryToAttack(attackingTerritory);
					undo = false;
				}
				// search opposingAdjacents for territories.get(defendingTerritoryIndex); ensure it is adjacent
				if(!opposingAdjacents.contains(tempTerritory)) {
					System.out.println("That territory is not adjacent to the attacking territory you selected.\nTry again");
					tempTerritory = chooseTerritoryToAttack(attackingTerritory);
					undo = false;
				}
		}
		
		// add option to cancel and go back to step before (choosing attacking territory).
		return tempTerritory;
	}
	
	/**
	 * Sorts an ArrayList of Dice in descending order
	 * For use when comparing the dice during an attack
	 * @param dice An ArrayList of Dice objects
	 * @return Sorted ArrayList
	 */
	public ArrayList<Dice> sortDice(ArrayList<Dice> dice) {
		if(dice.size() < 2)
			return dice;
		for(int i = 0; i < dice.size() - 1; i++) {
			for(int j = 0; j < dice.size() - i - 1; j++) {
				if(dice.get(j).getCurrentValue() < dice.get(j + 1).getCurrentValue()) {
					Dice temp = dice.get(j);
					dice.set(j, dice.get(j + 1));
					dice.set(j + 1, temp);
				}
			}
		}
		return dice;
	}
	
	/**
	 * Moves armies from one territory to another
	 * @param fromTerritory The territory to move armies from
	 * @param toTerritory The territory to move armies to
	 * @param armies The number of armies to move
	 */
	// TODO: Validation
	private void moveArmies(Territory fromTerritory, Territory toTerritory, int armies) {
		System.out.println("Moving " + armies + " armies from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName());
		// check player has both territories
		// check for adjacency
		// check that there's enough armies to move and still leave at least 1
		fromTerritory.decrementArmy(armies);
		toTerritory.incrementArmy(armies);
	}
	
	/**
	 * The attack step for the current player
	 * @param curAttack Attack object that holds a flag indicating if the Player receives a Risk card at the end of the attack step
	 * 	AND if the player chooses to continue the current attack
	 * @param attackingTerritory selected territory to attack from
	 * @param defendingTerritory selected territory to attack
	 * @return current Attack object
	 */
	private Attack attack(Attack curAttack, Territory attackingTerritory, Territory defendingTerritory) {
		System.out.println("\n" + attackingTerritory.getPlayer().getName() + " is attacking " + defendingTerritory.getPlayer().getName());
		System.out.println(attackingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ") vs "+ defendingTerritory.getTerritoryName() + " ("  + defendingTerritory.getArmyCount() + ")");
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
			System.out.println(attackingTerritory.getPlayer().getName() + " has " + attackingTerritory.getArmyCount() + " armies on " + attackingTerritory.getTerritoryName() + ", rolls " + attackingDiceTotal + " die/dice.");
			System.out.println(defendingTerritory.getPlayer().getName() + " has " + defendingTerritory.getArmyCount() + " armies on " + defendingTerritory.getTerritoryName() + ", rolls " + defendingDiceTotal + " die/dice.");
			
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
			sortDice(attackingDice);
			for(int i = 0; i < attackingDiceTotal; i++) {
				System.out.print(attackingDice.get(i).getCurrentValue() + " ");
			}
			System.out.println();
			sortDice(defendingDice);
			for(int i = 0; i < defendingDiceTotal; i++) {
				System.out.print(defendingDice.get(i).getCurrentValue() + " ");
			}
			System.out.println();
			
			// Find the minimum of number of dice rolled between the two players
			// (it must either be 1 or 2), then compare each of the 1 or 2 dice
			// to the opposing player's dice.
			int minDiceTotal = 0;
			if(defendingDiceTotal <= attackingDiceTotal) {
				minDiceTotal = defendingDiceTotal;
			} else {
				minDiceTotal = attackingDiceTotal;
			}
			
			// Compare the dice rolls
			for(int i = 0; i < minDiceTotal; i++) {
				if(defendingDice.get(i).getCurrentValue() < attackingDice.get(i).getCurrentValue()) {
					// If the defender's die is less than the attacker's, 
					// the defender loses an army
					System.out.println(defendingTerritory.getPlayer().getName() + " loses 1 army from " + defendingTerritory.getTerritoryName());
					defendingTerritory.decrementArmy(1);
				} else {
					// Else If the defender's die is greater than or equal to the attacker's,
					// the attacker loses an army
					System.out.println(attackingTerritory.getPlayer().getName() + " loses 1 army from " + attackingTerritory.getTerritoryName());
					attackingTerritory.decrementArmy(1);
				}
			}
			
			// Repeat until:
			// A) The attacking player has wiped out the defending players armies on their territory.
			if(defendingTerritory.getArmyCount() == 0) {
				if(!curAttack.receiveCard)
					curAttack.receiveCard = true;
				System.out.println(attackingTerritory.getPlayer().getName() + " has conquered " + defendingTerritory.getTerritoryName());
				// change ownership of territory
				defendingTerritory.setPlayer(attackingTerritory.getPlayer());
				
				// TODO: Check if the defending player has more than 0 remaining territories. If not, remove player from players ArrayList
				// TODO: If the defending player has lost, then check if there are more than 1 remaining players in the players ArrayList.
				//			If not, the game is over.
				
				// prompt attacking player to move at least 1 troop into conquered territory
				int armiesToMove = 0;
				boolean tryAgain = true;
				while(tryAgain) {
					try {
						String armiesToMoveStr = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", select between 1 and " + (attackingTerritory.getArmyCount() - 1) + " armies to move from " + attackingTerritory.getTerritoryName() + " to " + defendingTerritory.getTerritoryName());
						armiesToMove = Integer.parseInt(armiesToMoveStr);
						tryAgain = false;
					} catch(NumberFormatException e) {
						// not an int
						System.out.println("Could not parse number. Try again");
					}
					if(armiesToMove > attackingTerritory.getArmyCount() - 1) {
						tryAgain = true;
						System.out.println("You don't have enough troops.");
					}
					if(armiesToMove < 1) {
						tryAgain = true;
						System.out.println("Pick a number between 1 and " + (attackingTerritory.getArmyCount() - 1));
					}
				}
				// prompt user to choose how many armies to move into the
				// newly acquired territory from the attacking territory.
				moveArmies(attackingTerritory, defendingTerritory, armiesToMove);
			}
			
			// B) The attacking player has only 1 remaining army on their territory
			if(attackingTerritory.getArmyCount() == 1) {
				// attacking player can no longer attack from this territory
				System.out.println(attackingTerritory.getPlayer().getName() + " no longer has enough armies to attack from " + attackingTerritory.getTerritoryName());
				break;
			} else {
				// if attacking player has more armies, 
				// ask if they want to attack again from current territory
				String[] values = {"Yes", "No"};
				Object selected = JOptionPane.showInputDialog(null, "Continue attacking from " + attackingTerritory.getTerritoryName() + "?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
				if ( selected != null ) {//null if the user cancels. 
				    String selectedString = selected.toString();
					if(selectedString == "No") {
						continueAttacking = false;
					}
				} else {
				    System.out.println("User cancelled");
				    continueAttacking = false;
				}
			}
			// C) the attacking player retreats

		}
		String[] values = {"Yes", "No"};
		Object selected = JOptionPane.showInputDialog(null, "Choose a different territory to attack from?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
		if ( selected != null ) {
			//null if the user cancels. 
		    String selectedString = selected.toString();
			if(selectedString == "No") {
				curAttack.continueAttack = false;
			}
		} else {
		    System.out.println("User cancelled");
		    curAttack.continueAttack = false;
		}
		// Prompt the player either attack another territory
		// or end the attack phase of their turn
		return curAttack;
	}
	
	/**
	 * Fortification step during the current player's turn
	 */
	// Currently, this step only allows the player to move armies from one territory to another that is immediately adjacent to it
	// I believe the rules of Risk allow the player to move armies across multiple territories as long as they're all connected
	// TODO: We need to include a method that traverses all of the player's connected territories rather than just the immediately adjacent ones.
	public void fortify() {
		// Prompt player to pick a territory to move armies from		
		ArrayList<Territory> playersTerritories = getPlayersTerritories(players.get(currentPlayerIndex));
		for(int i = 0; i < playersTerritories.size(); i++) {
			System.out.print("[" + territories.indexOf(playersTerritories.get(i)) + "]" + playersTerritories.get(i).getTerritoryName() + ": " + playersTerritories.get(i).getArmyCount() + " armies ->{");
			ArrayList<Territory> tempAdj = playersTerritories.get(i).getAdjacentTerritories(false, true, false);
			for(int j = 0; j < tempAdj.size(); j++) {
				System.out.print("[" + territories.indexOf(tempAdj.get(j)) + "]" + tempAdj.get(j).getTerritoryName() + " (" + tempAdj.get(j).getArmyCount() + " armies), ");
			}
			System.out.println("}");
			
		}
		
		int fromTerritoryIndex = -1;
		Territory fromTerritory = new Territory();
		boolean tryAgain = true;
		while(tryAgain) {
			boolean undo = true;
			while(undo){
			try {
				String fromTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose a territory to send armies FROM.");
				int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to send armies from " + territories.get(Integer.parseInt(fromTerritoryInput)).getTerritoryName(), 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
				if (n == JOptionPane.YES_OPTION) {
			            undo = false;
			        } else if (n == JOptionPane.NO_OPTION) {
			            undo = true;
			        }
				 if(fromTerritoryInput == null) {
					return;
				}
				fromTerritoryIndex = Integer.parseInt(fromTerritoryInput);
				fromTerritory = territories.get(fromTerritoryIndex);
				tryAgain = false;
			} catch(NumberFormatException e) {
				// not an int
				System.out.println("Could not parse number. Try again");
				tryAgain = true;
			} catch(Exception e) {
				// not a territory index
				System.out.println("Error: " + e + "\nTry again");
				tryAgain = true;
			}
		}
		}
		int toTerritoryIndex = -1;
		tryAgain = true;
		Territory toTerritory = new Territory();
		while(tryAgain) {
			boolean undo = true;
			while(undo){
			try {
				String toTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose a territory to send armies TO.");
				int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to send armies to " + territories.get(Integer.parseInt(toTerritoryInput)).getTerritoryName(), 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
				if (n == JOptionPane.YES_OPTION) {
			            undo = false;
			        } else if (n == JOptionPane.NO_OPTION) {
			            undo = true;
			        }
				if(toTerritoryInput == null) {
					return;
				}
				toTerritoryIndex = Integer.parseInt(toTerritoryInput);
				toTerritory = territories.get(toTerritoryIndex);
				System.out.println("toTerritoryIndex: " + toTerritoryIndex + ", " + toTerritory.getTerritoryName());
				tryAgain = false;
			} catch(NumberFormatException e) {
				// not an int
				System.out.println("Could not parse number. Try again");
				tryAgain = true;
			} catch(Exception e) {
				// not a territory index
				System.out.println("Error: " + e + "\nTry again");
				tryAgain = true;
			}		
		}
	}
		
		int armiesToMove = 0;
		tryAgain = true;
		while(tryAgain) {
			boolean undo = true;
			while(undo){
				try {
					String armiesToMoveStr = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", select between 1 and " + (fromTerritory.getArmyCount() - 1) + " armies to move from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName());
					int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to move " + Integer.parseInt(armiesToMoveStr) + " from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName(), 
					        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
					        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
					if (n == JOptionPane.YES_OPTION) {
				            undo = false;
				        } else if (n == JOptionPane.NO_OPTION) {
				            undo = true;
				        }
					if(armiesToMoveStr == null) {
						return;
					}
					armiesToMove = Integer.parseInt(armiesToMoveStr);
					tryAgain = false;
				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
				}
				if(armiesToMove > fromTerritory.getArmyCount() - 1) {
					tryAgain = true;
					System.out.println("You don't have enough troops.");
				}
				if(armiesToMove < 1) {
					return;
				}
			}
		}
		moveArmies(fromTerritory, toTerritory, armiesToMove);
		
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
	
	private ArrayList<ArrayList<Card>> wildSubsets(ArrayList<Card> organizedCards) {
		ArrayList<ArrayList<Card>> subsets = new ArrayList<ArrayList<Card>>();
		Card[] data = new Card[2];
		getSubsetsUtil(organizedCards, organizedCards.size(), 2, 0, data, 0, subsets);
		return subsets;
	}
	
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
	
	private int turnInCardSet(Player player, ArrayList<Card> cardSet) {
		// check set
		
		// remove cards from player's current stack of cards
		for(int i = 0; i < 3; i++) {
			player.cards.remove(cardSet.get(i));
		}
		
		// increment card sets turned in count
		cardSetsTurnedIn++;
		
		// determine bonus armies
		if(cardSetsTurnedIn == 1) {
			return 4;
		} else if(cardSetsTurnedIn == 2) {
			return 6;
		} else if(cardSetsTurnedIn == 3) {
			return 8;
		} else if(cardSetsTurnedIn == 4) {
			return 10;
		} else if(cardSetsTurnedIn == 5) {
			return 12;
		} else if(cardSetsTurnedIn == 6) {
			return 15;
		} else if(cardSetsTurnedIn == 7) {
			return 20;
		} else if(cardSetsTurnedIn > 7) {
			return 20 + ((cardSetsTurnedIn - 7) * 5);
		} else {
			System.out.println("Error determining card sets turned in.");
			return 0;
		}
	}
	
	public boolean currentPlayerTurn() {
		boolean continueGame = true;
		//	Each turn consists of three parts:
		//	-- 1. Placing new troops
		//	-- 2. Attacking
		//	-- 3. Fortifying
		Player currentPlayer = players.get(currentPlayerIndex);
		System.out.println("It is " + currentPlayer.getName() + "'s turn.");
		// 1. Placing new troops
		
		// Check if a set of Risk cards can be turned in
		System.out.println(currentPlayer.getName() + "'s Risk Cards:");
		for(int i = 0; i < currentPlayer.cards.size(); i++) {
			System.out.println("\t" + currentPlayer.cards.get(i).getCardTypeName() + " (" + currentPlayer.cards.get(i).getTerritoryName() + ")");
		}
		int infantryCount = 0;
		int cavalryCount = 0;
		int artilleryCount = 0;
		int wildCount = 0;
		for(int i = 0; i < currentPlayer.cards.size(); i++) {
			if(currentPlayer.cards.get(i).getCardTypeName() == "Infantry") {
				infantryCount++;
			}
			else if(currentPlayer.cards.get(i).getCardTypeName() == "Cavalry") {
				cavalryCount++;
			}
			else if(currentPlayer.cards.get(i).getCardTypeName() == "Artillery") {
				artilleryCount++;
			}
			else if(currentPlayer.cards.get(i).getCardTypeName() == "Wild") {
				wildCount++;
			}
		}
		boolean threeOfAKind = infantryCount > 2 || cavalryCount > 2 || artilleryCount > 2;
		boolean oneOfEach = infantryCount > 0 && cavalryCount > 0 && artilleryCount > 0;
		boolean wildCardSet = wildCount > 0 && currentPlayer.cards.size() > 2;
		if(threeOfAKind || oneOfEach || wildCardSet) {
			System.out.println("You have a set of Risk cards you can turn in.");
			// if player's card count is greater than 4, must turn cards in.
			boolean mustTurnIn = false;
			if(currentPlayer.cards.size() > 4) {
				System.out.println("You have 5 or more cards. You MUST turn a set of cards in.");
				mustTurnIn = true;
			}
			ArrayList<ArrayList<Card>> CardSets = new ArrayList<ArrayList<Card>>();
			if(threeOfAKind) {
				ArrayList<ArrayList<Card>> tempSets = threeOfAKindExtractor(currentPlayer.cards, infantryCount, cavalryCount, artilleryCount);
				for(int i = 0; i < tempSets.size(); i++) {
					CardSets.add(tempSets.get(i));
				}
			}
			if(oneOfEach) {
				// add each possible set to CardSet
				ArrayList<ArrayList<Card>> tempSets = oneOfEachExtractor(currentPlayer.cards, infantryCount, cavalryCount, artilleryCount);
				for(int i = 0; i < tempSets.size(); i++) {
					CardSets.add(tempSets.get(i));
				}
			}
			if(wildCardSet) {
				// add each possible set to CardSet
				ArrayList<ArrayList<Card>> tempSets = wildSetsExtractor(currentPlayer.cards, wildCount);
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
					boolean undo = true;
					if(mustTurnIn) {
						while(trySetAgain){
							while(undo){
								cardSetInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", select a set of cards to turn in. (You must select a set)");   
								int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen set " + cardSetInput, 
									        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
									        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
								 if (n == JOptionPane.YES_OPTION) {
							            undo = false;
							        } else if (n == JOptionPane.NO_OPTION) {
							            undo = true;
							        }
							}
							if(cardSetInput.equals("0") || cardSetInput.equals("1") || cardSetInput.equals("2") || cardSetInput.equals("3")|| cardSetInput.equals("4") || cardSetInput.equals("5") || cardSetInput.equals("6")|| cardSetInput.equals("7")){
								trySetAgain = false;
							}
							else{
								JOptionPane.showMessageDialog(null, "You have entered an invalid set. Please try again", "Inane error", JOptionPane.ERROR_MESSAGE);
								undo = true;
							}
						}
						if(cardSetInput != null) {
							cardSetIndex = Integer.parseInt(cardSetInput);
						}
					} else {
						cardSetInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", select a set of cards to turn in. Click cancel to wait.");
						if(cardSetInput == null) {
							tryAgain = false;
							break;
						}
						cardSetIndex = Integer.parseInt(cardSetInput);
					}
					
					tryAgain = false;
					int bonusArmies = turnInCardSet(players.get(currentPlayerIndex), CardSets.get(cardSetIndex));
					System.out.println(players.get(currentPlayerIndex).getName() + " turned in a set of Risk Cards and was awarded " + bonusArmies + " armies.");
					currentPlayer.increaseArmies(bonusArmies);
					
				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
				}
			}
			
		}

		
		
		
		// Determine total armies received by board
		currentPlayer.increaseArmies(armyReplenishment());
		
		// Prompt player to select a territory
		while(currentPlayer.armies > 0) {
			printTerritories(false, true);
			int ti = pickTerritory(false);
			Territory tempTerritory = territories.get(ti);
			// Prompt player to place at least 1 army on selected territory
			int armies = placeArmies();
			tempTerritory.incrementArmy(armies);
			currentPlayer.decreaseArmies(armies);
			// Repeat until no armies remaining for player.
		}
		printTerritories(false, true);
		//boolean continueAttack = true;
		Attack currentAttack = new Attack(currentPlayer);
		while(currentAttack.continueAttack) {
			// TODO: Add option to end attack step during territory choosing.
			// TODO: Give player risk card at end of turn if they have conquered at least
			// 		one opposing player's territory.
			
			// 2. Attacking
			// Prompt player to choose a territory to attack from
			Territory attackingTerritory = chooseAttackingTerritory();
			System.out.println("Attacking from " + attackingTerritory.getTerritoryName());
			// Prompt player to choose a territory to attack
			Territory defendingTerritory = chooseTerritoryToAttack(attackingTerritory);
			System.out.println("Defending from " + defendingTerritory.getTerritoryName());
			// Continue until player decides to end attack phase
			currentAttack = attack(currentAttack, attackingTerritory, defendingTerritory);
		}
		if(currentAttack.receiveCard) {
			currentAttack.attackingPlayer.addCard(drawCard());
			System.out.println(currentAttack.attackingPlayer.getName() + " has received one Risk card");
		}
		
		// 3. Fortifying
		// select territory to move armies from
		// select territory to move armies to
		
		fortify();
		
		incrementCurrentPlayerIndex();
		
		// Check if one player controls all the territories
		// if so, continueGame = false
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
