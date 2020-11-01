package org.dsher.kingbot.model.content.werewolf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.dsher.kingbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class Game implements Runnable {

	public static final int WAIT_TIME = 5000;

	private MessageChannel channel;
	private Werewolf werewolfGame;

	private int awaitingInputFrom = -1;
	private Player troublemakerPlayerOne = null;

	private boolean finishedNight = false, stop = false, started = false;
	
	private ArrayList<String> nightEvents = new ArrayList<>();

	public Game(MessageChannel channel) {
		this.channel = channel;
		this.werewolfGame = new Werewolf();
	}

	public MessageChannel getChannel() {
		return this.channel;
	}

	public boolean isFinishedNight() {
		return this.finishedNight;
	}

	public boolean isStarted() {
		return started;
	}

	public Werewolf getWerewolfGame() {
		return this.werewolfGame;
	}

	@Override
	public void run() {
		if (stop) return;
		try {
			this.started = true;
			werewolfGame.prepareCards();
			channel.sendMessage("Welcome to One Night Ultimate Werewolf! In this game, a team of villagers will attempt to catch a secret werewolf hiding amongst them. There may be at most two werewolves among you, but the villagers need only catch one. If neither werewolf is caught at the end of the game, the werewolf team wins!\nIf you check your direct messages, you will find a new message from KingBot informing you of your role. Please read the description carefully, as you may wake up during the night as part of your role. When everyone is ready to begin, the host may use the command \"!ww night\" to start the night sequence.\n Good luck to everyone playing!").queue();
			String msg = "The possible roles for this game are as follows: ";
			for (Werewolf.Role role : werewolfGame.getCards()) {
				msg += role.getName() + ", ";
			}
			msg = msg.substring(0, msg.length() - 2) + ".";
			channel.sendMessage(msg).queue();
			werewolfGame.dealCards();
			informPlayerRoles();
			sleep(WAIT_TIME);
			handleNight();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}

	public void kill() {
		this.stop = true;
	}

	private void sleep(int time) throws InterruptedException {
		if (stop) return;
		Thread.sleep(time);
	}

	private synchronized void safeWait() throws InterruptedException {
		if (stop) return;
		wait();
	}

	/*********************************** START ********************************/

	public void informPlayerRoles() {
		if (stop) return;
		for (Player player : werewolfGame.getPlayers()) {
			player.getUser().openPrivateChannel().queue((channel) ->
			{
				channel.sendMessage("╒◖════════════════════◗╕\n" + 
						"  **ONE NIGHT ULTIMATE WEREWOLF**\n" + 
						"╘◖════════════════════◗╛\n" + player.getOriginalRole().getDescription()).queue();
			});
		}
		String toSend = "Please check your private messages to see which role you were assigned!\nThe possible roles for this game are as follows (there are three more possibilities than the number of players - this is intentional, three roles will not be claimed by a player!): ";
		for (Werewolf.Role role : werewolfGame.getCards()) {
			toSend += role.getName() + ", ";
		}
		toSend = toSend.substring(0, toSend.length() - 2) + ".";
		channel.sendMessage(toSend);
	}

	/*********************************** NIGHT *********************************/

	private void handleNight() throws InterruptedException {
		if (stop) return;
		werewolfTurn();
		sleep(WAIT_TIME);
		if (werewolfGame.getCards().contains(Werewolf.Role.MASON)) {
			masonTurn();
			sleep(WAIT_TIME);
		}
		inputRoleTurn(Werewolf.Role.SEER);
		inputRoleTurn(Werewolf.Role.ROBBER);
		inputRoleTurn(Werewolf.Role.TROUBLEMAKER);
		if (werewolfGame.getCards().contains(Werewolf.Role.DRUNK)) {
			drunkTurn();
			sleep(WAIT_TIME);
		}
		if (werewolfGame.getCards().contains(Werewolf.Role.INSOMNIAC)) {
			insomniacTurn();
			sleep(WAIT_TIME);
		}
		channel.sendMessage("Good morning! The night is over, and now it's time to discuss amongst yourselves who the werewolf is. You can be as forthcoming or secretive about your own role and what you did during the night as you wish. When everyone is ready, vote on which player you wish to accuse, then formally accuse them with ``!ww accuse [player name]``. Good luck!").queue();
		finishedNight = true;
	}

	private void werewolfTurn() {
		if (stop) return;
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getOriginalRole().equals(Werewolf.Role.WEREWOLF) || player.getOriginalRole().equals(Werewolf.Role.MINION)) {
				Player[] partners = findPartners(player, Werewolf.Role.WEREWOLF);
				String message;
				if (partners.length > 0) {
					if (partners.length == 1) {
						message = "Your werewolf partner in this game is " + partners[0].getUser().getName() + ".";
					} else {
						message = "Your werewolf partners in this game are ";
						for (Player partner : partners) {
							message += partner.getUser().getName() + ", ";
						}
						message = message.substring(0, message.length() - 2) + ".";
					}
				} else {
					message = "You're a lone wolf! You have no werewolf partner in this game.";
				}
				final String toSend = message;
				player.getUser().openPrivateChannel().queue((channel) ->
				{
					channel.sendMessage(toSend).queue();
				});
			}
		}
		nightEvents.add(wolfSummary());
		channel.sendMessage("Werewolves are recognizing each other... If there is a minion, they will also be told who their werewolf partners are at this time.").queue();
	}
	
	private String wolfSummary() {
		String summary = "";
		String wolves = "", minion = "";
		int wolfCount = 0;
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getRole().equals(Werewolf.Role.WEREWOLF)) {
				wolfCount++;
				if (wolves.isEmpty()) {
					wolves = player.getUser().getName();
				} else {
					wolves += " and " + player.getUser().getName();
				}
			} else if (player.getRole().equals(Werewolf.Role.MINION)) {
				minion = player.getUser().getName();
			}
		}
		switch (wolfCount) {
			case 0:
				summary = "There were no wolves at the beginning of the night!";
				break;
			case 1:
				summary = wolves + " woke up to find they were alone as the werewolf!";
				break;
			case 2:
				summary = wolves + " woke up and recognized eachother as the two werewolves.";
				break;
		}
		if (!minion.isEmpty()) {
			summary += " " + minion + " was the" + (wolfCount > 0 ? "ir" : "") + " minion.";
		}
		return summary;
	}

	private void masonTurn() {
		if (stop) return;
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getOriginalRole().equals(Werewolf.Role.MASON)) {
				Player[] partners = findPartners(player, Werewolf.Role.MASON);
				String message;
				if (partners.length > 0) {
					if (partners.length == 1) {
						message = "Your mason partner in this game is " + partners[0].getUser().getName() + ".";
					} else {
						message = "Your mason partners in this game are ";
						for (Player partner : partners) {
							message += partner.getUser().getName() + ", ";
						}
						message = message.substring(0, message.length() - 2) + ".";
					}
				} else {
					message = "You have no mason partner in this game.";
				}
				final String toSend = message;
				player.getUser().openPrivateChannel().queue((channel) ->
				{
					channel.sendMessage(toSend).queue();
				});
			}
		}
		nightEvents.add(masonSummary());
		channel.sendMessage("Masons are recognizing eachother...").queue();
	}
	
	private String masonSummary() {
		String summary = "";
		String masons = "";
		int masonCount = 0;
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getRole().equals(Werewolf.Role.MASON)) {
				masonCount++;
				if (masons.isEmpty()) {
					masons = player.getUser().getName();
				} else {
					masons += " and " + player.getUser().getName();
				}
			}
		}
		switch (masonCount) {
			case 0:
				summary = "There were no masons at the beginning of the night!";
				break;
			case 1:
				summary = masons + " woke up to find they were the only mason!";
				break;
			case 2:
				summary = masons + " woke up and recognized eachother as the two masons.";
				break;
		}
		return summary;
	}

	private Player[] findPartners(Player player, Werewolf.Role role) {
		// don't return on stop here -- best to let the logic play out
		ArrayList<Player> partners = new ArrayList<>();
		for (Player other : werewolfGame.getPlayers()) {
			if (other.getOriginalRole().equals(role) && !other.equals(player))
				partners.add(other);
		}
		Object[] array = partners.toArray();
		Player[] partnerArray = Arrays.copyOf(array, array.length, Player[].class);
		return partnerArray;
	}

	private void drunkTurn() {
		if (stop) return;
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getOriginalRole().equals(Werewolf.Role.DRUNK)) {
				int roll = Utils.getRandom(1, werewolfGame.getCenterCards().size());
				player.setRole(werewolfGame.getCenterCards().get(roll - 1));
				werewolfGame.getCenterCards().set(roll - 1, player.getOriginalRole());
				final String toSend = "As the drunk, you're up in the night stumbling around town again. You have exchanged your role with Unused Role " + roll + "! You will not know the identity of your new role, but perhaps speaking with the other villagers will help you figure this out (remember the number of your unused roll, it's important information!). Remember, you are now on the team of your new role, so you may want to keep this close to your chest for now.";
				nightEvents.add("The drunk, " + player.getUser().getName() + " took on Unused Role " + roll + " as his new role, becoming the " + player.getRole().getName() + "!");
				player.getUser().openPrivateChannel().queue((channel) ->
				{
					channel.sendMessage(toSend).queue();
				});
			}
		}
		channel.sendMessage("The town drunk is wandering about at night again. They will exchange their role for one of the three unused roles for this round, although they will not learn their new identity. Beware, town drunk! You may become a werewolf and not even know it!").queue();
	}

	private void insomniacTurn() {
		if (stop) return;
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getOriginalRole().equals(Werewolf.Role.INSOMNIAC)) {		
				final String toSend = ("A lot happened tonight! As the insomniac, you have the benefit of seeing if any of tonight's events affected your role. After tonight, your role is " + (player.getRole().equals(player.getOriginalRole()) ? " still " : "") + " the " + player.getRole().getName() + ".");
				nightEvents.add("The insomniac woke up to check their role again, learning that by the end of the night they were the " + player.getRole().getName() + ".");
				player.getUser().openPrivateChannel().queue((channel) ->
				{
					channel.sendMessage(toSend).queue();
				});
			}
		}
		channel.sendMessage("The insomniac is waking up to check if their role is the same as when they went to sleep. Insomniac, check your messages now!");
	}

	private void inputRoleTurn(Werewolf.Role role) throws InterruptedException {
		if (stop) return;
		if (werewolfGame.getCards().contains(role)) {
			if (doInputRoleTurn(role)) {
				safeWait();
			} else {
				sleep(Utils.getRandom(15000, 20000));
			}
		}
	}

	private boolean doInputRoleTurn(Werewolf.Role role) {
		if (stop) return false;
		boolean found = false;
		String msg = "Something went wrong, contact admin.";
		String prvtMsg = "Something went wrong, contact admin";
		switch (role) {
			case SEER:
				msg = "The seer is peering into the other roles in this game... If you want information, the seer will be a good person to ask! Seer, check your messages now!";
				prvtMsg = "At the beginning of the game, you were informed of all the possible roles a player could be. You may have noticed there were three more possibilities than there are players.\nAs seer, you may choose to look at **two** of these unused roles, **or** you may look at **one** other player's role.\nIf you wish to see two of the three unused roles in this game, reply \"center\". If you wish to see another player's cards, please reply with their name.";
				break;
			case ROBBER:
				msg = "The robber is prowling the night looking for someone to switch roles with. Beware! Your role may be stolen, and you'll be none the wiser... Robber, check your messages now!";
				prvtMsg = "As the robber, you may now choose to swap cards with another player. If you wish to do this, please respond with the name of the player you'd like to swap with. Remember, if you take someone's card, you will join that card's team! If you would rather stay as the robber, respond \"skip turn\".";
				break;
			case TROUBLEMAKER:
				msg = "The troublemaker is on the loose! They may choose to swap two player's roles... Troublemaker, check your messages now!";
				prvtMsg = "As the troublemaker, you may now choose to swap two players' cards. You will not see either card you swap! If you wish to swap cards, please respond with the name of the first player whose cards you wish to swap. Otherwise, respond \"skip turn\".";
			default:
				break;
		}
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getOriginalRole().equals(role)) {
				found = true;
				awaitingInputFrom = role.getRoleId();
				final String toSend = prvtMsg;
				player.getUser().openPrivateChannel().queue((channel) ->
				{
					channel.sendMessage(toSend).queue();
				});
			}
		}
		channel.sendMessage(msg).queue();;
		return found;
	}

	/*********************************** PRIVATE INPUT ********************************/

	public void acceptPrivateInput(User author, String message, MessageChannel channel) {
		if (!werewolfGame.isUserInGame(author)) 
			return;
		if (channel.getType() != ChannelType.PRIVATE) {
			return;
		}
		// only accept messages from player matching the awaitingInputFrom role
		Werewolf.Role or = werewolfGame.getPlayerByUser(author).getOriginalRole();
		if (or == null || or.getRoleId() != awaitingInputFrom)
			return;
		if (message.toLowerCase().equals("skip turn")) {
			resume();
			return;
		}
		switch (Werewolf.Role.getByRoleId(awaitingInputFrom)) {
			case SEER:
				if (message.toLowerCase().equals("center")) {
					Werewolf.Role[] cardsToSee = new Werewolf.Role[2];
					int[] cardNumbers = new int[2];
					int roll = Utils.getRandom(0, 2);
					for (int i = 0, j = 0; i < 3; i++) {
						if (roll == i)
							continue;
						cardsToSee[j] = werewolfGame.getCenterCards().get(i);
						cardNumbers[j] = i + 1;
						j++;
					}
					String seerMsg = "The cards you saw were as follows:";
					for (int i = 0; i < cardsToSee.length; i++) {
						seerMsg += "Card " + cardNumbers[i] + ": " + Utils.capitalize(cardsToSee[i].getName()) + "\n";
					}
					channel.sendMessage(seerMsg).queue();
					nightEvents.add("The seer, " + author.getName() + ", looked at two of the unused roles. They same Unused Role " + cardNumbers[0] + ": the " + cardsToSee[0].getName() + ", and Unused Role " + cardNumbers[1] + ": the " + cardsToSee[1].getName() + "!");
					resume();
				} else {
					Player player = werewolfGame.getPlayerByUsername(message.toLowerCase());
					if (player != null) {
						channel.sendMessage(player.getUser().getName() + "'s role is " + player.getRole().getName() + ".").queue();
						nightEvents.add("The seer, " + author.getName() + ", looked at " + player.getUser().getName() + "'s role and discovered they were the " + player.getRole().getName() + "!");
						resume();
					} else {
						channel.sendMessage("No player found by that name. **center** to look at two center cards, or type a player's name. Alternatively, you may type **skip turn** to skip your turn.").queue();
					}
				}
				break;
			case ROBBER:
				Player robberPlayer = werewolfGame.getPlayerByUsername(message.toLowerCase());
				if (robberPlayer != null) {
					Player sender = werewolfGame.getPlayerByUser(author);
					Werewolf.Role tempRole = robberPlayer.getRole();
					swapRoles(robberPlayer, sender);
					channel.sendMessage("You have swapped roles with " + robberPlayer.getUser().getName() + ". Your new role is the " + sender.getRole().getName() + ".").queue();
					nightEvents.add("The robber, " + author.getName() + ", swapped roles with " + robberPlayer.getUser().getName() + ", becoming the " + tempRole.getName() + ".");
					resume();
				} else {
					channel.sendMessage("No player found by that name. Please type a player's name to swap roles with them. Alternatively, you may type **skip turn** to skip your turn.").queue();
				}
				break;
			case TROUBLEMAKER:
				Player troublemakerPlayer = werewolfGame.getPlayerByUsername(message.toLowerCase());
				if (troublemakerPlayer != null) {
					if (this.troublemakerPlayerOne != null) {
						Werewolf.Role tempRoleOne = troublemakerPlayerOne.getRole();
						Werewolf.Role tempRoleTwo = troublemakerPlayer.getRole();
						swapRoles(troublemakerPlayer, troublemakerPlayerOne);
						channel.sendMessage("You have swapped the roles of " + troublemakerPlayer.getUser().getName() + " and " + troublemakerPlayerOne.getUser().getName() + ".").queue();
						nightEvents.add("The troublemaker, " + author.getName() + ", swapped the roles of " + troublemakerPlayerOne.getUser().getName() + " (the " + tempRoleOne.getName() + "), and " + troublemakerPlayer.getUser().getName() + " (the " + tempRoleTwo.getName() + ").");
						resume();
					} else {
						this.troublemakerPlayerOne = troublemakerPlayer;
						channel.sendMessage("Who would you like to swap " + troublemakerPlayer.getUser().getName() + "s role with? Please type the name of another player.").queue();
					}
				} else {
					channel.sendMessage("No player found by that name. Please type the name of a player whom you wish you swap roles with another. Alternatively, you may type **skip turn** to skip your turn.").queue();
				}
				break;
			default:
				break;			
		}
	}

	private synchronized void resume() {
		awaitingInputFrom = -1;
		notify();
	}

	private void swapRoles(Player one, Player two) {
		Werewolf.Role tempRole = two.getRole();
		two.setRole(one.getRole());
		one.setRole(tempRole);
	}

	/*********************************** ACCUSE ********************************/

	public MessageEmbed buildResults(String accusation) {
		String result = buildResult(accusation);
		if (result == null) {
			return null;
		}
		String werewolves = "", tanner = "", villagers = "", unused = "";
		for (Player player : werewolfGame.getPlayers()) {
			if (player.getRole().equals(Werewolf.Role.WEREWOLF)|| player.getRole().equals(Werewolf.Role.MINION)) {
				werewolves += player.getUser().getName() + " (" + player.getRole().getEmoji() + " " + Utils.capitalize(player.getRole().getName()) + "), ";
			} else if (player.getRole().equals(Werewolf.Role.TANNER)) {
				tanner = player.getUser().getName();
			} else {
				villagers += player.getUser().getName() + " (" + player.getRole().getEmoji() + " " + Utils.capitalize(player.getRole().getName()) + "), ";
			}
		}
		werewolves = Utils.endListString(werewolves);
		villagers = Utils.endListString(villagers);
		
		for (Werewolf.Role role : werewolfGame.getCenterCards()) {
			unused += role.getEmoji() + " " + Utils.capitalize(role.getName()) + ", ";
		}
		unused = Utils.endListString(unused);
		
		String winner = result.toLowerCase().contains("werewolves win") ? "Werewolves" : result.toLowerCase().contains("tanner") ? "Tanners" : "Villagers";
		
		String title = winner + " Win!";
		
		EmbedBuilder builder = new EmbedBuilder()
				.setTitle(title)
				.setColor(Color.BLACK)
				.setThumbnail(winner.toLowerCase().equals("werewolves") ? "https://i.imgur.com/FN5TPFt.jpg" : winner.toLowerCase().equals("tanners") ? "https://i.imgur.com/mxV1tFO.png" : "https://i.imgur.com/yHkimzt.jpg")
				.setDescription(result);
		
		if (!werewolves.isEmpty()) {
			builder.addField("Werewolves", werewolves, false);
		}
		
		if (!villagers.isEmpty()) {
			builder.addField("Villagers", villagers, false);
		}
		
		if (!tanner.isEmpty()) {
			builder.addField("Tanner", tanner, false);
		}
		
		builder.addField("Unused Roles", unused, false);
		
		builder.addField("Game Summary", buildSummary(), false);

		return builder.build();
	}
	
	private String buildResult(String accusation) {
		if (isFinishedNight()) {
			Player player = getWerewolfGame().getPlayerByUsername(accusation.toLowerCase());
			if (player == null) {
				if (accusation.toLowerCase().equals("center")) {
					int wolfCount  = 0;
					for (org.dsher.kingbot.model.content.werewolf.Werewolf.Role center : getWerewolfGame().getCenterCards()) {
						if (center.equals(org.dsher.kingbot.model.content.werewolf.Werewolf.Role.WEREWOLF))
							wolfCount++;
					}
					if (wolfCount >= 2) {
						return  "Both werewolves were in the center. Congratulations! The villagers win.";
					} else {
						return "One or both of the werewolves were amongst you... Sorry! The werewolves win!";
					}
				}
				return null;
			}
			org.dsher.kingbot.model.content.werewolf.Werewolf.Role role = player.getRole();
			switch (role) {
				case WEREWOLF:
					return player.getUser().getName() + " was a werewolf! :wolf: Congratulations villagers, you found the werewolf!";
				case TANNER:
					return player.getUser().getName() + " was the tanner! That means " + player.getUser().getName() + " wins!";
				default:
					return "Sorry villagers, " + player.getUser().getName() + " was the " + role.getName() + ". That means the werewolves win!";
			}
		}
		return null;
	}
	
	private String buildSummary() {
		String msg = "During the night...\n";
		for (String event : nightEvents) {
			msg += event + "\n";
		}
		return msg;
	}

}
