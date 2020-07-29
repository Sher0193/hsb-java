package org.dsher.kingbot.model.command.impl;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.dsher.kingbot.Bot;
import org.dsher.kingbot.model.command.Command;
import org.dsher.kingbot.model.content.scoreboard.Scoreboard;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Score extends Command {

	public Score() {
		helpEntry = "\"**BASICS**\\nUse \\\"!score\\\" to add points for a list of users to the channel's scoreboard. A scoreboard must have been created for the channel with \\\"!scoreboard create\\\".\\n\\\"!score\\\" requires a comma-separated list of names, such as \\\"!score Crosby, Stills, Nash, Young\\\".\\n**MULTIPLE POINT VALUES**\\nBy default, \\\"!score\\\" will award one point. In addition, \\\"!half\\\" will award 0.5 points, \\\"!penalty\\\" will award -1 point, and \\\"!penaltyhalf\\\" will award -0.5 points.\\nYou may precede a list of names with a number and colon to award that value to the following list, such as \\\"!score 5: Crosby, Stills, Nash, Young\\\", which will award 5 points. You may mix point values in a single command, such as \\\"!score 5: Crosby, Stills 10: Nash, Young\\\", which will award 5 to Crosby and Stills, then 10 to Nash and Young.\"";
		commands = new String[] {"score", "half", "penalty", "penaltyhalf", "sc"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		if (author == null) {
			return false;
		}
		if (args[0].isEmpty()) {
			channel.sendMessage("Please enter a list of names separated by commas e.g. \"!score Crosby, Stills, Nash, Young\"").queue();;
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
				if (isNumeric(args[i].replace(":", "")) || i == 0) {
					String[] users;
					Double amt = 1.0;
					if (isNumeric(args[i].replace(":", ""))) {
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
						msg += newArgs[j].trim();
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
				channel.sendMessage(sb.buildScoreboard(false, changeMsg)).queue();;
			}
			/*arrs = new ArrayList<>();
	        amts = new Array();
	        for (let i = args.length - 1; i >= 0; i--) {
	            if (args[i].charAt(args[i].length - 1) === ":" || !isNaN(args[i])) { // add passed number to amts, number: is legacy
	                if (args[i].charAt(args[i].length - 1) == ":") {
	                    amtstring = args[i].slice(0, -1);
	                } else {
	                    amtstring = args[i];
	                }
	                if (!isNaN(amtstring) && amtstring !== "") {
	                    amts.push(parseFloat(amtstring));
	                    arrs.push(args.splice(i + 1, args.length - i));
	                    args.splice(i, 1);
	                } else {
	                    return;
	                }
	            } else if (i === 0) {
	                amts.push(1);
	                arrs.push(args);
	            }
	        }*/
		}
		/*var scoreboard = sh.getScoreboardById(channel.id);
	    var success = "";
	    for (let i = 0; i < arrs.length; i++) {
	        var amt = amts[i];
	        amt = command === "penalty" ? amt * -1 : command === "half" ? amt * 0.5 : command === "penaltyhalf" ? amt * -0.5 : amt;
	        newargs = arrs[i].join(" ").split(",");
	        var points = amt === 1 ? "point" : "points";
	        var next = "Added " + amt + " " + points + " for ";
	        let j, k;
	        for (j = 0, k = 0; j < newargs.length; j++) {
	            if (newargs[j] === "")
	                continue;
	            if (j != 0)
	                next += ", ";
	            next += newargs[j].trim();
	            k++;
	        }
	        if (k === 0)
	            continue;
	        next += ".\n";
	        success += next;
	        if (scoreboard !== null) {
	            scoreboard.addScores(newargs, amt);
	            sh.saveScoreboards();

	        } else {
	            channel.sendMessage("Could not find scoreboard.");
	            return;
	        }

	    }
	    if (success != "") {
	        channel.send(scoreboard.buildScoreboard(false, success));
	    }
		 */
		return true;
	}

	private boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
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
