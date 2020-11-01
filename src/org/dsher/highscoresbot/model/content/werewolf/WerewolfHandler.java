package org.dsher.kingbot.model.content.werewolf;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class WerewolfHandler {

	private ArrayList<Game> games = new ArrayList<>();
	
	public WerewolfHandler() {}
	
	public Game getGameById(String id) {
		for (Game game : games) {
			if (game.getChannel().getId().equals(id)) {
				return game;
			}
		}
		return null;
	}
	
	public boolean addGame(MessageChannel channel) {
		Game existing = getGameById(channel.getId());
		if (existing != null) {
			removeGame(existing);
		}
		return addGame(new Game(channel));
	}
	public boolean addGame(Game game) {
		return games.add(game);
	}
	
	public boolean removeGame(String channelId) {
		Game existing = getGameById(channelId);
		return removeGame(existing);
	}
	public boolean removeGame(Game game) {
		if (game == null)
			return false;
		game.kill();
		return games.remove(game);
	}
	
	public Game[] getGamesByUser(User user) {
		int count = 0;
		ArrayList<Game> gamesList = new ArrayList<>();
		for (Game game : games) {
			if (game.getWerewolfGame().isUserInGame(user)) {
				count++;
				gamesList.add(game);
			}
		}
		Game[] games = new Game[count];
		for (int i = 0; i < games.length; i++) {
			games[i] = gamesList.get(i);
		}
		return games;
	}
	
}
