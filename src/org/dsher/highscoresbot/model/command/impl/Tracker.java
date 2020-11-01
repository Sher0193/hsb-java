package org.dsher.highscoresbot.model.command.impl;

import org.dsher.highscoresbot.Bot;
import org.dsher.highscoresbot.model.command.Command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Tracker extends Command {
	
	public Tracker() {
		String name = Bot.getBotInstance().getName();
		helpEntry = name + " will output the first page of the vidyascape.org tracker leaderboards for the requested time frame. Valid times are ``daily``, ``weekly``, and ``monthly``";
		commands = new String[] {"tracker"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		return false;
	}

}
