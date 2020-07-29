package org.dsher.kingbot.model.command;

import org.dsher.kingbot.model.command.impl.Ping;
import org.dsher.kingbot.model.command.impl.Score;
import org.dsher.kingbot.model.command.impl.Scoreboard;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandHandler {
	
	private static final Command[] COMMANDS = {
			new Ping(),
			new Score(),
			new Scoreboard()
	};
	
	/***
	 * Returns a command object with a matching command string.
	 * @param strCmd The string to match to a command.
	 * @return Command object.
	 */
	private static Command getCommand(String strCmd) {
		for (Command c : COMMANDS) {
			if (c.isMatchingCommand(strCmd))
				return c;
		}
		return null;
	}
	
	public static boolean handleUnprefixedCommand(String message) {
		return false;
	}
	
	public static boolean handleCommand(String command, String[] args, MessageChannel channel, User author) {
		Command cObj = getCommand(command);
		if (cObj != null) {
			cObj.execute(command, args, channel, author);
		}
		return false;
	}

}
