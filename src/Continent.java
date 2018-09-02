
public class Continent {
	String name;
	public Territory[] territories;
	int bonusArmies;
	/*At the beginning of each turn, players recieve bonus armies for each continent they control.
	Each continent has a different number of bonus armies a player can recieve. 
	Asia: 7
	North America: 5
	Europe: 5
	Africa: 3
	South America: 2
	Australia: 2*/
	
	public Continent(String name, int bonusArmies, Territory[] territories) {
		this.name = name;
		this.bonusArmies = bonusArmies;
		this.territories = territories;
	}
	public String getContinentName(){
		return name;
	}
	public int getBonusArmies(){
		return bonusArmies;
	}
	public Territory[] getTerritories()
	{
		return territories;
	}
	
}
