package org.dsher.highscoresbot.model.command.impl;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.dsher.highscoresbot.Bot;
import org.dsher.highscoresbot.model.command.Command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class Vsg extends Command {
	
	private static int REQUESTS = 0;
	private static final int MAX_REQUESTS = 5;

	public Vsg() {
		String name = Bot.getBotInstance().getName();
		helpEntry = name + " will output a link to the current /v/scape thread.";
		commands = new String[] {"vsg", "thread"};
	}

	@Override
	public boolean execute(String command, String[] args, MessageChannel channel, User author) {
		if (REQUESTS >= MAX_REQUESTS) {
			channel.sendMessage("Currently handling too many requests for this command...");
			return false;
		}
		try {
			REQUESTS++;
			URL url = new URL("https://a.4cdn.org/vg/catalog.json");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.connect();

			JsonParser parser = new JsonParser();
			JsonElement tree = parser.parse(new InputStreamReader(url.openStream(), "UTF-8"));

			if (tree.isJsonArray()) {
				JsonArray arr = tree.getAsJsonArray();

				for (int i = 0; i < arr.size(); i++) {
					if (arr.get(i).isJsonObject()) {
						JsonObject obj = arr.get(i).getAsJsonObject();

						if (obj.get("threads") != null && obj.get("threads").isJsonArray()) {
							JsonArray threads = obj.get("threads").getAsJsonArray();

							for (int j = 0; j < threads.size(); j++) {
								if (threads.get(i).isJsonObject()) {
									JsonObject thread = threads.get(j).getAsJsonObject();

									if (thread.get("sub") != null && thread.get("sub").getAsString().contains("/vsg/")) {
										String link = "https://boards.4chan.org/vg/thread/" + thread.get("no").getAsString();
										EmbedBuilder builder = new EmbedBuilder()
												.setTitle(thread.get("sub").getAsString(), link)
												.setColor(Color.BLUE)
												.setThumbnail("https://i.4cdn.org/vg/" + thread.get("tim").getAsString() + thread.get("ext").getAsString())
												.addField("Current Replies", thread.get("replies").getAsString(), false);
										channel.sendMessage(builder.build()).queue();
										return true;
									}
								}
							}
						}
					}
				}
			}

		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			REQUESTS--;
		}
		return false;
	}

}
