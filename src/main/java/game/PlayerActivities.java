package main.java.game;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Month;

public class PlayerActivities {
	private String activityOutput;
	public PlayerActivities() {
		activityOutput = "";
	}
	public void startGame(ArrayList<Player> players) {
		LocalDateTime currentTime = LocalDateTime.now();
		String playersString = "";
		for(int i = 0; i < players.size() - 1; i++) {
			playersString += players.get(i).getName() + " (" + players.get(i).getColor() + "),";
		}
		playersString += players.get(players.size() - 1).getName() + " (" + players.get(players.size() - 1).getColor() + "),";
		activityOutput = "Game started at " + currentTime;
		activityOutput = "\nwith Players: " + playersString;
	}
	public void receiveArmies(Player p, int armiesReceived) {
		activityOutput = p.getName() + " received " + armiesReceived + " armies";
	}
	public void placeArmies(Player p, Territory t, int armiesPlaced) {
		activityOutput = p.getName() + " placed " + armiesPlaced + " armies on " + t.getTerritoryName();
	}
	public void attack(Territory attackingTerritory, Territory defendingTerritory) {
		activityOutput = attackingTerritory.getPlayer().getName() + "(" + attackingTerritory.getTerritoryName() + ") is attacking " + defendingTerritory.getPlayer().getName() + "("+ defendingTerritory.getTerritoryName() +")";
	}
	public void diceRoll(Territory attackingTerritory, Territory defendingTerritory, ArrayList<Dice> attackingDice, ArrayList<Dice> defendingDice) {
		String attackingRolls = "";
		String defendingRolls = "";
		for(int i = 0; i < attackingDice.size(); i++) {
			attackingRolls += attackingDice.get(i).getCurrentValue() + " ";
		}
		for(int i = 0; i < defendingDice.size(); i++) {
			defendingRolls += defendingDice.get(i).getCurrentValue() + " ";
		}
		activityOutput = attackingTerritory.getPlayer().getName() + " rolled " + attackingRolls + "; " + defendingTerritory.getPlayer().getName() + " rolled " + defendingRolls;
	}
	public void endAttack(Attack attack) {
		activityOutput = attack.attackingPlayer + " has ended their attack";
	}
	public void playerDefeated(Player p) {
		activityOutput = p.getName() + " has been eliminated";
	}
	public void playerWins(Player p) {
		activityOutput = p.getName() + " has won the game";
	}
	public void fortify(Player p, Territory fromTerritory, Territory toTerritory, int armies) {
		activityOutput = p.getName() + " has fortified " + toTerritory.getTerritoryName() + " with " + armies + " armies from " + fromTerritory.getTerritoryName();
	}
	public void receiveRiskCard(Player p, Card c) {
		activityOutput = p.getName() + " has received a risk card: " + c.getCardTypeName() + " " + c.getTerritoryName();
	}
	public void turnInRiskCards(Player p, ArrayList<Card> cards, int bonusArmies) {
		String cardsString = "";
		for(int i = 0; i < cards.size() - 1; i++) {
			cardsString += cards.get(i).getTerritoryName() + "(" + cards.get(i).getCardTypeName() +"),";
		}
		cardsString += cards.get(cards.size() - 1).getTerritoryName() + "(" + cards.get(cards.size() - 1).getCardTypeName() +")";
		activityOutput = p.getName() + " has received " + bonusArmies + " bonus armies from turning in a set of Risk Cards: " + cardsString;
	}
	public void transferCredits(Player p, String r, String c) {
		activityOutput = p.getName() + " has transferred " + c + " credits to " + r;
	}
	public void undoMove(Player p) {
		activityOutput = p.getName() + " has undone a move";
	}
	public void buyCredits(Player p, int credits) {
		activityOutput = p.getName() + " has bought " + credits + " credit(s)";
	}
	public void buyCards(Player p) {
		activityOutput = p.getName() + " has bought a Risk Card";
	}
	public void loseArmy(Player p) {
		activityOutput = p.getName() + " has lost an army";
	}
	public void conquerTerritory(Player p, Player d, Territory t) {
		activityOutput = p.getName() + " has conquered " + t.getTerritoryName() + " from " + d.getName();
	}
	public void print() {
		System.out.println(activityOutput);
	}
	public String getActivityOutput() {
		return activityOutput + "\n";
	}
}
