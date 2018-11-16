package main.java.game;
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
	public Territory() {
		this.name = "";
		this.continentIndex = (byte)-1;
		hasPlayer = false;
		armyCount = 0;
	}
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
	public void setPlayer(Player player) {
		occupyingPlayer = player;
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
			case 0:
				continent = "North America";
				break;
			case 1:
				continent = "South America";
				break;
			case 2:
				continent = "Europe";
				break;
			case 3:
				continent = "Africa";
				break;
			case 4:
				continent = "Asia";
				break;
			case 5:
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
	public ArrayList<Territory> getAdjacentTerritories(boolean onlyOtherPlayers, boolean onlyPlayerTerritories, boolean print) {
		ArrayList<Territory> tempList = new ArrayList<Territory>();
		for(int i = 0; i < adjacentTerritories.size(); i++) {
			Territory tempTerritory = adjacentTerritories.get(i);
			if(onlyOtherPlayers) {
				if(tempTerritory.getPlayer() != occupyingPlayer) {
					tempList.add(tempTerritory);
					if(print) {
						if(tempTerritory.getPlayer() == null)
							System.out.println(tempTerritory.getTerritoryName() + "(UNOCCUPIED):" + tempTerritory.getArmyCount() + " armies");
						else
							System.out.println(tempTerritory.getTerritoryName() + "(" + tempTerritory.getPlayer().getName() + "):" + tempTerritory.getArmyCount() + " armies");
					}
				}
			} else {
				if(onlyPlayerTerritories) {
					if(tempTerritory.getPlayer() == occupyingPlayer) {
						tempList.add(tempTerritory);
						if(print)
							System.out.println(tempTerritory.getTerritoryName() + ":" + tempTerritory.getArmyCount() + " armies");
					}
				} else {
					tempList.add(tempTerritory);
					if(print) {
						if(tempTerritory.getPlayer() == null)
							System.out.println(tempTerritory.getTerritoryName() + "(UNOCCUPIED):" + tempTerritory.getArmyCount() + " armies");
						else
							System.out.println(tempTerritory.getTerritoryName() + "(" + tempTerritory.getPlayer().getName() + "):" + tempTerritory.getArmyCount() + " armies");
					}
				}
			}
		}
		return tempList;
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
