package org.dsher.highscoresbot.model.command;

import org.dsher.highscoresbot.model.command.impl.Help;
import org.dsher.highscoresbot.model.command.impl.Ping;
import org.dsher.highscoresbot.model.command.impl.Roll;
import org.dsher.highscoresbot.model.command.impl.Stopwatch;
import org.dsher.highscoresbot.model.command.impl.Uptime;
import org.dsher.highscoresbot.model.command.impl.Vsg;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandHandler {

	private static final Command[] COMMANDS = {
			new Ping(),
			new Help(),
			new Roll(),
			new Stopwatch(),
			new Uptime(),
			new Vsg()
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

	public static Command[] getCommands() {
		return COMMANDS;
	}

	public static boolean handleUnprefixedCommand(Message message) {
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
