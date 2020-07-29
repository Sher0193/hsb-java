package org.dsher.kingbot.model.command.impl;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.dsher.kingbot.Bot;
import org.dsher.kingbot.model.command.Command;
import org.dsher.kingbot.model.content.scoreboard.Scoreboard;
import org.dsher.kingbot.utils.Utils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Score extends Command {

	public Score() {
		String prefix = Bot.getBotInstance().getPrefix();
		helpEntry = "**BASICS**\nUse \"" + prefix + "score\" to add points for a list of users to the channel's scoreboard. A scoreboard must have been created for the channel with \"" + prefix + "scoreboard create\".\n\"" + prefix + "score\" requires a comma-separated list of names, such as \"" + prefix + "score Crosby, Stills, Nash, Young\".\n**MULTIPLE POINT VALUES**\nBy default, \"" + prefix + "score\" will award one point. In addition, \"" + prefix + "half\" will award 0.5 points, \"" + prefix + "penalty\" will award -1 point, and \"" + prefix + "penaltyhalf\" will award -0.5 points.\nYou may precede a list of names with a number and colon to award that value to the following list, such as \"" + prefix + "score 5: Crosby, Stills, Nash, Young\", which will award 5 points. You may mix point values in a single command, such as \"" + prefix + "score 5: Crosby, Stills 10: Nash, Young\", which will award 5 to Crosby and Stills, then 10 to Nash and Young.\"";
		commands = new String[] {"score", "half", "penalty", "penaltyhalf", "sc"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		if (author == null) {
			return false;
		}
		if (args[0].isEmpty()) {
			channel.sendMessage("Please enter a list of names separated by commas e.g. \"" + Bot.getBotInstance().getPrefix() + "score Crosby, Stills, Nash, Young\"").queue();;
			return false;
		} else {
			Scoreboard sb = Bot.getBotInstance().getScoreboardHandler().getScoreboardById(channel.getId());
			if (sb == null) {
				channel.sendMessage("Could not find scoreboard.").queue();;
				return false;
			}
			double mod = command.equals("half") ? 0.5 : command.equals("penaltyhalf") ? -0.5 : command.equals("penalty") ? -1 : 1;
			String changeMsg = "";

			for (int i = args.length -1; i >= 0; i--) {
				if (Utils.isNumeric(args[i].replace(":", "")) || i == 0) {
					String[] users;
					Double amt = 1.0;
					if (Utils.isNumeric(args[i].replace(":", ""))) {
						String[][] arrays = spliceArray(args, i);
						amt = Double.parseDouble(arrays[1][0].replace(":", "")) * mod;
						users = spliceArray(arrays[1], 1)[1];

						args = arrays[0];
					} else {
						users = args;
					}
					String[] newArgs = Arrays.stream(users).collect(Collectors.joining(" ")).split(",");

					String points = amt == 1 ? "point" : "points";
					String score = new DecimalFormat("0.####").format(amt);

					String msg = "Added " + score + " " + points + " for ";

					int j, k;

					for (j = 0, k = 0; j < newArgs.length; j++) {
						if (newArgs[j].isEmpty())
							continue;
						if (j != 0)
							msg += ", ";
						msg += Utils.capitalize(newArgs[j].trim());
						k++;
					}					
					if (k == 0)
						continue;
					msg += ".\n";
					changeMsg += msg;

					sb.addScores(newArgs, amt);
					Bot.getBotInstance().getScoreboardHandler().saveScoreboards();

				}
			}
			if (!changeMsg.isEmpty()) {
				channel.sendMessage(sb.buildScoreboard(false, changeMsg)).queue();
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove all elements of an array starting at index s. Return new array in [0], removed elements in [1].
	 */
	private String[][] spliceArray(String[] array, int s) {
		return spliceArray(array, s, array.length - 1);
	}

	private String[][] spliceArray(String[] array, int s, int e) {
		String[] newArray = new String[(e - s) + 1];
		String[] oldArray = new String[s];
		for (int i = 0; i < e - s + 1; i++) {
			newArray[i] = array[i + s];
		}
		for (int i = 0; i < oldArray.length; i++) {
			oldArray[i] = array[i];
		}
		String[][] arrays = {oldArray, newArray};
		return arrays;
	}
}
