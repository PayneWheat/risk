package main.java.game;
import java.util.*;

public class Continent {
	String name;
	public ArrayList<Territory> territories = new ArrayList<Territory>();
	int bonusArmies;
	/*At the beginning of each turn, players recieve bonus armies for each continent they control.
	Each continent has a different number of bonus armies a player can recieve. 
	Asia: 7
	North America: 5
	Europe: 5
	Africa: 3
	South America: 2
	Australia: 2*/
	
	public Continent(String name, int bonusArmies) {
		this.name = name;
		this.bonusArmies = bonusArmies;
		//this.territories = territories;
	}
	public String getContinentName(){
		return name;
	}
	public int getBonusArmies(){
		return bonusArmies;
	}
	public ArrayList<Territory> getTerritories()
	{
		return territories;
	}
	public void addToContinent(Territory territory) {
		this.territories.add(territory);
	}
	public void printTerritories() {
		Player tempPlayer = bonusArmiesAwarded();
		if(tempPlayer != null) {
			System.out.println("Territories in " + this.name + "(controlled by " + tempPlayer.name + "): ");
		} else {
			System.out.println("Territories in " + this.name + ": ");
		}
		for(int i = 0; i < territories.size(); i++) {
			System.out.print(territories.get(i).name + ", ");
		}
		System.out.println();
	}
	public Player bonusArmiesAwarded() {
		Player tempPlayer = territories.get(0).occupyingPlayer;
		for(int i = 1; i < territories.size(); i++) {
			if(tempPlayer != territories.get(i).occupyingPlayer) {
				tempPlayer = null;
				break;
			}
		}
		return tempPlayer;
	}
}
