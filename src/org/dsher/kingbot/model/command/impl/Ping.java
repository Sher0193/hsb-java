package org.dsher.kingbot.model.command.impl;

import org.dsher.kingbot.Bot;
import org.dsher.kingbot.model.command.Command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Ping extends Command {

	public Ping() {
		helpEntry = Bot.getBotInstance().getName() + " will measure and output the time it takes in milliseconds to receive a response from its server.";
		commands = new String[] {"ping"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		long time = System.currentTimeMillis();
		channel.sendMessage("Pong!") /* => RestAction<Message> */
		.queue(response /* => Message */ -> {
			response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
		});
		return true;
	}

}
