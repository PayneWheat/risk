import java.util.*;

public class Territory {
	public String name;
	public byte continentIndex;
	ArrayList<Territory> adjacentTerritories = new ArrayList<Territory>();
	Player occupyingPlayer;
	int armyCount;
	/*
	 * 1: North America (5)
	 * 2: South America (2)
	 * 3. Europe (5)
	 * 4. Africa (3)
	 * 5. Asia (7)
	 * 6. Australia (2)
	 */
	public Territory(String name, byte continentIndex) {
		this.name = name;
		this.continentIndex = continentIndex;
	}
	public void setAdjacentTerritory(Territory territory) {
		adjacentTerritories.add(territory);
	}
	public ArrayList<Territory> getAdjacentTerritories() {
		return adjacentTerritories;
	}
}
