package org.dsher.kingbot.model.content.werewolf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.api.entities.User;

public class Werewolf {

	public static enum Role {
		VILLAGER(0, "villager", ":man_farmer:", "You are a **Villager** :man_farmer:! Your job is to catch a werewolf. You have no special abilities or quirks and will not be informed of anything during the night. Good luck!"),
		WEREWOLF(1, "werewolf", ":wolf:", "Awooooo! You're a **Werewolf** :wolf:! The whole village is trying to catch you, so keep your identity secret at any cost! You need to lie and cheat as much as you need to to fool everyone else. **You win if no werewolves are caught at the end of the game**. You may have another werewolf partner, if so you will be informed of their identity during the night. Good luck!"),
		SEER(2, "seer", ":crystal_ball:", "You're the **Seer** :crystal_ball:! You are on the team of the villagers, so your job is to catch a werewolf. During the night, you will have a chance to peek at another player's card, so keep an eye out for further instructions during the night!"),
		ROBBER(3, "robber", ":detective:", "You're the **Robber** :detective:! You are on the team of the villagers, so your job is to catch a werewolf. During the night, you will have a chance to steal another player's card. If you do so, you will be on the team of whatever card you chose. Await further instructions during the night!"),
		TROUBLEMAKER(4, "troublemaker", ":clown:", "You're the **Troublemaker** :clown:! You are on the team of the villagers, so your job is to catch a werewolf. During the night, you will have a chance to switch two players' cards. Await further instructions during the night!"),
		TANNER(5, "tanner", ":slight_frown:", "You're the **tanner** :slight_frown:. You have lost all will to live, you only win this game if you convince the town to kill **you** instead of a werewolf. You will need to lie and cheat to make the village believe you're worth killing. Good luck."),
		DRUNK(6, "drunk", ":beer:", "You're the **town drunk** :beer:! You are on the team of the villagers, so your job is to catch a werewolf. During the night, you will be given a random extra role, and informed of which card you received (although you will not be told the role). Keep an eye out for this information during the night!"),
		HUNTER(7, "hunter", ":gun:", "You're the **hunter** :gun:! You are on the team of the villagers, so your job is to catch a werewolf. If you are killed at the end of the game, you may select another player to kill instead. Your kill is not subject to votes, so if you select the werewolf, the villagers win. Good luck!"),
		MASON(8, "mason", ":hammer:", "You're a **mason** :hammer:! You are on the team of the villagers, so your job is to catch a werewolf. You may have another mason friend in the game. If this is the case, during the night you will be told who he or she is."),
		INSOMNIAC(9, "insomniac", ":crescent_moon:", "You're the **insomniac** :crescent_moon:! You are on the team of the villagers, so your job is to catch a werewolf. At the end of the night, you will be told if your role has changed due to other player's nighttime behaviour. Remember, if your new role may put you on another team. Good luck!"),
		MINION(10, "minion", ":japanese_goblin:", "You are the minion :japanese_goblin:! You are on the team of the werewolves, your job is to **make sure** no werewolf dies, even if it means sacrificing yourself. You will be informed of the identity of the werewolves during the night."),
		DOPPLEGANGER(11, "doppleganger", ":tbd:", "You're the doppleganger. FUCK.");

		private int role;
		private String name, emoji, description;

		Role(int role, String name, String emoji, String description) {
			this.role = role;
			this.name = name;
			this.emoji = emoji;
			this.description = description;
		}
		
		public static Role getByRoleId(int roleId) {
			for (Role role : Role.values()) {
				if (role.getRoleId() == roleId)
					return role;
			}
			return null;
		}

		public int getRoleId() {
			return role;
		}

		public String getDescription() {
			return this.description;
		}

		public String getName() {
			return this.name;
		}
		
		public String getEmoji() {
			return this.emoji;
		}
	}

	private static final Role[] BASE_CARDS = {Role.WEREWOLF, Role.WEREWOLF, Role.SEER, Role.ROBBER, Role.TROUBLEMAKER};

	private static final Role[] EXTRA_ROLES = {Role.TANNER, Role.DRUNK, Role.HUNTER, Role.MASON, Role.MASON, Role.MINION, Role.INSOMNIAC, Role.VILLAGER, Role.VILLAGER, Role.VILLAGER};

	@SuppressWarnings("unused")
	private static final Role[] UNIMPLEMENTED_ROLES = {Role.DOPPLEGANGER};

	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<Role> cards = new ArrayList<>();

	private ArrayList<Role> centerCards = new ArrayList<>();

	public boolean addPlayer(User user) {
		for (Player player : players) {
			if (player.getUser().equals(user))
				return false;
		}
		if (players.size() < 10)
			this.players.add(new Player(user));
		return true;
	}

	public boolean removePlayer(User user) {
		for (Player player : players) {
			if (player.getUser().equals(user)) {
				players.remove(player);
				return true;
			}
		}
		return false;
	}

	public void prepareCards() {
		List<Role> list = Arrays.asList(EXTRA_ROLES);
		Collections.shuffle(list);

		for (int i = 0; i < players.size() + 3; i++) {
			if (i >= BASE_CARDS.length) {
				cards.add(list.get(i - BASE_CARDS.length));
			} else {
				cards.add(BASE_CARDS[i]);
			}
		}
	}

	public void dealCards() {
		Collections.shuffle(cards);

		int i = 0;

		for (; i < players.size(); i++) {
			players.get(i).setOriginalRole(cards.get(i));
			players.get(i).setRole(cards.get(i));
		}

		for (int j = 0; j < 3; j++) {
			centerCards.add(cards.get(i++));
		}
	}

	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public ArrayList<Role> getCards() {
		return this.cards;
	}

	public ArrayList<Role> getCenterCards() {
		return this.centerCards;
	}
	
	public Player getPlayerByUsername(String name) {
		for (Player player : players) {
			if (player.getUser().getName().toLowerCase().equals(name))
				return player;
		}
		return null;
	}
	
	public Player getPlayerByUser(User user) {
		for (Player player : players) {
			if (player.getUser().equals(user)) 
				return player;
		}
		return null;
	}
	
	public boolean isUserInGame(User user) {
		return (getPlayerByUser(user) != null);	
	}

}
