package org.dsher.highscoresbot.model.content.scoreboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ScoreboardHandler {

	private static final String SAVE_FILE = "./data/sb_save.ser";

	private ArrayList<Scoreboard> scoreboards = new ArrayList<>();

	public ScoreboardHandler() {}

	public Scoreboard getScoreboardById(String id) {
		for (Scoreboard sb : scoreboards) {
			if (sb.getChannelId().equals(id)) {
				return sb;
			}
		}
		return null;
	}

	public boolean addScoreboard(String channelId) {
		Scoreboard existing = getScoreboardById(channelId);
		if (existing != null) {
			removeScoreboard(existing);
		}
		return addScoreboard(new Scoreboard(channelId));
	}

	public boolean addScoreboard(Scoreboard scoreboard) {
		return this.scoreboards.add(scoreboard);
	}

	public boolean removeScoreboard(String channelId) {
		Scoreboard existing = getScoreboardById(channelId);
		return removeScoreboard(existing);       
	}

	public boolean removeScoreboard(Scoreboard scoreboard) {
		return this.scoreboards.remove(scoreboard);
	}

	public void saveScoreboards() {
		try {
			File saveFile = new File(SAVE_FILE);
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(saveFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for (Scoreboard sb : scoreboards) {
				oos.writeObject(sb);
			}
			oos.close();
			fos.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

}
