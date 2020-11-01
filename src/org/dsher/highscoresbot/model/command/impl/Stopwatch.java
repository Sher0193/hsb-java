package org.dsher.highscoresbot.model.command.impl;

import org.dsher.highscoresbot.Bot;
import org.dsher.highscoresbot.model.command.Command;
import org.dsher.highscoresbot.utils.Utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Stopwatch extends Command {
	
	public Stopwatch() {
		String prefix = Bot.getBotInstance().getPrefix();
		helpEntry = "``" + prefix + "sw [number]`` or ``" + prefix + "stopwatch [number]`` will begin a countdown starting at the specified number, and decrementing by 5 every 5 seconds.";
		commands = new String[] {"stopwatch", "sw", "timer"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		if (args.length > 0 && Utils.isNumeric(args[0])) {
			int time = Integer.parseInt(args[0]);
			if (time % 5 == 0 && time <= 160) {
				channel.sendMessage(time + "").queue(response -> {
					Timer timer = new Timer(response, time);
					Thread thread = new Thread(timer);
					thread.start();
				});
				return true;
			}
		}
		channel.sendMessage("Time must be a denomination of 5, greater than 0").queue();
		return false;
	}
	
	private class Timer implements Runnable {
		
		private Message msg;
		private int time;
		
		private Timer(Message msg, int time) {
			this.msg = msg;
			this.time = time;
		}

		@Override
		public void run() {
			for (; time >= 0; time -= 5) {
				msg.editMessage(time + "").queue();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
		}
		
	}

}
