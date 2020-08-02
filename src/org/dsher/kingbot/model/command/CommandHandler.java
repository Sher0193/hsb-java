package org.dsher.kingbot.model.command;

import org.dsher.kingbot.Bot;
import org.dsher.kingbot.model.command.impl.Help;
import org.dsher.kingbot.model.command.impl.Ping;
import org.dsher.kingbot.model.command.impl.Roll;
import org.dsher.kingbot.model.command.impl.Score;
import org.dsher.kingbot.model.command.impl.Scoreboard;
import org.dsher.kingbot.model.command.impl.Stopwatch;
import org.dsher.kingbot.model.command.impl.Uptime;
import org.dsher.kingbot.model.command.impl.Werewolf;
import org.dsher.kingbot.model.content.werewolf.Game;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandHandler {

	private static final Command[] COMMANDS = {
			new Ping(),
			new Score(),
			new Scoreboard(),
			new Help(),
			new Roll(),
			new Stopwatch(),
			new Uptime(),
			new Werewolf()
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
		// ONUW Handling
		for (Game game : Bot.getBotInstance().getWerewolfHandler().getGamesByUser(message.getAuthor())) {
			if (game != null) {
				game.acceptPrivateInput(message.getAuthor(), message.getContentRaw(), message.getChannel());
			}
		}
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
