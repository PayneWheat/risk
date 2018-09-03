import java.util.*;

public class Territory {
	public String name;
	public byte continentIndex;
	ArrayList<Territory> adjacentTerritories = new ArrayList<Territory>();
	Player occupyingPlayer;
	private boolean hasPlayer;
	private int armyCount;
	private int x;
	private int y;

	public Territory(String name, byte continentIndex) {
		this.name = name;
		this.continentIndex = continentIndex;
		hasPlayer = false;
		armyCount = 0;	
	}
	public String getTerritoryName(){
		return name;
	}
	public byte getcontinentIndex(){
		return continentIndex;
	}
	public String getContinent() {
		/*
		 * 1: North America (5)
		 * 2: South America (2)
		 * 3. Europe (5)
		 * 4. Africa (3)
		 * 5. Asia (7)
		 * 6. Australia (2)
		 */
		String continent;
		switch((int)continentIndex)
		{
			case 1:
				continent = "North America";
				break;
			case 2:
				continent = "South America";
				break;
			case 3:
				continent = "Europe";
				break;
			case 4:
				continent = "Africa";
				break;
			case 5:
				continent = "Asia";
				break;
			case 6:
				continent = "Australia";
				break;
			default:
				continent = "NONE";
				break;
		}
		return continent;
	}
	public void setOccupant(Player occupyingPlayer){
		hasPlayer = true;
		this.occupyingPlayer = occupyingPlayer;
	}
	public Player getPlayer(){
		return occupyingPlayer;
	}
	public boolean isOccupied(){
		return hasPlayer;
	}
	public void setArmyCount(int armies){
		armyCount = armies;
	}
	public void incrementArmy(int armies){
		armyCount = armyCount + armies; 
	}
	public void decrementArmy(int armies){
		armyCount = armyCount - armies;
	}
	public int getArmyCount() {
		return armyCount;
	}
	public void setAdjacentTerritory(Territory territory) {
		adjacentTerritories.add(territory);
	}
	public ArrayList<Territory> getAdjacentTerritories() {
		return adjacentTerritories;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setX(int X) {
		this.x = X;
	}
	public void setY(int Y) {
		this.y = Y;
	}
}
