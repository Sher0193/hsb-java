package org.dsher.kingbot.model.content.scoreboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dsher.kingbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Scoreboard {
	
	private ArrayList<String> users = new ArrayList<>();
	private HashMap<String, Integer> scores = new HashMap<>();
	
	private int channel = -1;
	
	public Scoreboard(int channel) {
		this.channel = channel;
	}
	
	public int getChannelId(){
		return channel;
	}
	
	public boolean addScore(String user, int amt) {
		// Try to find existing user
		String toFind = user.trim();
		for (String u : this.users) {
			if (toFind.toLowerCase().equals(u.toLowerCase())) {
				// Found user, add score to existing in hashmap
				this.scores.replace(u, this.scores.get(u) + amt);
				return true;
			}
		}
		// Not found
		if (this.users.add(toFind)) {
			this.scores.put(toFind, amt);
			return true;
		}
		return false;
	}
	
	public boolean addScores(String[] users, int amt) {
		for (String u : users) {
			if (!addScore(u, amt))
				return false;
		}
		return true;
	}
	
	public MessageEmbed buildScoreboard(boolean end, String msg) {
        sort();
        if (this.channel > 0) {
        	EmbedBuilder builder = new EmbedBuilder()
        	.setTitle(end ? "Final Score:" : "Current Score")
        	.setColor(Color.YELLOW)
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
        for (int i = 0, j = 1; i < this.users.size(); i++) {
            if (this.scores.get(this.users.get(i)) > 0 && this.users.get(i) != "") {
                if (i > 0 && this.scores.get(this.users.get(i)) < this.scores.get(this.users.get(i - 1)))
                    j = i + 1;
                string += (j == 1 ? ":first_place:" : j == 2 ? ":second_place:" : j == 3 ? ":third_place:" : ("*" + Utils.getOrdinalSuffix(j) + "*")) + " **" + this.users.get(i) + "**: " + this.scores.get(this.users.get(i)) + "\n";
            }
        }
        return string;
    }
	
	private void sort() {
        var len = this.users.size();

        for (var i = 0; i < len; i++) {
            for (var j = 0; j < len - i - 1; j++) {
                if (this.scores.get(users.get(j)) < this.scores.get(users.get(j + 1))) {
                    // swap scores
                	Collections.swap(this.users, j, j + 1);
                }
            }
        }
    }

}
