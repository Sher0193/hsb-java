package org.dsher.kingbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.dsher.kingbot.model.command.CommandHandler;
import org.dsher.kingbot.model.content.scoreboard.ScoreboardHandler;
import org.dsher.kingbot.model.content.werewolf.WerewolfHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot extends ListenerAdapter {

	private static JDA jda;	

	private static Bot botInstance;

	private String token, prefix, name;

	private ScoreboardHandler sh = new ScoreboardHandler();
	private WerewolfHandler wwh = new WerewolfHandler();

	private static long launchTime = 0;

	public static void main(String[] args) throws LoginException {

		Properties p = new Properties();


		try {
			p.load(new FileInputStream("./data/kingbot.ini"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Singleton Instance of Bot
		botInstance = new Bot(p.getProperty("token"), p.getProperty("prefix"), p.getProperty("name"));		

		// We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
		// All other events will be disabled.
		jda = JDABuilder.createLight(botInstance.getToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
				.addEventListeners(botInstance)
				.setActivity(Activity.playing("!help"))
				.build();
	}

	public static Bot getBotInstance() {
		return botInstance;
	}

	public Bot(String token, String prefix, String name) {
		this.token = token;
		this.prefix = prefix;
		this.name = name;
	}

	@Override
	public void onReady(ReadyEvent event) {
		launchTime = System.currentTimeMillis();
		sh.loadScoreboards();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message msg = event.getMessage();

		// Do not process messages from other bots
		if (msg.getAuthor().isBot()) 
			return;

		// Handle all messages, prefixed and not
		CommandHandler.handleUnprefixedCommand(msg);

		// From this point, do not process unprefixed messages
		if (msg.getContentRaw().indexOf(prefix) != 0)
			return;

		// Split message string on spaces
		String[] splitMsg = msg.getContentRaw().substring(prefix.length()).split(" ");

		// First element is the command
		String command = splitMsg[0].toLowerCase();

		// Subsequent elements are the arguments for the command, track in its own array
		String[] args = new String[splitMsg.length -1];
		for (int i = 0; i < args.length; i++) {
			args[i] = splitMsg[i + 1];
		}

		CommandHandler.handleCommand(command, args, msg.getChannel(), msg.getAuthor());

	}

	public JDA getJDA() {
		return jda;
	}

	public long getLaunchTime() {
		return launchTime;
	}

	public String getName() {
		return name;
	}

	private String getToken() {
		return token;
	}

	public String getPrefix() {
		return prefix;
	}

	public ScoreboardHandler getScoreboardHandler() {
		return sh;
	}
	
	public WerewolfHandler getWerewolfHandler() {
		return wwh;
	}

}
