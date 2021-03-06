package main.java.game;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

//import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import java.util.Timer;
import java.util.TimerTask;

import java.io.*;

public class Board implements Observer{
	//TODO: Make class a singleton
	public ArrayList<Territory> territories = new ArrayList<Territory>();
	public ArrayList<Continent> continents = new ArrayList<Continent>();
	private ArrayList<Card> cards = new ArrayList<Card>();
	public int cardSetsTurnedIn;
	public ArrayList<Player> players = new ArrayList<Player>();
	public int currentPlayerIndex;
	public int initialArmies;
	public S3 s3 = null;
	private BufferedReader br = null;
	public boolean useAPIs = false;
	public boolean consoleOnly = false;
	public String attackMessage;
	public boolean playwithbot = false;
	public static long currentplayerID;
	public TelegramBot bot;
	
	private static final Board instance = new Board();
	
	public Board() {
		generateGraph();
		this.cards = createCardDeck();
		this.cardSetsTurnedIn = 0;
	}
	
	public Board(boolean useAPIs, boolean consoleOnly) {
		generateGraph();
		this.cards = createCardDeck();
		this.cardSetsTurnedIn = 0;
		this.useAPIs = useAPIs;
		this.consoleOnly = consoleOnly;
		if(this.useAPIs == true) {
			s3 = new S3(true);
		}
		if(this.consoleOnly == true) {
    		InputStreamReader isr = new InputStreamReader(System.in);
    		this.br = new BufferedReader(isr);
    		
		}
	}
	
	public static Board getInstance() {
		return instance;
	}
	
	public void setAPITrue() {
		this.useAPIs = true;
	}
	
	public void setConsoleOnlyTrue() {
		this.consoleOnly = true;
	}
	
	/**
	 * A player has 30 seconds to decide their next action. If they fail to decide, they game will move to the next player.
	 * 
	 */
    class TaskTimerStep extends TimerTask {
    	public boolean hasStarted = false;
   		public void run() {
   			this.hasStarted = true;
   			if(playwithbot == true) {
   				// move on to next step?
   			}
			//JOptionPane.showMessageDialog(null, "You have failed to enter anything. Your turn is forfeited.", "Warning", JOptionPane.WARNING_MESSAGE);
   			System.out.println("Timer expired! Default action taken.");
		}
    }
    /**
     * 
     * @param inputMessage
     */
    public void timedAcknowledgement(String inputMessage) {
    	System.out.println(inputMessage);
    }
    /**
     * 
     * @param inputMessage
     * @return response
     * @throws Exception
     */
    public String timedPrompt(String inputMessage) throws Exception{
    	String userInput = null;
        Timer timer = new Timer();
        TaskTimerStep tts = new TaskTimerStep();
        timer.schedule( tts, 30*1000 );
    	if(consoleOnly) {
    		System.out.print(inputMessage + ": ");
    		try {
    			while(!br.ready()) {
    				if(tts.hasStarted) {
    					throw new Exception("Timer ended");
    				}
    			}
    			userInput = br.readLine();
    			System.out.println("Selected: " + userInput);
    		} catch(Exception e) {
    			//e.printStackTrace();
    		}
    	}
        timer.cancel();
        return userInput;
    }
    /**
     * Prompts the user for a response given a set of values to choose from
     * @param inputMessage
     * @param values
     * @return selected value (string but as an object class)
     * @throws Exception
     */
    public Object timedSelectionPrompt(String inputMessage, String[] values) throws Exception {
    	Timer timer = new Timer();
    	Object selected = null;
    	TaskTimerStep tts = new TaskTimerStep();
    	timer.schedule(tts, 30 * 1000);
		System.out.println(inputMessage);
		for(int i = 0; i < values.length; i++) {
			System.out.println("[" + i + "]" + values[i]);
		}
		System.out.print("Select 0 - " + (values.length + 1) + ": ");
		try {
			if(playwithbot == true) {
				
			} else {
			while(!br.ready()) {
				if(tts.hasStarted) {
					throw new Exception("Timer ended");
				}
			}
			int valIndex = Integer.parseInt(br.readLine());
			selected = values[valIndex];
			}
		} catch(Exception e) {
			//e.printStackTrace();
		}
    	
		timer.cancel();
    	return selected;
    }
    /**
     * Prompts user to choose between 2 values
     * @param inputMessage
     * @param instruction
     * @param values
     * @return selected index
     * @throws Exception
     */
	public int timedButtonPrompt(String inputMessage, String instruction, String[] values) throws Exception {
		Timer timer = new Timer();
		int option = -1;
		TaskTimerStep tts = new TaskTimerStep();
		timer.schedule(tts, 30 * 1000);
		if(consoleOnly) {
			//InputStreamReader isr = new InputStreamReader(System.in);
			//br = new BufferedReader(isr);
			System.out.println(inputMessage);
			System.out.println("[0]" + values[0]);
			System.out.println("[1]" + values[1]);
			System.out.print("Select 0 or 1: ");
			try {
				while(!br.ready()) {
					if(tts.hasStarted) {
						throw new Exception("Timer ended");
					}
				}
				String selection = br.readLine();
				option = Integer.parseInt(selection);
				System.out.println("Inputted: " + option);
			} catch(Exception e) {
				//e.printStackTrace();
			}
		}
		timer.cancel();
		return option;
	}
	
	public int untimedIntPrompt(String inputMessage, String instruction, int min, int max) {
		int option = -1;
		if(consoleOnly) {
			System.out.println(inputMessage);
			System.out.print("Select between " + min + " and " + max + ": ");
			try {
				String selection = br.readLine();
				option = Integer.parseInt(selection);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return option;
	}
	public String untimedStringPrompt(String inputMessage) {
		String input = null;
		if(consoleOnly) {
			System.out.print(inputMessage);
			try {
				input = br.readLine();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return input;
	}
	/**
	 * 
	 * @return attack message
	 */
	public String getBoardAttackMessage(){
		return attackMessage;
	}
	
	/**
	 * Creates the initial card deck by taking each territory created
	 * and applying it a value of 1-3 representing the card type {Infantry, Cavalry, Artillery}
	 * Then two wild cards are added to the deck, and the deck is then shuffled 
	 * @return shuffled risk card deck
	 */
	private ArrayList<Card> createCardDeck() {
		ArrayList<Card> deck = new ArrayList<Card>();
		// This automatically generates the deck of cards
		// based off the list of territory names.
		for(int i = 0; i < this.territories.size(); i++) {
			byte type = (byte)(i % 3 + 1);
			Card tempCard = new Card(this.territories.get(i).name, type);
			deck.add(tempCard);
			//System.out.println("Card created[" + i + "]: " + tempCard.territoryName + ", " + tempCard.getCardTypeName());
		}
		for(int i = 0; i < 2; i++) {
			// wild cards
			deck.add(new Card("Wild", (byte)4));
			//System.out.println("Card created: Wild card");
		}
		// shuffles risk card deck
		//System.out.println("Total cards: " + deck.size() + "\n");
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
	

	public int remainingCards() {
		return cards.size();
	}

	
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
	
	public void botprintTerritories(boolean onlyUnoccupied, boolean showAdjacent) {
		TelegramBot bot = new TelegramBot();
		if(onlyUnoccupied == true) {
			System.out.println("Available territories:");
			String message = "Available territories: \n";
			//bot.sendplayer("Available territories:");
			for(int i = 0; i < this.territories.size(); i++) {
				if(!this.territories.get(i).isOccupied()) {
					System.out.print("[" + i + "]" + this.territories.get(i).name + ", ");
					//bot.sendplayer("[" + i + "]" + this.territories.get(i).name + ", ");
					message += "[" + i + "]" + this.territories.get(i).name + ", ";
				}
			}
			System.out.println("");
			message += "\n";
			bot.sendplayer(message);
		} else {
			String message = "";
			for(int i = 0; i < this.territories.size(); i++) {
				String playerName = ""; 
				if(territories.get(i).isOccupied()) {
					playerName = territories.get(i).getPlayer().getName();
				} else {
					playerName = "UNOCCUPIED";
				}
				//bot.sendplayer("[" + i + "]" + territories.get(i).getContinent() + ", " + territories.get(i).name + "(" + playerName +  ", " + territories.get(i).getArmyCount() + " armies)");
				System.out.print("[" + i + "]" + territories.get(i).getContinent() + ", " + territories.get(i).name + "(" + playerName +  ", " + territories.get(i).getArmyCount() + " armies)");
				message += "[" + i + "]" + territories.get(i).name + "(" + playerName +  ", " + territories.get(i).getArmyCount() + " armies)";
				if(showAdjacent) {
					//bot.sendplayer("->{ ");
					message += "->{ ";
					System.out.print("->{ ");
					ArrayList<Territory> adjs = territories.get(i).getAdjacentTerritories();
					for(int j = 0; j < adjs.size(); j++) {
						//bot.sendplayer(adjs.get(j).name + ", ");
						message += adjs.get(j).name + ", ";
						System.out.print(adjs.get(j).name + ", ");
					}
					//bot.sendplayer("}");
					message += "}\n";
					System.out.print("}");
				}
				System.out.println("");		
			}
			bot.sendplayer(message);
		}
	}
	
	
	/**
	 * Returns the initial armies to be received by the players. 
	 * See Risk rules for details.
	 * @param numOfPlayers
	 * @return initial army count
	 */
	public int initalArmyDispursement(int numOfPlayers) {
		int armies = 0;
		switch(numOfPlayers)
		{
			case 2:
				armies = 40;
				break;
			case 3:
				armies = 35;
				break;
			case 4:
				armies = 30;
				break;
			case 5 :
				armies = 25;
				break;
			case 6: 
				armies = 20;
				break;
		}
		return armies;
	}
	
	public boolean addPlayer(String name, String color) {
		Player tempPlayer = new Player(name, color, 25, 0);
		players.add(tempPlayer);
		System.out.println("Player added!");
		return true;
		
	}
	public boolean addPlayer(String name, String color, Long chatID) {
		Player tempPlayer = new Player(name, color, 25, 0, chatID);
		players.add(tempPlayer);
		System.out.println("Player added!");
		return true;
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
		System.out.println(players[0].getName() + " rolled a " + initRolls[0]);
		for(int i = 1; i < numOfPlayers; i++) {
			initRolls[i] = d.getDiceValue();
			System.out.println(players[i].getName() + " rolled a " + initRolls[i]);
			if(initRolls[maxIndex] < initRolls[i])
				maxIndex = i;
		}
		System.out.println(players[maxIndex].getName() + " goes first.");
		
		// Determine the number of initial armies to place by the number of players
		this.initialArmies = initalArmyDispursement(numOfPlayers);
		
		currentPlayerIndex = maxIndex;
		// Converting array parameter Players into an ArrayList
		this.players = new ArrayList<Player>(Arrays.asList(players));
		for(int i = 0; i < numOfPlayers; i++) {
			System.out.println(" The order of players to play is " + players[i].getName());
		}
		if(this.useAPIs == true) {
			s3.pa.startGame(this.players);
			s3.logPlayerActivity();
		}
		// CARD PILOT -- just checking that the cards are working
		/*
		for(int i = 0; i < numOfPlayers; i++) {
			for(int j = 0; j < 5; j++) {
				this.players.get(i).addCard(drawCard());
			}
		}
		*/
	}
	public void startGame() {
		System.out.println("Number of players: " + this.players.size());
		int numOfPlayers = this.players.size();
		// Roll dice for each player to determine who goes first
		Dice d = new Dice();
		int initRolls[] = new int[numOfPlayers];
		int maxIndex = 0;
		initRolls[0] = d.getDiceValue();
		// Sorting and displaying results
		// TODO: tiebreaker? increase sides of die so there is less of a chance of a tie?
		// TODO: Refactor this to a comparator class for the dice.
		System.out.println(players.get(0).getName() + " rolled a " + initRolls[0]);
		for(int i = 1; i < numOfPlayers; i++) {
			initRolls[i] = d.getDiceValue();
			System.out.println(players.get(i).getName() + " rolled a " + initRolls[i]);
			if(initRolls[maxIndex] < initRolls[i])
				maxIndex = i;
		}
		this.initialArmies = initalArmyDispursement(numOfPlayers);
		
		currentPlayerIndex = maxIndex;
		//currentplayerID=players.get(currentPlayerIndex).getChatID();
		//bot.sendplayer(players.get(maxIndex).getName() + " goes first.");
		System.out.println(players.get(maxIndex).getName() + " goes first.");
		
		for(int i = 0; i < numOfPlayers; i++) {
			System.out.println(" The order of players to play is " + this.players.get(i).getName());
		}
		if(this.useAPIs == true) {
			s3.pa.startGame(this.players);
			s3.logPlayerActivity();
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
			if(this.useAPIs == true) {
				s3.pa.receiveArmies(players.get(i), initialArmies);
				s3.logPlayerActivity();
			}
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
				// Automatically select a territory for the player, done in a way that keeps the players territories grouped together
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
			if(this.useAPIs == true) {
				s3.pa.placeArmies(players.get(currentPlayerIndex), tempTerritory, 1);
				s3.logPlayerActivity();
			}
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
	public int pickTerritory(boolean initialTurns, Player player) {
		// timedPrompt gets called within the player pickTerritory method
		boolean undo = true;
		boolean checkInput = true;
		int ti = -1;
		while(undo) {
			ti = player.pickTerritory(initialTurns, this);
			// TODO: change these to a try/catch block. Throw proper exceptions
			if(ti > territories.size() - 1) {
				System.out.println("Out of range. Try again");
				ti = pickTerritory(initialTurns, player);
			}
			if(unoccupiedTerritoriesCount() > 0 && ti != -1) {
				if(territories.get(ti).isOccupied()) {
					System.out.println("Territory already occupied. Try again");
					ti = pickTerritory(initialTurns, player);
				}
			} else if(ti > -1) {
				if(territories.get(ti).getPlayer() != player) {
					System.out.println("You do not control this territory. Try again");
					ti = pickTerritory(initialTurns, player);
				}
			}
			if(ti < 0) {
				System.out.println("Null input received, picking territory at random...");
				// null input received, automatically pick a territory
				if(unoccupiedTerritoriesCount() > 0) {
					System.out.println("Picking first unoccupied territory.");
					// pick the first unoccupied territory
					for(int i = 0; i < territories.size(); i++) {
						if(!territories.get(i).isOccupied()) {
							ti = i;
							break;
						}
					}
				} else {
					// pick one of the player's territories randomly
					System.out.println("Picking one of the player's territories randomly.");
					Random random = new Random();
					int randIndex = random.nextInt(playerTerritoriesCount(player));
					Territory randTerritory = getPlayersTerritories(player).get(randIndex);
					System.out.println(randTerritory.getTerritoryName() + " (" + randTerritory.getPlayer() + ") chosen.");
					ti = territories.indexOf(randTerritory);
					//System.out.println("ti=" + ti);
					checkInput = false;
					undo = false;
				}
				
			}
			if(checkInput == true) {
				/*
				int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen " + territories.get(ti).getTerritoryName(), 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
				*/
				String values[] = {"Continue", "Undo"};
				String confirmationMessage = "You have chosen " + territories.get(ti).getTerritoryName();
				try {
					int n = timedButtonPrompt(confirmationMessage, "Undo?", values);
					if (n == JOptionPane.NO_OPTION) {
						if(player.getCredits() > 0){
							player.useCredits(player.getCredits()-1);
						}
						else{
							//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
							String inputMessage = "You do not have enough credits to undo your action.";
							timedAcknowledgement(inputMessage);
							undo = false;
						}
			        } else if (n == JOptionPane.YES_OPTION) {
			            undo = false;
			        }
				} catch(Exception e) {
					System.out.println(e.getStackTrace());
				}
			}
		}
		return ti;
	}
	
	/**
	 * 
	 * @param initialTurns boolean flag indicating whether or not it's the initial placement step.
	 * If true, player can only place one army at a time.
	 * @return index of territory picked
	 */
	public int botpickTerritory(boolean initialTurns, Player player) {
		// timedPrompt gets called within the player pickTerritory method
		//this.botprintTerritories(false, true);
		TelegramBot bot = new TelegramBot();
		boolean undo = true;
		boolean checkInput = true;
		int ti = -1;
		while(undo) {
			this.botprintTerritories(false, true);
			ti = player.botpickTerritory(initialTurns, this);
			// TODO: change these to a try/catch block. Throw proper exceptions
			if(ti > territories.size() - 1) {
				bot.sendplayer("Out of range. Try again");
				System.out.println("Out of range. Try again");
				ti = pickTerritory(initialTurns, player);
			}
			if(unoccupiedTerritoriesCount() > 0) {
				if(territories.get(ti).isOccupied()) {
					bot.sendplayer("Territory already occupied. Try again");
					System.out.println("Territory already occupied. Try again");
					ti = pickTerritory(initialTurns, player);
				}
			} else if(ti > -1) {
				if(territories.get(ti).getPlayer() != player) {
					bot.sendplayer("You do not control this territory. Try again");
					System.out.println("You do not control this territory. Try again");
					ti = pickTerritory(initialTurns, player);
				}
			}
			if(ti < 0) {
				bot.sendplayer("Null input received, picking territory at random...");
				System.out.println("Null input received, picking territory at random...");
				// null input received, automatically pick a territory
				if(unoccupiedTerritoriesCount() > 0) {
					System.out.println("Picking first unoccupied territory.");
					// pick the first unoccupied territory
					for(int i = 0; i < territories.size(); i++) {
						if(!territories.get(i).isOccupied()) {
							ti = i;
							undo = false;
							break;
						}
					}
				} else {
					// pick one of the player's territories randomly
					System.out.println("Picking one of the player's territories randomly.");
					Random random = new Random();
					int randIndex = random.nextInt(playerTerritoriesCount(player));
					Territory randTerritory = getPlayersTerritories(player).get(randIndex);
					System.out.println(randTerritory.getTerritoryName() + " (" + randTerritory.getPlayer() + ") chosen.");
					ti = territories.indexOf(randTerritory);
					//System.out.println("ti=" + ti);
					checkInput = false;
					undo = false;
				}
				
			}
			
			if(checkInput == true) {
				String input = null;
				String confirmationMessage = "You have chosen " + territories.get(ti).getTerritoryName() + "-- yes/no";
				bot.sendplayer(confirmationMessage);
				bot.cleanmessage();
				int counter = 0;
				while(bot.getmessage()==null && counter < 30) {
					try {
						Thread.sleep(1000);
						counter++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(bot.getmessage()!=null) {
					input = bot.getmessage();
				}
				if(input.equals("no")) {
					if(player.getCredits() > 0){
						player.useCredits(player.getCredits()-1);
					}
					else{
						String inputMessage = "You do not have enough credits to undo your action.";
						bot.sendplayer("You do not have enough credits to undo your action.");
						timedAcknowledgement(inputMessage);
						undo = false;
					}}else if (input.equals("yes")) {
			            undo = false;
			        }
				
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
	 * @return the number of unoccupied territories remaining on the board
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
		int armies = 0;
		int territoryCount = playerTerritoriesCount(player);
		if(territoryCount < 9)
			armies = 3;
		else
			armies = territoryCount / 3;
		System.out.println(player.name + " has " + territoryCount + " territories which yields " + armies + " armies.");
		int contArmies = 0;
		int contCount = 0;
		// check if player has control of any continent
		for(int i = 0; i < continents.size(); i++) {
			if(continents.get(i).bonusArmiesAwarded() == player) {
				contArmies += continents.get(i).getBonusArmies();
				contCount++;
			}
		}
		System.out.println(player.name + " has control of " + contCount + " continents for an additional " + contArmies + " armies.");
		armies += contArmies;
		System.out.println(player.name + " receives " + armies + " armies.");
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
	public Territory chooseAttackingTerritory() {
		boolean undo = true;
		Territory tempTerritory = new Territory();
		while(undo) {
			Player currentPlayer = players.get(currentPlayerIndex);
			tempTerritory = currentPlayer.chooseAttackingTerritory(getPlayersTerritories(currentPlayer), territories, this);
			if(tempTerritory == null) {
				return null;
			}
			/*
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to attack from " + tempTerritory.getTerritoryName(), 
			        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
			        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			*/
			String confirmationMessage = "You have chosen to attack from " + tempTerritory.getTerritoryName() + " --yes/no";
			String values[] = {"Continue", "Undo"};
			try {
				int n = timedButtonPrompt(confirmationMessage, "Undo?", values);
				if (n == JOptionPane.NO_OPTION) {
					if(currentPlayer.getCredits() > 0){
						currentPlayer.useCredits(currentPlayer.getCredits()-1);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
						String inputMessage = "You do not have enough credits to undo your action.";
						timedAcknowledgement(inputMessage);
						undo = false;
					}
		        } else if (n == JOptionPane.YES_OPTION) {
		            undo = false;
		        }
			} catch(Exception e) {
				System.out.println(e.getStackTrace());
			}
		}
		return tempTerritory;
	}
	
	/**
	 * Prompts user to select one of their territories to attack from
	 * For use during the attack step of a player's turn
	 * @return Territory selected
	 */
	private Territory botchooseAttackingTerritory() {
		TelegramBot bot = new TelegramBot();
		boolean undo = true;
		Territory tempTerritory = new Territory();
		while(undo) {
			Player currentPlayer = players.get(currentPlayerIndex);
			tempTerritory = currentPlayer.botchooseAttackingTerritory(getPlayersTerritories(currentPlayer), territories, this);
			if(tempTerritory == null) {
				return null;
			}

			String confirmationMessage = "You have chosen to attack from " + tempTerritory.getTerritoryName() + " --yes/no";
			String n = null;
			bot.sendplayer(confirmationMessage);
			bot.cleanmessage();
			int counter = 0;
			while(bot.getmessage() == null && counter < 30) {
				try {
					Thread.sleep(1000);
					counter++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(bot.getmessage()!=null) {
				n = bot.getmessage();
			}
			if(n.equals("no")) {
				if(currentPlayer.getCredits() > 0){
					currentPlayer.useCredits(currentPlayer.getCredits()-1);
				}
				else{
					//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
					String inputMessage = "You do not have enough credits to undo your action.";
					bot.sendplayer(inputMessage);
					timedAcknowledgement(inputMessage);
					undo = false;
				}
			} else if (n.equals("yes")) {
				undo = false;
			}
		}
		return tempTerritory;
	}
	
	//TODO: Move to Player class
	/**
	 * Prompts player to choose a territory to attack.
	 * For use when the player is in the attack step of their turn.
	 * @param attackingTerritory Territory chosen prior to attack from
	 * @return Territory selected to be attacked
	 */
	public Territory chooseTerritoryToAttack(Territory attackingTerritory) {
		boolean undo = true;
		Territory tempTerritory = new Territory();
		while(undo) {
			tempTerritory = players.get(currentPlayerIndex).chooseTerritoryToAttack(attackingTerritory, territories, this);
			if(tempTerritory == null) {
				return null;
			}
			/*
			int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to attack " + tempTerritory.getTerritoryName(), 
			        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
			        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
			*/
			String confirmationMessage = "You have chosen to attack " + tempTerritory.getTerritoryName() + " --yes/no";
			String[] values = {"Continue", "Undo"};
			try {
				int n = timedButtonPrompt(confirmationMessage, "Undo?", values);
				if (n == JOptionPane.NO_OPTION) {
					if(players.get(currentPlayerIndex).getCredits() > 0){
						players.get(currentPlayerIndex).useCredits(players.get(currentPlayerIndex).getCredits()-1);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
						String inputMessage = "You do not have enough credits to undo your action.";
						timedAcknowledgement(inputMessage);
						undo = true;
					}
				} else if (n == JOptionPane.YES_OPTION) {
				    undo = false;
				}
			} catch(Exception e) {
				System.out.println(e.getStackTrace());
			}
		}
		return tempTerritory;
	}
	
	//TODO: Move to Player class
		/**
		 * Prompts player to choose a territory to attack.
		 * For use when the player is in the attack step of their turn.
		 * @param attackingTerritory Territory chosen prior to attack from
		 * @return Territory selected to be attacked
		 */
		private Territory botchooseTerritoryToAttack(Territory attackingTerritory) {
			TelegramBot bot = new TelegramBot();
			boolean undo = true;
			Territory tempTerritory = new Territory();
			while(undo) {
				tempTerritory = players.get(currentPlayerIndex).botchooseTerritoryToAttack(attackingTerritory, territories, this);
				if(tempTerritory == null) {
					return null;
				}
				
				String confirmationMessage = "You have chosen to attack " + tempTerritory.getTerritoryName() + " --yes/no";
				String n = null;
				bot.sendplayer(confirmationMessage);
				bot.cleanmessage();
				int counter = 0;
				while(bot.getmessage() == null && counter < 30) {
					try {
						Thread.sleep(1000);
						counter++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(bot.getmessage() != null) {
					n = bot.getmessage();
				}
				if(n.equals("no")) {
					if(players.get(currentPlayerIndex).getCredits() > 0){
						players.get(currentPlayerIndex).useCredits(players.get(currentPlayerIndex).getCredits()-1);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
						String inputMessage = "You do not have enough credits to undo your action.";
						bot.sendmessage(inputMessage);
						timedAcknowledgement(inputMessage);
						undo = true;
					}
				} else if (n.equals("yes")) {
					undo = false;
				}
			}
			return tempTerritory;
		}
	
	//TODO: Refactor this as a comparator class/interface
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
	public void moveArmies(Territory fromTerritory, Territory toTerritory, int armies) {
		System.out.println("Moving " + armies + " armies from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName());
		// TODO: check player has both territories
		// TODO: check for adjacency
		// TODO: check that there's enough armies to move and still leave at least 1
		fromTerritory.decrementArmy(armies);
		toTerritory.incrementArmy(armies);
	}
	
	public ArrayList<ArrayList<Dice>> diceHelper(Territory attackingTerritory, Territory defendingTerritory) {
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
		if(playwithbot == true) {
			String message = attackingTerritory.getPlayer().getName() + " has " + attackingTerritory.getArmyCount() + " armies on " + attackingTerritory.getTerritoryName() + ", rolls " + attackingDiceTotal + " die/dice.\n";
			message += defendingTerritory.getPlayer().getName() + " has " + defendingTerritory.getArmyCount() + " armies on " + defendingTerritory.getTerritoryName() + ", rolls " + defendingDiceTotal + " die/dice.\n";
			this.bot.sendallplayer(message);
		}
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
		String message = attackingTerritory.getPlayer().getName() + " rolled ";
		for(int i = 0; i < attackingDiceTotal; i++) {
			System.out.print(attackingDice.get(i).getCurrentValue() + " ");
			message += attackingDice.get(i).getCurrentValue() + " ";
		}
		System.out.println();
		message += '\n';
		sortDice(defendingDice);
		message += defendingTerritory.getPlayer().getName() + " rolled ";
		for(int i = 0; i < defendingDiceTotal; i++) {
			System.out.print(defendingDice.get(i).getCurrentValue() + " ");
			message += defendingDice.get(i).getCurrentValue() + " ";
		}
		System.out.println();
		if(playwithbot == true)
			this.bot.sendallplayer(message);
		ArrayList<ArrayList<Dice>> returnLists = new ArrayList<ArrayList<Dice>>();
		returnLists.add(attackingDice);
		returnLists.add(defendingDice);		
		return returnLists;
	}
	
	public void armyAdjustment(Territory attackingTerritory, ArrayList<Dice> attackingDice, Territory defendingTerritory, ArrayList<Dice> defendingDice) {
		int attackingDiceTotal = attackingDice.size();
		int defendingDiceTotal = defendingDice.size();
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
		TelegramBot bot = new TelegramBot();
		if(playwithbot == true) {
			bot.sendallplayer("\n" + attackingTerritory.getPlayer().getName() + " is attacking " + defendingTerritory.getPlayer().getName());
			bot.sendallplayer(attackingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ") vs "+ defendingTerritory.getTerritoryName() + " ("  + defendingTerritory.getArmyCount() + ")");
		}
		System.out.println("\n" + attackingTerritory.getPlayer().getName() + " is attacking " + defendingTerritory.getPlayer().getName());
		System.out.println(attackingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ") vs "+ defendingTerritory.getTerritoryName() + " ("  + defendingTerritory.getArmyCount() + ")");

		attackMessage = defendingTerritory.getPlayer().getName() + ", " + attackingTerritory.getPlayer().getName() + " is attacking your territory (" + defendingTerritory.getTerritoryName() + ")!"; 		
		//JOptionPane.showMessageDialog(null, attackMessage, "warning", JOptionPane.WARNING_MESSAGE);
		timedAcknowledgement(attackMessage);
		update(defendingTerritory.getPlayer(), attackMessage);
		
		// Prompt player to roll dice, with the number of dice determined
		// for both players by the total armies present on either territory.
		// OR allow player to "retreat" -- or stop attack
		boolean continueAttacking = true;
		while(continueAttacking) {
			
			ArrayList<ArrayList<Dice>> dice = diceHelper(attackingTerritory, defendingTerritory);
			// Find the minimum of number of dice rolled between the two players
			// (it must either be 1 or 2), then compare each of the 1 or 2 dice
			// to the opposing player's dice.
			armyAdjustment(attackingTerritory, dice.get(0), defendingTerritory, dice.get(1));
			
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
					if(this.useAPIs == true) {
						s3.pa.playerDefeated(tempPlayer);
						s3.logPlayerActivity();
					}
					// TODO: If the defending player has lost, then check if there are more than 1 remaining players in the players ArrayList.
					//			If not, the game is over.
					if(players.size() < 2) {
						// game should end somehow
						if(this.useAPIs == true) {
							s3.pa.playerWins(players.get(currentPlayerIndex));
							s3.logPlayerActivity();
						}
					}		
				}
				
				// prompt attacking player to move at least 1 troop into conquered territory
				int armiesToMove = 0;
				boolean tryAgain = true;		
				while(tryAgain) {
					try {
						//userInput = "";
						//timeUp = false;
						String inputMessage = players.get(currentPlayerIndex).getName() + ", select between 1 and " + (attackingTerritory.getArmyCount() - 1) + " armies to move from " + attackingTerritory.getTerritoryName() + " to " + defendingTerritory.getTerritoryName();
						try{
					         //new Board(true)).timedPrompt();
							String userInput = this.timedPrompt(inputMessage);
							if(userInput == null) {
								System.out.println("Null input received, defaulting to 1 army.");
								armiesToMove = 1;
							} else {
								armiesToMove = Integer.parseInt(userInput);
							}
							tryAgain = false;
					    }
					    catch(Exception e){
					    	
					    }
						
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
				if(this.useAPIs == true) {
					s3.pa.fortify(players.get(currentPlayerIndex), attackingTerritory, defendingTerritory, armiesToMove);
					s3.logPlayerActivity();
				}
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
				//Object selected = JOptionPane.showInputDialog(null, "Continue attacking from " + attackingTerritory.getTerritoryName() + "?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
				String inputMessage = "Continue attacking from " + attackingTerritory.getTerritoryName() + "?";
				try {
					Object selected = timedSelectionPrompt(inputMessage, values);
					if ( selected != null ) {
						//null if the user cancels. 
					    String selectedString = selected.toString();
						if(selectedString == "No") {
							continueAttacking = false;
						}
					} else {
					    System.out.println("User cancelled");
					    continueAttacking = false;
					}
				} catch(Exception e) {
					System.out.println(e.getStackTrace());
				}

			}
			// C) the attacking player retreats

		}
		String[] values = {"Yes", "No"};
		//Object selected = JOptionPane.showInputDialog(null, "Choose a different territory to attack from?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
		String inputMessage = "Choose a different territory to attack from?";
		try {
			Object selected = timedSelectionPrompt(inputMessage, values);
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
		} catch(Exception e) {
			
		}

		// Prompt the player either attack another territory
		// or end the attack phase of their turn
		return curAttack;
	}
	
	/**
	 * The attack step for the current player
	 * @param curAttack Attack object that holds a flag indicating if the Player receives a Risk card at the end of the attack step
	 * 	AND if the player chooses to continue the current attack
	 * @param attackingTerritory selected territory to attack from
	 * @param defendingTerritory selected territory to attack
	 * @return current Attack object
	 */
	private Attack botattack(Attack curAttack, Territory attackingTerritory, Territory defendingTerritory) {
		bot = new TelegramBot();
		bot.sendallplayer("\n" + attackingTerritory.getPlayer().getName() + " is attacking " + defendingTerritory.getPlayer().getName());
		bot.sendallplayer(attackingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ") vs "+ defendingTerritory.getTerritoryName() + " ("  + defendingTerritory.getArmyCount() + ")");
		System.out.println("\n" + attackingTerritory.getPlayer().getName() + " is attacking " + defendingTerritory.getPlayer().getName());
		System.out.println(attackingTerritory.getTerritoryName() + " ("  + attackingTerritory.getArmyCount() + ") vs "+ defendingTerritory.getTerritoryName() + " ("  + defendingTerritory.getArmyCount() + ")");

		attackMessage = defendingTerritory.getPlayer().getName() + ", " + attackingTerritory.getPlayer().getName() + " is attacking your territory (" + defendingTerritory.getTerritoryName() + ")!"; 		
		//JOptionPane.showMessageDialog(null, attackMessage, "warning", JOptionPane.WARNING_MESSAGE);
		timedAcknowledgement(attackMessage);
		update(defendingTerritory.getPlayer(), attackMessage);
		
		// Prompt player to roll dice, with the number of dice determined
		// for both players by the total armies present on either territory.
		// OR allow player to "retreat" -- or stop attack
		boolean continueAttacking = true;
		while(continueAttacking) {
			ArrayList<ArrayList<Dice>> dice = diceHelper(attackingTerritory, defendingTerritory);
			// Find the minimum of number of dice rolled between the two players
			// (it must either be 1 or 2), then compare each of the 1 or 2 dice
			// to the opposing player's dice.
			
			armyAdjustment(attackingTerritory, dice.get(0), defendingTerritory, dice.get(1));
			
			// Repeat until:
			// A) The attacking player has wiped out the defending players armies on their territory.
			if(defendingTerritory.getArmyCount() == 0) {
				if(!curAttack.receiveCard)
					curAttack.receiveCard = true;
				// change ownership of territory
				Player tempPlayer = defendingTerritory.getPlayer();
				defendingTerritory.setPlayer(attackingTerritory.getPlayer());
				
				bot.sendplayer(attackingTerritory.getPlayer().getName() + " has conquered " + defendingTerritory.getTerritoryName());
				System.out.println(attackingTerritory.getPlayer().getName() + " has conquered " + defendingTerritory.getTerritoryName());

				// Check if the defending player has more than 0 remaining territories. 
				// If not, remove player from players ArrayList
				if(playerTerritoriesCount(tempPlayer) < 1) {
					players.remove(tempPlayer);
					if(this.useAPIs == true) {
						s3.pa.playerDefeated(tempPlayer);
						s3.logPlayerActivity();
					}
					// TODO: If the defending player has lost, then check if there are more than 1 remaining players in the players ArrayList.
					//			If not, the game is over.
					if(players.size() < 2) {
						// game should end somehow
						if(this.useAPIs == true) {
							s3.pa.playerWins(players.get(currentPlayerIndex));
							s3.logPlayerActivity();
						}
					}		
				}
				
				// prompt attacking player to move at least 1 troop into conquered territory
				int armiesToMove = 0;
				boolean tryAgain = true;
				String userinput = null;
				while(tryAgain) {
					try {
						//userInput = "";
						//timeUp = false;
						String inputMessage = players.get(currentPlayerIndex).getName() + ", select between 1 and " + (attackingTerritory.getArmyCount() - 1) + " armies to move from " + attackingTerritory.getTerritoryName() + " to " + defendingTerritory.getTerritoryName();
						bot.sendplayer(inputMessage);
						try{
					         //new Board(true)).timedPrompt();
							bot.cleanmessage();
							int counter = 0;
							while(bot.getmessage() == null && counter < 30) {
								try {
									Thread.sleep(1000);
									counter++;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if(bot.getmessage()!=null) {
								userinput = bot.getmessage();
							}
							if(userinput == null) {
								System.out.println("Null input received, defaulting to 1 army.");
								bot.sendplayer("Null input received, defaulting to 1 army.");
								armiesToMove = 1;
							} else {
								armiesToMove = Integer.parseInt(userinput);
							}
							tryAgain = false;
					    }
					    catch(Exception e){
					    	
					    }
						
					} catch(NumberFormatException e) {
						// not an int
						bot.sendplayer("Could not parse number. Try again");
						System.out.println("Could not parse number. Try again");
					}
					if(armiesToMove > attackingTerritory.getArmyCount() - 1) {
						tryAgain = true;
						bot.sendplayer("You don't have enough troops.");
						System.out.println("You don't have enough troops.");
					}
					if(armiesToMove < 1) {
						tryAgain = true;
						bot.sendplayer("Pick a number between 1 and " + (attackingTerritory.getArmyCount() - 1));
						System.out.println("Pick a number between 1 and " + (attackingTerritory.getArmyCount() - 1));
					}
				}
				moveArmies(attackingTerritory, defendingTerritory, armiesToMove);
				if(this.useAPIs == true) {
					s3.pa.fortify(players.get(currentPlayerIndex), attackingTerritory, defendingTerritory, armiesToMove);
					s3.logPlayerActivity();
				}
				continueAttacking = false;
			}
			// B) The attacking player has only 1 remaining army on their territory
			else if(attackingTerritory.getArmyCount() == 1) {
				// attacking player can no longer attack from this territory
				bot.sendplayer(attackingTerritory.getPlayer().getName() + " no longer has enough armies to attack from " + attackingTerritory.getTerritoryName());
				System.out.println(attackingTerritory.getPlayer().getName() + " no longer has enough armies to attack from " + attackingTerritory.getTerritoryName());
				break;
			} else {
				// if attacking player has more armies, 
				// ask if they want to attack again from current territory
				String userInput = null;
				//Object selected = JOptionPane.showInputDialog(null, "Continue attacking from " + attackingTerritory.getTerritoryName() + "?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
				String inputMessage = "Continue attacking from " + attackingTerritory.getTerritoryName() + "?";
				bot.sendplayer(inputMessage);
				bot.cleanmessage();
				int counter = 0;
				while(bot.getmessage() == null && counter < 30) {
					try {
						Thread.sleep(1000);
						counter++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(bot.getmessage()!=null) {
					userInput = bot.getmessage();
				}
				if(userInput.equals("no")) {
					continueAttacking = false;
				}

			}
			// C) the attacking player retreats

		}
		String userInput = null;
		//Object selected = JOptionPane.showInputDialog(null, "Choose a different territory to attack from?", "Selection", JOptionPane.DEFAULT_OPTION, null, values, "0");
		String inputMessage = "Choose a different territory to attack from?";
		bot.sendplayer(inputMessage);
		bot.cleanmessage();
		int counter = 0;
		while(bot.getmessage() == null && counter < 30) {
			try {
				Thread.sleep(1000);
				counter++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(bot.getmessage()!=null) {
			userInput = bot.getmessage();
		}
		if(userInput.equals("no")) {
			curAttack.continueAttack = false;
		}

		// Prompt the player either attack another territory
		// or end the attack phase of their turn
		return curAttack;
	}
	
	@Override
	public void update(Player p, String o){
		attackMessage = o;
		p.setAttackMessage(p, o);
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
		Territory fromTerritory = null;
		boolean tryAgain = true;
		while(tryAgain) {
			boolean undo = true;
			while(undo) {
				try {
					//userInput = "";
					//timeUp = false;
					String inputMessage = players.get(currentPlayerIndex).getName() + ", choose a territory to send armies FROM";
					try {
				         //(new Board(true)).timedPrompt();
						String userInput = this.timedPrompt(inputMessage);
						if(userInput == null) {
							System.out.println("Null input received, skipping fortify step.");
							return;
						}
						fromTerritoryIndex = Integer.parseInt(userInput);
						fromTerritory = territories.get(fromTerritoryIndex);
						tryAgain = false;
				    }
				    catch(Exception e){
				            System.out.println( e );
				    }
					/*
					if(timeUp){
						// Move to the next player
					}
					*/

				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
					tryAgain = true;
				} catch(Exception e) {
					// not a territory index
					System.out.println("Error: " + e + "\nTry again");
					tryAgain = true;
				}
				if(fromTerritory == null) {
					return;
				}
				/*
				int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to send armies from " + fromTerritory.getTerritoryName(), 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
				*/
				try {
					String[] values = {"Continue", "Undo"};
					int n = timedButtonPrompt("You have chosen to send armies from " + fromTerritory.getTerritoryName(), "Undo?", values);
					if (n == JOptionPane.YES_OPTION) {
						if(players.get(currentPlayerIndex).getCredits() > 0){
							players.get(currentPlayerIndex).useCredits(players.get(currentPlayerIndex).getCredits()-1);
						} else {
							//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
							String inputMessage = "You do not have enough credits to undo your action.";
							timedAcknowledgement(inputMessage);
						}
			            undo = false;
			        } else if(n == JOptionPane.NO_OPTION) {
			        	undo = true;
			        }
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		int toTerritoryIndex = -1;
		tryAgain = true;
		Territory toTerritory = null;
		while(tryAgain) {
			boolean undo = true;
			while(undo) {
				try {
					//userInput = "";
					//timeUp = false;
					String inputMessage = players.get(currentPlayerIndex).getName() + ", choose a territory to send armies TO.";
					try{
				         //(new Board(true)).timedPrompt();
						String userInput = this.timedPrompt(inputMessage);
						if(userInput == null) {
							System.out.println("Null input received, skipping fortify step.");
							return;
						}
						toTerritoryIndex = Integer.parseInt(userInput);
						toTerritory = territories.get(toTerritoryIndex);
						System.out.println("toTerritoryIndex: " + toTerritoryIndex + ", " + toTerritory.getTerritoryName());
						tryAgain = false;
				    }
				    catch(Exception e ){
				            
				    }
					/*
					if(timeUp){
						// Move to the next player
					}
					*/

				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
					tryAgain = true;
				} catch(Exception e) {
					// not a territory index
					System.out.println("Error: " + e + "\nTry again");
					tryAgain = true;
				}
				if(toTerritory == null) {
					return;
				}
				String confirmationMessage = "You have chosen to send armies from " + toTerritory.getTerritoryName();
				Player player = players.get(currentPlayerIndex);
				String[] values = {"Yes", "No"};

				try {
					int n = timedButtonPrompt(confirmationMessage, "Undo?", values);
					if (n == JOptionPane.NO_OPTION) {
						if(player.getCredits() > 0){
							player.useCredits(player.getCredits()-1);
						}
						else{
							//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
							String inputMessage = "You do not have enough credits to undo your action.";
							timedAcknowledgement(inputMessage);
							undo = false;
						}
			        } else if (n == JOptionPane.YES_OPTION) {
			            undo = false;
			        }
				} catch(Exception e) {
					System.out.println(e.getStackTrace());
				}
				/*
				int n = JOptionPane.showOptionDialog(new JFrame(), "You have chosen to send armies from " + toTerritory.getTerritoryName(), 
				        "Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				        null, new Object[] {"Continue", "Undo"}, JOptionPane.YES_OPTION);
				if (n == JOptionPane.NO_OPTION) {
					if(players.get(currentPlayerIndex).getCredits() > 0){
						players.get(currentPlayerIndex).useCredits(players.get(currentPlayerIndex).getCredits()-1);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
						String inputMessage = "You do not have enough credits to undo your action.";
						timedAcknowledgement(inputMessage);
						undo = true;
					}
		        } else if (n == JOptionPane.YES_OPTION) {
		            undo = false;
		        }
		        */
			}
		}
		
		int armiesToMove = 0;
		tryAgain = true;
		while(tryAgain) {
			try {
				//userInput = "";
				//timeUp = false;
				String inputMessage = players.get(currentPlayerIndex).getName() + ", select between 1 and " + (fromTerritory.getArmyCount() - 1) + " armies to move from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName();
				try{
			         //(new Board(true)).timedPrompt();
					String userInput = this.timedPrompt(inputMessage);
					if(userInput == null) {
						System.out.println("Null input received, skipping fortify step.");
						return;
					}
					armiesToMove = Integer.parseInt(userInput);
					tryAgain = false;
			    }
			    catch(Exception e ){
			            
			    }
				/*
				if(timeUp){
					// Move to the next player
				}
				*/

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
		if(this.useAPIs == true) {
			s3.pa.fortify(players.get(currentPlayerIndex), fromTerritory, toTerritory, armiesToMove);
			s3.logPlayerActivity();
		}
		
	}
	
	/**
	 * Fortification step during the current player's turn
	 */
	// Currently, this step only allows the player to move armies from one territory to another that is immediately adjacent to it
	// I believe the rules of Risk allow the player to move armies across multiple territories as long as they're all connected
	// TODO: We need to include a method that traverses all of the player's connected territories rather than just the immediately adjacent ones.
	public void botfortify() {
		// Prompt player to pick a territory to move armies from	
		TelegramBot bot = new TelegramBot();
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
		Territory fromTerritory = null;
		boolean tryAgain = true;
		String userInput = null;
		while(tryAgain) {
			boolean undo = true;
			while(undo) {
				try {
					//userInput = "";
					//timeUp = false;
					String inputMessage = players.get(currentPlayerIndex).getName() + ", choose a territory to send armies FROM";
					bot.sendplayer(inputMessage);
					try{
				         //(new Board(true)).timedPrompt();
						bot.cleanmessage();
						int counter = 0;
						while(bot.getmessage() == null && counter < 30) {
							try {
								Thread.sleep(1000);
								counter++;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(bot.getmessage() != null) {
							userInput = bot.getmessage();
						}
						if(userInput == null) {
							System.out.println("Null input received, skipping fortify step.");
							return;
						}
						fromTerritoryIndex = Integer.parseInt(userInput);
						fromTerritory = territories.get(fromTerritoryIndex);
						tryAgain = false;
				     }
				    catch(Exception e){
				            System.out.println( e );
				    }
					/*
					if(timeUp){
						// Move to the next player
					}
					*/

				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
					tryAgain = true;
				} catch(Exception e) {
					// not a territory index
					System.out.println("Error: " + e + "\nTry again");
					tryAgain = true;
				}
				if(fromTerritory == null) {
					return;
				}
				bot.sendplayer("You have chosen to send armies from " + fromTerritory.getTerritoryName());
				bot.cleanmessage();
				int counter = 0;
				String n = null;
				while(bot.getmessage() == null && counter < 30) {
					try {
						Thread.sleep(1000);
						counter++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(bot.getmessage() != null) {
					n = bot.getmessage();
				}
				if(n.equals("yes")) {
					if(players.get(currentPlayerIndex).getCredits() > 0){
						players.get(currentPlayerIndex).useCredits(players.get(currentPlayerIndex).getCredits()-1);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
						String inputMessage = "You do not have enough credits to undo your action.";
						bot.sendplayer(inputMessage);
						timedAcknowledgement(inputMessage);
					}
					undo = false;
				} else if (n.equals("no")) {
					undo = true;
				} else {
					
				}
			}
		}
		int toTerritoryIndex = -1;
		tryAgain = true;
		Territory toTerritory = null;
		userInput = null;
		while(tryAgain) {
			boolean undo = true;
			while(undo) {
				try {
					//userInput = "";
					//timeUp = false;
					String inputMessage = players.get(currentPlayerIndex).getName() + ", choose a territory to send armies TO.";
					bot.sendplayer(inputMessage);
					try{
						int counter = 0;
				         //(new Board(true)).timedPrompt();
						while(bot.getmessage() == null && counter < 30) {
							try {
								Thread.sleep(1000);
								counter++;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(bot.getmessage()!=null) {
							userInput = bot.getmessage();
						}
						if(userInput == null) {
							bot.sendplayer("Null input received, skipping fortify step.");
							System.out.println("Null input received, skipping fortify step.");
							return;
						}
						toTerritoryIndex = Integer.parseInt(userInput);
						toTerritory = territories.get(toTerritoryIndex);
						bot.sendplayer("toTerritoryIndex: " + toTerritoryIndex + ", " + toTerritory.getTerritoryName());
						System.out.println("toTerritoryIndex: " + toTerritoryIndex + ", " + toTerritory.getTerritoryName());
						tryAgain = false;
				    }
				    catch(Exception e ){
				            
				    }
					/*
					if(timeUp){
						// Move to the next player
					}
					*/

				} catch(NumberFormatException e) {
					// not an int
					System.out.println("Could not parse number. Try again");
					tryAgain = true;
				} catch(Exception e) {
					// not a territory index
					System.out.println("Error: " + e + "\nTry again");
					tryAgain = true;
				}
				if(toTerritory == null) {
					return;
				}
				bot.sendplayer("You have chosen to send armies from " + toTerritory.getTerritoryName());
				String n = null;
				bot.cleanmessage();
				int counter = 0;
				while(bot.getmessage() == null && counter < 30) {
					try {
						Thread.sleep(1000);
						counter++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(bot.getmessage()!=null) {
					n = bot.getmessage();
				}
				if(n.equals("no")) {
					if(players.get(currentPlayerIndex).getCredits() > 0){
						players.get(currentPlayerIndex).useCredits(players.get(currentPlayerIndex).getCredits()-1);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You do not have enough credits to undo your action.");
						String inputMessage = "You do not have enough credits to undo your action.";
						bot.sendplayer(inputMessage);
						timedAcknowledgement(inputMessage);
						undo = true;
					}
				} else if (n.equals("yes")) {
					undo = false;
				}
			}
		}
		
		int armiesToMove = 0;
		tryAgain = true;
		String Input = null;
		while(tryAgain) {
			try {
				//userInput = "";
				//timeUp = false;
				String inputMessage = players.get(currentPlayerIndex).getName() + ", select between 1 and " + (fromTerritory.getArmyCount() - 1) + " armies to move from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName();
				bot.sendplayer(players.get(currentPlayerIndex).getName() + ", select between 1 and " + (fromTerritory.getArmyCount() - 1) + " armies to move from " + fromTerritory.getTerritoryName() + " to " + toTerritory.getTerritoryName());
				try{
					bot.cleanmessage();
					int counter = 0;
					while(bot.getmessage() == null && counter < 30) {
						try {
							Thread.sleep(1000);
							counter++;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(bot.getmessage()!=null) {
						Input = bot.getmessage();
					}
					if(Input == null) {
						bot.sendplayer("Null input received, skipping fortify step.");
						System.out.println("Null input received, skipping fortify step.");
						return;
					}
					armiesToMove = Integer.parseInt(Input);
					tryAgain = false;
			    }
			    catch(Exception e ){
			            
			    }
				/*
				if(timeUp){
					// Move to the next player
				}
				*/

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
		if(this.useAPIs == true) {
			s3.pa.fortify(players.get(currentPlayerIndex), fromTerritory, toTerritory, armiesToMove);
			s3.logPlayerActivity();
		}
		
	}
	

	/**
	 * 
	 * @param player
	 * @param cardSet
	 * @return
	 */
	public int turnInCardSet(Player player, ArrayList<Card> cardSet) {
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
	 * In addition, at the beginning of each turn, players are given the option to purchase in-game credit which they can use to purchase undo moves and wild cards
	 * It costs 1 credit to undo a move.
	 * It costs 5 credits to purchase a wild card.
	 * They can also transfer credits to another player for no additional cost.
	 * @return
	 */
	public boolean currentPlayerTurn() {
		boolean continueGame = true;
		Player currentPlayer = players.get(currentPlayerIndex);
		System.out.println("It is " + currentPlayer.getName() + "'s turn.");
		
		// Purchasing in-game credit
		boolean invalidCredits = true;
		int credits = 0;
		/*
		int c = JOptionPane.showOptionDialog(new JFrame(),currentPlayer.getName() + ", would you like to purchase in-game credits?", 
		        "In-Game Credits", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
		        null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
		*/
		String inputMessage = currentPlayer.getName() + ", would you like to purchase in-game credits??";
		String instruction = "In-Game Credits";
		String[] values = {"Yes", "No"};
		try {
			int c = timedButtonPrompt(inputMessage, instruction, values);
			if (c == JOptionPane.YES_OPTION) {
				while(invalidCredits){
					credits = Integer.parseInt(JOptionPane.showInputDialog(null, "How many credits would you like to purchase? You currently have " + currentPlayer.getCurrency() + " units of currency"));
					if(currentPlayer.getCurrency() >= credits && credits >= 0){
						invalidCredits = false;
						currentPlayer.useCurrency(credits);
						currentPlayer.buyCredits(credits);
						if(this.useAPIs == true) {
							s3.pa.buyCredits(currentPlayer, credits);
							s3.logPlayerActivity();
						}
					}
					else if (currentPlayer.getCurrency() < currentPlayer.getCredits()){
						//JOptionPane.showMessageDialog(null, "You don't have enough currency. Please try again.");
						inputMessage = "You don't have enough currency. Please try again.";
						timedAcknowledgement(inputMessage);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You have entered an invalid number. Please try again.");
						inputMessage = "You have entered an invalid number. Please try again.";
						timedAcknowledgement(inputMessage);
					}
				}
	        }
			if(c == JOptionPane.CLOSED_OPTION) {
				System.out.println("Closed option");
			}
			if(c == JOptionPane.CANCEL_OPTION) {
				System.out.println("Cancel option");
			}
			if(c == JOptionPane.DEFAULT_OPTION) {
				System.out.println("Default option");
			}
		} catch(Exception e) {
			System.out.println(e.getStackTrace());
		}

		// Buy wild cards
		/*
		int card = JOptionPane.showOptionDialog(new JFrame(),currentPlayer.getName() + ", would you like to purchase a wild card (each one costs 5 units of currency)?", 
		        "Cards", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
		        null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
		 */
		inputMessage = currentPlayer.getName() + ", would you like to purchase a wild card (each one costs 5 units of currency)?";
		// values variable unchanged
		try {
			int card = timedButtonPrompt(inputMessage, "Purchase?", values);
			if (card == JOptionPane.YES_OPTION){
				if(currentPlayer.getCredits() >= 5){
					Card wild = new Card("Wild", (byte)4);
					currentPlayer.addCard(wild);
					currentPlayer.useCredits(5);
					//JOptionPane.showMessageDialog(null, "You have successfully purchased a wild card!");
					inputMessage = "You have successfully purchased a wild card!";
					timedAcknowledgement(inputMessage);
					if(this.useAPIs == true) {
						s3.pa.buyCards(currentPlayer);
						s3.logPlayerActivity();
					}
				}
				else{
					//JOptionPane.showMessageDialog(null, "Sorry, you don't have enough currency.");
					inputMessage = "Sorry, you don't have enough currency.";
					timedAcknowledgement(inputMessage);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Transfer credits
		String receiver = "";
		boolean playerNotFound = true;
		boolean invalidTransferCredits = true;
		int playerIndex = 0;
		String transferCredits = "";
		/*
		int transfer = JOptionPane.showOptionDialog(new JFrame(), currentPlayer.getName() + ", would you like to transfer credits to another player?", 
		        "Transfer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
		        null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
		*/
		inputMessage = currentPlayer.getName() + ", would you like to transfer credits to another player?";
		try {
			int transfer = timedButtonPrompt(inputMessage, "Transfer?", values);
			if (transfer == JOptionPane.YES_OPTION){
				// TODO: change this to a dropdown box
				inputMessage = "Enter the name of the player you wish to transfer credits to.";
				while(playerNotFound){
					//reciever = JOptionPane.showInputDialog(null, "Enter the name of the player you wish to transfer credits to.");
					receiver = timedPrompt(inputMessage);
					for(int i = 0; i < players.size(); i++){
						if(receiver.equals(players.get(i).getName())){
							playerIndex = i;
							playerNotFound = false;
						}
					}
					if(playerNotFound){
						//JOptionPane.showMessageDialog(null, "The player you requested is not found. Please try again.");
						inputMessage = "The player you requested is not found. Please try again.";
						timedAcknowledgement(inputMessage);
					}
				}
				while(invalidTransferCredits){
					transferCredits = JOptionPane.showInputDialog(null, "How many credits would you like to transfer to " + players.get(playerIndex).getName());
					int transferNum = Integer.parseInt(transferCredits);
					if(transferNum <= currentPlayer.getCredits()){
						players.get(playerIndex).buyCredits(transferNum);
						currentPlayer.useCredits(transferNum);
						invalidTransferCredits = false;
					}
					else if(transferNum > currentPlayer.getCredits()){
						//JOptionPane.showMessageDialog(null, "You don't have enough currency. Please try again.");
						inputMessage = "You don't have enough currency. Please try again.";
						timedAcknowledgement(inputMessage);
					}
					else{
						//JOptionPane.showMessageDialog(null, "You have entered an invalid number. Please try again.");
						inputMessage = "You have entered an invalid number. Please try again.";
						timedAcknowledgement(inputMessage);
					}
				}
				if(this.useAPIs == true) {
					s3.pa.transferCredits(currentPlayer, receiver, transferCredits);
					s3.logPlayerActivity();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		// 1. Placing new troops
		
		// Check if a set of Risk cards can be turned in
		System.out.println(currentPlayer.getName() + "'s Risk Cards:");
		ArrayList<Card> cardsTurnedIn = currentPlayer.cardCheck();
		if(cardsTurnedIn != null) {
			int bonusArmies = turnInCardSet(currentPlayer, cardsTurnedIn);
			System.out.println(currentPlayer.getName() + " turned in a set of Risk Cards and was awarded " + bonusArmies + " armies.");
			currentPlayer.increaseArmies(bonusArmies);
		}
	
		
		// Determine total armies received by board
		int armiesRecd = armyReplenishment(currentPlayer);
		currentPlayer.increaseArmies(armiesRecd);
		if(this.useAPIs == true) {
			s3.pa.receiveArmies(currentPlayer, armiesRecd);
			s3.logPlayerActivity();
		}
		
		// Prompt player to select a territory
		while(currentPlayer.armies > 0) {
			printTerritories(false, true);
			int ti = pickTerritory(false, currentPlayer);
			Territory tempTerritory = null;
			if(ti == -1) {
				// pick first territory
				ArrayList<Territory> playerTerritories = getPlayersTerritories(currentPlayer);
				Random rand = new Random();
				tempTerritory = playerTerritories.get(rand.nextInt(playerTerritories.size()));
			} else {
				tempTerritory = territories.get(ti);
			}
			// Prompt player to place at least 1 army on selected territory
			int armies = currentPlayer.chooseArmiesQty(this);
			tempTerritory.incrementArmy(armies);
			currentPlayer.decreaseArmies(armies);
			if(this.useAPIs == true) {
				s3.pa.placeArmies(currentPlayer, tempTerritory, armies);
				s3.logPlayerActivity();
			}
			// Repeat until no armies remaining for player.
		}
		printTerritories(false, true);
		
		// 2. Attacking
		Attack currentAttack = new Attack(currentPlayer);
		while(currentAttack.continueAttack) {
			// Prompt player to choose a territory to attack from
			Territory attackingTerritory = chooseAttackingTerritory();
			if(attackingTerritory == null) {
				System.out.println("No attacking territory selected, ending attack step.");
				currentAttack.continueAttack = false;
				break;
			}
			System.out.println("Attacking from " + attackingTerritory.getTerritoryName());
			// Prompt player to choose a territory to attack
			Territory defendingTerritory = chooseTerritoryToAttack(attackingTerritory);
			if(defendingTerritory == null) {
				System.out.println("No defending territory selected, ending attack step.");
				currentAttack.continueAttack = false;
				break;
			}
			System.out.println("Defending from " + defendingTerritory.getTerritoryName());
			
			
			// Continue until player decides to end attack phase
			if(this.useAPIs == true) {
				s3.pa.attack(attackingTerritory, defendingTerritory);
				s3.logPlayerActivity();
			}
			currentAttack = attack(currentAttack, attackingTerritory, defendingTerritory);
		}
		if(currentAttack.receiveCard) {
			Card tempCard = drawCard();
			currentAttack.attackingPlayer.addCard(tempCard);
			System.out.println(currentAttack.attackingPlayer.getName() + " has received one Risk card");
			if(this.useAPIs == true) {
				s3.pa.receiveRiskCard(currentPlayer, tempCard);
				s3.logPlayerActivity();
			}
		}
		
		// 3. Fortifying
		fortify();
		
		// Change currentPlayerIndex to next player
		incrementCurrentPlayerIndex();
		
		//post the number of territories conquered by each player on Twitter 
		//after each turn and at the end of the game
		if(this.useAPIs) {
			Twitter4j newTwitter = new Twitter4j();
			newTwitter.PostTwitter(" Player " + currentPlayer.getName() + " have  conquered " + playerTerritoriesCount(currentPlayer) + " territories");
		}
		
		// Check if one player controls all the territories
		// if so, continueGame = false
		if(players.size() < 2) {
			continueGame = false;
			return continueGame;
		} else {
			return continueGame;
		}
	}
	
	public boolean botcurrentPlayerTurn() {
		boolean continueGame = true;
		TelegramBot bot = new TelegramBot();
		Player currentPlayer = players.get(currentPlayerIndex);
		currentplayerID = currentPlayer.getChatID();
		bot.sendplayer("It is " + currentPlayer.getName() + "'s turn.");
		System.out.println("It is " + currentPlayer.getName() + "'s turn.");
		
		// Purchasing in-game credit
		boolean invalidCredits = true;
		int credits = 0;
		/*
		int c = JOptionPane.showOptionDialog(new JFrame(),currentPlayer.getName() + ", would you like to purchase in-game credits?", 
		        "In-Game Credits", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
		        null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
		*/
		String inputMessage = currentPlayer.getName() + ", would you like to purchase in-game credits?";
		bot.sendplayer(inputMessage);
		String answer = null;
		try {
			bot.cleanmessage();
			int counter = 0;
			while(bot.getmessage() == null && counter < 30) {
				try {
					counter++;
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bot.getmessage()!=null) {
				answer = bot.getmessage();
			}
			if (answer.equals("yes")) {
				while(invalidCredits){
					bot.sendplayer("How many credits would you like to purchase? You currently have " + currentPlayer.getCurrency() + " units of currency");
					bot.cleanmessage();
					answer = null;
					counter = 0;
					while(bot.getmessage() == null && counter < 60) {
						try {
							Thread.sleep(1000);
							counter++;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(bot.getmessage()!=null) {
						answer = bot.getmessage();
					}
					credits = Integer.parseInt(answer);
					if(currentPlayer.getCurrency() >= credits && credits >= 0){
						invalidCredits = false;
						currentPlayer.useCurrency(credits);
						currentPlayer.buyCredits(credits);
						if(this.useAPIs == true) {
							s3.pa.buyCredits(currentPlayer, credits);
							s3.logPlayerActivity();
						}
					}
					else if (currentPlayer.getCurrency() < currentPlayer.getCredits()){
						bot.sendplayer("You don't have enough currency. Please try again.");
						inputMessage = "You don't have enough currency. Please try again.";
						timedAcknowledgement(inputMessage);
					}
					else{
						bot.sendplayer("You have entered an invalid number. Please try again.");
						inputMessage = "You have entered an invalid number. Please try again.";
						timedAcknowledgement(inputMessage);
					}
				}
	        }
		} catch(Exception e) {
			System.out.println(e.getStackTrace());
		}

		// Buy wild cards
		inputMessage = currentPlayer.getName() + ", would you like to purchase a wild card (each one costs 5 units of currency)?";
		bot.sendplayer(inputMessage);
		// values variable unchanged
		try {
			bot.cleanmessage();
			int counter = 0;
			while(bot.getmessage() == null && counter < 30) {
				try {
					Thread.sleep(1000);
					counter++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bot.getmessage()!=null) {
				answer = bot.getmessage();
			}
			if (answer.equals("yes")){
				if(currentPlayer.getCredits() >= 5){
					Card wild = new Card("Wild", (byte)4);
					currentPlayer.addCard(wild);
					currentPlayer.useCredits(5);
					inputMessage = "You have successfully purchased a wild card!";
					bot.sendplayer(inputMessage);
					timedAcknowledgement(inputMessage);
					if(this.useAPIs == true) {
						s3.pa.buyCards(currentPlayer);
						s3.logPlayerActivity();
					}
				}
				else{
					inputMessage = "Sorry, you don't have enough currency.";
					bot.sendplayer(inputMessage);
					timedAcknowledgement(inputMessage);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Transfer credits
		String receiver = "";
		boolean playerNotFound = true;
		boolean invalidTransferCredits = true;
		int playerIndex = 0;
		String transferCredits = "";
		/*
		int transfer = JOptionPane.showOptionDialog(new JFrame(), currentPlayer.getName() + ", would you like to transfer credits to another player?", 
		        "Transfer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
		        null, new Object[] {"Yes", "No"}, JOptionPane.YES_OPTION);
		*/
		inputMessage = currentPlayer.getName() + ", would you like to transfer credits to another player?";
		bot.sendplayer(inputMessage);
		try {
			bot.cleanmessage();
			int counter = 0;
			while(bot.getmessage() == null && counter < 30) {
				try {
					Thread.sleep(1000);
					counter++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bot.getmessage()!=null) {
				answer = bot.getmessage();
			}
			if (answer.equals("yes")){
				// TODO: change this to a dropdown box
				inputMessage = "Enter the name of the player you wish to transfer credits to.";
				bot.sendplayer(inputMessage);
				while(playerNotFound){
					bot.cleanmessage();
					counter = 0;
					while(bot.getmessage() == null && counter < 30) {
						try {
							Thread.sleep(1000);
							counter++;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(bot.getmessage()!=null) {
						answer = bot.getmessage();
					}
					receiver = answer;
					for(int i = 0; i < players.size(); i++){
						if(receiver.equals(players.get(i).getName())){
							playerIndex = i;
							playerNotFound = false;
						}
					}
					if(playerNotFound){
						inputMessage = "The player you requested is not found. Please try again.";
						bot.sendplayer(inputMessage);
						timedAcknowledgement(inputMessage);
					}
				}
				while(invalidTransferCredits){
					bot.sendplayer("How many credits would you like to transfer to " + players.get(playerIndex).getName());
					bot.cleanmessage();
					counter = 0;
					while(bot.getmessage() == null && counter < 30) {
						try {
							Thread.sleep(1000);
							counter++;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(bot.getmessage()!=null) {
						answer = bot.getmessage();
					}
					transferCredits = answer;
					int transferNum = Integer.parseInt(transferCredits);
					if(transferNum <= currentPlayer.getCredits()){
						players.get(playerIndex).buyCredits(transferNum);
						currentPlayer.useCredits(transferNum);
						invalidTransferCredits = false;
					}
					else if(transferNum > currentPlayer.getCredits()){
						inputMessage = "You don't have enough currency. Please try again.";
						bot.sendplayer(inputMessage);
						timedAcknowledgement(inputMessage);
					}
					else{
						inputMessage = "You have entered an invalid number. Please try again.";
						bot.sendplayer(inputMessage);
						timedAcknowledgement(inputMessage);
					}
				}
				if(this.useAPIs == true) {
					s3.pa.transferCredits(currentPlayer, receiver, transferCredits);
					s3.logPlayerActivity();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// 1. Placing new troops
		
		// Check if a set of Risk cards can be turned in
		System.out.println(currentPlayer.getName() + "'s Risk Cards:");
		ArrayList<Card> cardsTurnedIn = currentPlayer.cardCheck();
		if(cardsTurnedIn != null) {
			int bonusArmies = turnInCardSet(currentPlayer, cardsTurnedIn);
			System.out.println(currentPlayer.getName() + " turned in a set of Risk Cards and was awarded " + bonusArmies + " armies.");
			currentPlayer.increaseArmies(bonusArmies);
		}
	
		
		// Determine total armies received by board
		int armiesRecd = armyReplenishment(currentPlayer);
		currentPlayer.increaseArmies(armiesRecd);
		if(this.useAPIs == true) {
			s3.pa.receiveArmies(currentPlayer, armiesRecd);
			s3.logPlayerActivity();
		}
		
		// Prompt player to select a territory
		while(currentPlayer.armies > 0) {
			printTerritories(false, true);
			int ti = botpickTerritory(false, currentPlayer);
			Territory tempTerritory = territories.get(ti);
			System.out.print(currentPlayer.getName() + " has entered " + ti + ", " + tempTerritory.getTerritoryName());
			// Prompt player to place at least 1 army on selected territory
			int armies = currentPlayer.botchooseArmiesQty(this);
			tempTerritory.incrementArmy(armies);
			currentPlayer.decreaseArmies(armies);
			if(this.useAPIs == true) {
				s3.pa.placeArmies(currentPlayer, tempTerritory, armies);
				s3.logPlayerActivity();
			}
			// Repeat until no armies remaining for player.
		}
		printTerritories(false, true);
		
		// 2. Attacking
		Attack currentAttack = new Attack(currentPlayer);
		while(currentAttack.continueAttack) {
			// Prompt player to choose a territory to attack from
			Territory attackingTerritory = botchooseAttackingTerritory();
			if(attackingTerritory == null) {
				bot.sendplayer("No attacking territory selected, ending attack step.");
				System.out.println("No attacking territory selected, ending attack step.");
				currentAttack.continueAttack = false;
				break;
			}
			bot.sendplayer("Attacking from " + attackingTerritory.getTerritoryName());
			System.out.println("Attacking from " + attackingTerritory.getTerritoryName());
			// Prompt player to choose a territory to attack
			Territory defendingTerritory = botchooseTerritoryToAttack(attackingTerritory);
			if(defendingTerritory == null) {
				bot.sendplayer("No defending territory selected, ending attack step.");
				System.out.println("No defending territory selected, ending attack step.");
				currentAttack.continueAttack = false;
				break;
			}
			bot.sendplayer("Defending from " + defendingTerritory.getTerritoryName());
			System.out.println("Defending from " + defendingTerritory.getTerritoryName());
			
			
			// Continue until player decides to end attack phase
			if(this.useAPIs == true) {
				s3.pa.attack(attackingTerritory, defendingTerritory);
				s3.logPlayerActivity();
			}
			currentAttack = botattack(currentAttack, attackingTerritory, defendingTerritory);
		}
		if(currentAttack.receiveCard) {
			Card tempCard = drawCard();
			currentAttack.attackingPlayer.addCard(tempCard);
			System.out.println(currentAttack.attackingPlayer.getName() + " has received one Risk card");
			if(this.useAPIs == true) {
				s3.pa.receiveRiskCard(currentPlayer, tempCard);
				s3.logPlayerActivity();
			}
		}
		
		// 3. Fortifying
		botfortify();
		
		// Change currentPlayerIndex to next player
		incrementCurrentPlayerIndex();
		
		//post the number of territories conquered by each player on Twitter 
		//after each turn and at the end of the game
		if(this.useAPIs) {
			Twitter4j newTwitter = new Twitter4j();
			newTwitter.PostTwitter(" Player " + currentPlayer.getName() + " have  conquered " + playerTerritoriesCount(currentPlayer) + " territories");
		}
		
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
		//System.out.println("Continent " + continentIndex + " created!");
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
		//System.out.println("Continent " + continentIndex + " created!");
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
		//System.out.println("Continent " + continentIndex + " created!");
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
		//System.out.println("Continent " + continentIndex + " created!");
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
		//System.out.println("Continent " + continentIndex + " created!");
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
		//System.out.println("Continent " + continentIndex + " created!");
		continents.get(continentIndex).printTerritories();

		//printTerritories(true, false);
		//System.out.println("Territories size: " + this.territories.size());
		//System.out.println(this.territories.get(0).getTerritoryName());
		
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
