package org.dsher.highscoresbot.model.content.scoreboard;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import org.dsher.highscoresbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Scoreboard implements Serializable {

	private static final long serialVersionUID = -4058883818810083618L;

	private HashMap<String, Double> scores = new HashMap<>();

	private String channel = "";

	public Scoreboard(String channel) {
		setChannelId(channel);
	}

	public void setChannelId(String channel) {
		this.channel = channel;
	}	
	public String getChannelId() {
		return channel;
	}

	public void setScores(HashMap<String, Double> scores) {
		this.scores = scores;
	}

	public boolean addScore(String user, double amt) {
		// Try to find existing user
		String toFind = user.trim().toLowerCase();
		if (this.scores.get(toFind) != null) {
			this.scores.replace(toFind, this.scores.get(toFind) + amt < 0 ? 0 : this.scores.get(toFind) + amt);
		} else {		
			this.scores.put(toFind, amt);
		}
		return true;
	}

	public boolean addScores(String[] users, double amt) {
		for (String u : users) {
			if (!addScore(u, amt))
				return false;
		}
		return true;
	}

	public MessageEmbed buildScoreboard(boolean end, String msg) {
		if (!this.channel.isEmpty()) {
			EmbedBuilder builder = new EmbedBuilder()
					.setTitle(end ? "Final Score:" : "Current Score")
					.setColor(0xf20963)
					.setDescription(this.scoresToString(end));
			if (!msg.isEmpty()) {
				builder.addField(new Field("Change:", msg, false));
			}
			return builder.build();
		}
		return null;
	}

	private String scoresToString(boolean end) {
		String string = "";

		Object[] array = this.scores.keySet().toArray();

		String[] users = Arrays.copyOf(array, array.length, String[].class);


		users = sort(users);

		for (int i = 0, j = 1; i < users.length; i++) {
			if (this.scores.get(users[i]) > 0 && users[i] != "") {
				if (i > 0 && this.scores.get(users[i]) < this.scores.get(users[i - 1]))
					j = i + 1;
				String score = new DecimalFormat("0.####").format(this.scores.get(users[i]));
				string += (j == 1 ? ":first_place:" : j == 2 ? ":second_place:" : j == 3 ? ":third_place:" : ("*" + j + Utils.getOrdinalSuffix(j) + "*")) + " **" + Utils.capitalize(users[i]) + "**: " + score + "\n";
			}
		}
		return string;
	}

	private String[] sort(String[] arr) {
		var len = arr.length;
		for (var i = 0; i < len; i++) {
			for (var j = 0; j < len - i - 1; j++) {
				if (this.scores.get(arr[j]) < this.scores.get(arr[j + 1])) {
					// swap users
					String tempEle = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = tempEle;
				}
			}
		}
		return arr;
	}

}
