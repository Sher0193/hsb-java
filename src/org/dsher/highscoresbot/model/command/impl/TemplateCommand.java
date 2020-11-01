package org.dsher.highscoresbot.model.command.impl;

import org.dsher.highscoresbot.model.command.Command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class TemplateCommand extends Command {

	public TemplateCommand() {
		// add the help entry for this command here
		helpEntry = "A short description about how to use this command.";
		// add all text commands to access this command in this array
		commands = new String[] {"command", "com"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		channel.sendMessage("Command successfully executed.").queue();
		return true;
	}

}
