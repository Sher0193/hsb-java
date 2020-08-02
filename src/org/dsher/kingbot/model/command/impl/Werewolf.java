package org.dsher.kingbot.model.command.impl;

import org.dsher.kingbot.Bot;
import org.dsher.kingbot.model.command.Command;
import org.dsher.kingbot.model.content.werewolf.Game;
import org.dsher.kingbot.model.content.werewolf.Player;
import org.dsher.kingbot.model.content.werewolf.WerewolfHandler;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class Werewolf extends Command {

	public Werewolf() {
		helpEntry = "Under construction.";
		commands = new String[] {"werewolf", "ww"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		if (args.length > 0) {
			WerewolfHandler wwh = Bot.getBotInstance().getWerewolfHandler();
			Game game = wwh.getGameById(channel.getId());
			switch (args[0].toLowerCase()) {
				case "create":
					if (wwh.addGame(channel)) {
						channel.sendMessage("Started a new One Night Ultimate Werewolf game in channel " + channel.getName() + ".").queue();
						return true;
					}
					channel.sendMessage("Could not add new game.").queue();
					return false;
				case "start":
					if (game == null) 
						return false;
					org.dsher.kingbot.model.content.werewolf.Werewolf ww = game.getWerewolfGame();
					if (ww != null && ww.getPlayers().size() >= 3) {
						Thread thread = new Thread(game);
						thread.start();
					}
					return false;
				case "end":
					if (wwh.removeGame(channel.getId())) {
						channel.sendMessage("Removed One Night Ultimate Werewolf game from channel " + channel.getName() + ".").queue();
						return true;
					}
					channel.sendMessage("Could not remove game.").queue();
					return false;
				case "join":
					if (game == null)
						return false;
					if (game.isStarted()) {
						channel.sendMessage("Game in progress, cannot join.").queue();
					}
					if (game.getWerewolfGame().addPlayer(author)) {
						channel.sendMessage("Successfully added " + author.getName() + " to the game.").queue();
						return true;
					}
					return false;
				case "leave":
					if (game == null)
						return false;
					if (game.isStarted()) {
						channel.sendMessage("Game in progress, cannot leave.").queue();
					}
					if (game.getWerewolfGame().removePlayer(author)) {
						channel.sendMessage(author.getName() + " successfully left the game.").queue();
						return true;
					}
					return false;
				case "players":
					if (channel.getType() == ChannelType.PRIVATE) {
						String gamesPlayerList = "";
						for (Game g : wwh.getGamesByUser(author)) {
							if (g.getWerewolfGame().isUserInGame(author)) {
								gamesPlayerList += "**#" + g.getChannel().getName() + "**\n" + usersInGameToString(g) + "\n";

							}
						}
						if (gamesPlayerList.isEmpty()) {
							gamesPlayerList = "You are not currently in any active One Night Ultimate Werewolf games.";
						}
						channel.sendMessage(gamesPlayerList).queue();
						return true;
					} else {
						if (game == null)
							return false;
						channel.sendMessage(usersInGameToString(game)).queue();
						return true;
					}
				case "accuse":
					if (args.length >= 2) {
						String accusation = "";
						for (int i = 1; i < args.length; i++) {
							accusation += args[i] + " ";
						}
						if (game.getWerewolfGame().getPlayerByUsername(accusation.trim().toLowerCase()).getRole() == org.dsher.kingbot.model.content.werewolf.Werewolf.Role.HUNTER) {
							// Hunter handling
							channel.sendMessage(author.getName() + " was the hunter! That means they can now choose to accuse whomever else they wish.").queue();
						} else {
							MessageEmbed result = game.buildResults(accusation.trim());
							if (result != null) {
								channel.sendMessage(result).queue();
								wwh.removeGame(channel.getId());
							} else {
								channel.sendMessage("No player found by that name.").queue();;
							}
						}
						return true;
					}
					return false;
			}
		}
		return false;
	}

	private String usersInGameToString(Game game) {
		String playerList = "";
		for (Player player : game.getWerewolfGame().getPlayers()) {
			playerList += player.getUser().getName() + ", ";
		}
		if (playerList.isEmpty()) {
			playerList = "No players found.";
		} else {
			playerList = playerList.substring(0, playerList.length() - 2) + ".";
		}
		return playerList;
	}

}
