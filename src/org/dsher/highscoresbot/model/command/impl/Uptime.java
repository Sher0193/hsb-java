package org.dsher.highscoresbot.model.command.impl;

import org.dsher.highscoresbot.Bot;
import org.dsher.highscoresbot.model.command.Command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Uptime extends Command {
	
	public Uptime() {
		helpEntry = "Outputs how long " + Bot.getBotInstance().getName() + " has been online.";
		commands = new String[] {"uptime", "up"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		long curTime = System.currentTimeMillis();
	    long uptime = curTime - Bot.getBotInstance().getLaunchTime();

	    int days = (int)Math.floor(uptime / 86400000);
	    uptime %= 86400000;

	    int hours = (int)Math.floor(uptime / 3600000);
	    uptime %= 3600000;

	    int minutes = (int)Math.floor(uptime / 60000);

	    //TODO: format method in utils
	    channel.sendMessage(Bot.getBotInstance().getName() + " has been online for " + (days > 0 ? (days + " day" + (days == 1 ? "" : "s") + ", ") : "") + (hours > 0 ? (hours + " hour" + (hours == 1 ? "" : "s") + ", ") : "") + minutes + " minute" + (minutes == 1 ? ("") : "s") + ".").queue();
	    return true;
	}
	

}
