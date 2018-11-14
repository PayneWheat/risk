package main.java.game;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Twitter4j {
	public void PostTwitter(String MSG) {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.updateStatus(MSG);
			System.out.println("Successfully updated the status in Twitter.");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
