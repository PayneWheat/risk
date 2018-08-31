import javax.swing.JOptionPane;

public class Main {
	public static void main(String[] args) {
		// Risk
		System.out.println("We will put Risk here.");
		
		// Create board object
		//Board board = new Board();
		
		// Prompt for number of players (here in main or in board constructor/method?)
		Dice d = new Dice(); 
		Player[]players = new Player[6];
		int numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the number of players (2,3,4,5,6): "));
		
		// Initial number of armies for each player is determined
		int initialArmies = 0;
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
		
		// Each player chooses their color and enters their name (here in main or in player constructor/method?)
		for(int i = 0; i < numOfPlayers; i++)
		{
			String name = JOptionPane.showInputDialog("Please enter the name of player " + (i+1) + ":");
			String color = JOptionPane.showInputDialog("Please enter a color for player " + (i+1) + ":");
			int diceValue = d.getDiceValue();
			players[i] = new Player(name, color, initialArmies, diceValue, 0, 0);
		}
		
		// Risk cards are generated and shuffled
		
		// Players begin initial army placement and continue until all armies have been placed
		
		// Players take turns until one player controls the whole board
		//	-- Each turn consists of three parts:
		//	-- 1. Placing new troops
		//	-- 2. Attacking
		//	-- 3. Fortifying
		
	}
}
