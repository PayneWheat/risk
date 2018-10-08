package main.java.game;

public class Attack {
	public boolean continueAttack;
	public boolean receiveCard;
	public Player attackingPlayer;
	public Attack(Player attackingPlayer) {
		this.attackingPlayer = attackingPlayer;
		continueAttack = true;
		receiveCard = false;
	}
}
