package main.java.game;
import java.util.*;

import javax.swing.JOptionPane;

public class Board {
	//TODO: Make class a singleton
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
	
	/**
	 * Creates the initial card deck by taking each territory created
	 * and applying it a value of 1-3 representing the card type {Infantry, Cavalry, Artillery}
	 * Then two wild cards are added to the deck, and the deck is then shuffled 
	 * @return
	 */
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
	
	/**
	 * "Draws" a Risk Card by taking the card in the first position
	 * of the cards list.
	 * @return Card drawn from cards list
	 */
	public Card drawCard() {
		// When a player has conquered at least one territory during their turn,
		// they will draw a risk card.
		Card tempCard = cards.get(0);
		cards.remove(0);
		return tempCard;
	}
	
	/*
	public int remainingCards() {
		return cards.size();
	}
	*/
	
	//TODO: refactor this code before testing
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
	 * Accepts an array of Player objects, rolls the first die to determine who goes first,
	 * and converts the array into an ArrayList for the attribute players.
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
		// TODO: tiebreaker? increase sides of die so there is less of a chance of a tie?
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
	 * Prompts each player to place one army until all initial armies have been placed
	 * Until all territories are occupied, the current player must choose an unoccupied territory
	 * 
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
				ti = pickTerritory(true, players.get(currentPlayerIndex));
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
	private int pickTerritory(boolean initialTurns, Player player) {
		int ti = player.pickTerritory(initialTurns);
		// TODO: change these to a try/catch block. Throw proper exceptions
		if(ti > territories.size() - 1) {
			System.out.println("Out of range. Try again");
			ti = pickTerritory(initialTurns, player);
		}
		if(unoccupiedTerritoriesCount() > 0) {
			if(territories.get(ti).isOccupied()) {
				System.out.println("Territory already occupied. Try again");
				ti = pickTerritory(initialTurns, player);
			}
		} else {
			if(territories.get(ti).getPlayer() != player) {
				System.out.println("You do not control this territory. Try again");
				ti = pickTerritory(initialTurns, player);
			}
		}
		return ti;
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
	public int playerTerritoriesCount(Player player) {
		int count = 0;
		for(int i = 0; i < territories.size(); i++) {
			if(territories.get(i).getPlayer() == player) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Calculates how many armies are to be given to the player at the beginning of the turn
	 * @return number of armies gained
	 */
	public int armyReplenishment(Player player) {
		// Army replenishment at beginning of player's turn.
		//Player curPlayer = players.get(currentPlayerIndex);
		int armies = 0;
		int territoryCount = playerTerritoriesCount(player);
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
	public ArrayList<Territory> getPlayersTerritories(Player player) {
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
		Player currentPlayer = players.get(currentPlayerIndex);
		Territory tempTerritory = currentPlayer.chooseAttackingTerritory(getPlayersTerritories(currentPlayer), territories);
		return tempTerritory;
	}
	
	//TODO: Move to Player class
	/**
	 * Prompts player to choose a territory to attack.
	 * For use when the player is in the attack step of their turn.
	 * @param attackingTerritory Territory chosen prior to attack from
	 * @return Territory selected to be attacked
	 */
	private Territory chooseTerritoryToAttack(Territory attackingTerritory) {
		Territory tempTerritory = players.get(currentPlayerIndex).chooseTerritoryToAttack(attackingTerritory, territories);
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
				// change ownership of territory
				Player tempPlayer = defendingTerritory.getPlayer();
				defendingTerritory.setPlayer(attackingTerritory.getPlayer());
				
				System.out.println(attackingTerritory.getPlayer().getName() + " has conquered " + defendingTerritory.getTerritoryName());

				// Check if the defending player has more than 0 remaining territories. 
				// If not, remove player from players ArrayList
				if(playerTerritoriesCount(tempPlayer) < 1) {
					players.remove(tempPlayer);
					// TODO: If the defending player has lost, then check if there are more than 1 remaining players in the players ArrayList.
					//			If not, the game is over.
					if(players.size() < 2) {
						// game should end somehow
					}		
				}
				
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
				moveArmies(attackingTerritory, defendingTerritory, armiesToMove);
				continueAttacking = false;
			}
			// B) The attacking player has only 1 remaining army on their territory
			else if(attackingTerritory.getArmyCount() == 1) {
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
			try {
				String fromTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose a territory to send armies FROM.");
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
		int toTerritoryIndex = -1;
		tryAgain = true;
		Territory toTerritory = new Territory();
		while(tryAgain) {
			try {
				String toTerritoryInput = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", choose a territory to send armies TO.");
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
		
		
		int armiesToMove = 0;
		tryAgain = true;
		while(tryAgain) {
			try {
				String armiesToMoveStr = JOptionPane.showInputDialog(players.get(currentPlayerIndex).getName() + ", select between 1 and " + (fromTerritory.getArmyCount() - 1) + " armies to move from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName());
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
		moveArmies(fromTerritory, toTerritory, armiesToMove);
		
	}
	

	/**
	 * 
	 * @param player
	 * @param cardSet
	 * @return
	 */
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
	
	/**
	 * Executes current player's turn
	 * 
	 * Each turn consists of three parts:
	 * -- 1. Placing new troops
	 * -- 2. Attacking
	 * -- 3. Fortifying
	 * 
	 * @return
	 */
	public boolean currentPlayerTurn() {
		boolean continueGame = true;
		Player currentPlayer = players.get(currentPlayerIndex);
		System.out.println("It is " + currentPlayer.getName() + "'s turn.");
		
		// 1. Placing new troops
		
		//TODO: Create a method for this in the Player class
		// Check if a set of Risk cards can be turned in
		System.out.println(currentPlayer.getName() + "'s Risk Cards:");
		ArrayList<Card> cardsTurnedIn = currentPlayer.cardCheck();
		if(cardsTurnedIn != null) {
			int bonusArmies = turnInCardSet(currentPlayer, cardsTurnedIn);
			System.out.println(currentPlayer.getName() + " turned in a set of Risk Cards and was awarded " + bonusArmies + " armies.");
			currentPlayer.increaseArmies(bonusArmies);
		}
	
		
		// Determine total armies received by board
		currentPlayer.increaseArmies(armyReplenishment(currentPlayer));
		
		// Prompt player to select a territory
		while(currentPlayer.armies > 0) {
			printTerritories(false, true);
			int ti = pickTerritory(false, currentPlayer);
			Territory tempTerritory = territories.get(ti);
			// Prompt player to place at least 1 army on selected territory
			int armies = currentPlayer.chooseArmiesQty();
			tempTerritory.incrementArmy(armies);
			currentPlayer.decreaseArmies(armies);
			// Repeat until no armies remaining for player.
		}
		printTerritories(false, true);
		
		// 2. Attacking
		Attack currentAttack = new Attack(currentPlayer);
		while(currentAttack.continueAttack) {
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
		fortify();
		
		// Change currentPlayerIndex to next player
		incrementCurrentPlayerIndex();
		
		// Check if one player controls all the territories
		// if so, continueGame = false
		return continueGame;
	}
	
	/**
	 * Creates the board's graph structure from the supplied list of territories.
	 */
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
