package org.dsher.kingbot.model.command.impl;

import org.dsher.kingbot.Bot;
import org.dsher.kingbot.model.command.Command;
import org.dsher.kingbot.model.content.scoreboard.ScoreboardHandler;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Scoreboard extends Command {

	public Scoreboard() {
		helpEntry = "**BASICS**\n\"!scoreboard\" displays the current score for this channel's scoreboard.\n**CREATING A SCOREBOARD FOR THE CHANNEL**\nSimply use the command \"!scoreboard create\" to create a scoreboard. If one exists already, this command will overwrite the previous board.\n**CLEARING THE SCOREBOARD**\nThe current scoreboard may be erased from this channel with \"!scoreboard clear\".";
		commands = new String[] {"scoreboard", "sb"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		if (author == null) {
			return false;
		}
		ScoreboardHandler sh = Bot.getBotInstance().getScoreboardHandler();
		if (args.length > 0) {
			if (args[0].equals("create")) {
				/*if (th.getTriviaById(channel.id) !== null) {
	            channel.send("Scoreboard in use by an active Trivia game. Please stop the trivia game with !triviaend first to add a new scoreboard.");
	            return;
	        }*/
				sh.addScoreboard(channel.getId());
				if (sh.getScoreboardById(channel.getId()) != null) {
					channel.sendMessage("Created scoreboard for channel " + channel.getName()).queue();;
					sh.saveScoreboards();
					return true;
				} else {
					channel.sendMessage("Could not create scoreboard.").queue();;
					return false;
				}
			} else if (args[0].equals("clear")) {
				/*if (th.getTriviaById(channel.id) !== null) {
	            channel.send("Scoreboard in use by an active Trivia game. Please stop the trivia game with !triviaend to remove the current scoreboard.");
	            return;
	        }*/
				if (sh.removeScoreboard(channel.getId())) {
					channel.sendMessage("Removed scoreboard for channel " + channel.getName()).queue();;
					sh.saveScoreboards();
					return true;
				} else {
					channel.sendMessage("Could not remove scoreboard.").queue();;
					return false;
				}
			} 
		} else {
			org.dsher.kingbot.model.content.scoreboard.Scoreboard scoreboard = sh.getScoreboardById(channel.getId());
			if (scoreboard != null) {
				channel.sendMessage(scoreboard.buildScoreboard(false, "")).queue();;
				return true;
			} else {
				channel.sendMessage("Could not find scoreboard.").queue();;
				return false;
			}
		}
		return false;
	}

}
