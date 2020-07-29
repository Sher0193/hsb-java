package org.dsher.kingbot.model.command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public abstract class Command {
	
	protected String helpEntry;
	protected String[] commands;

	public abstract boolean execute(String command, String[] args, MessageChannel channel, User author);
	
	public String getHelpEntry() {
		return helpEntry;
	}
	
	public String[] getCommands() {
		return commands;
	}
	
	public boolean isMatchingCommand(String command) {
		for (int i = 0; i < commands.length; i++) {
			if (command.equals(commands[i]))
				return true;
		}
		return false;
	}
	
}
