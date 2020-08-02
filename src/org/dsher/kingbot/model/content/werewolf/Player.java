package org.dsher.kingbot.model.content.werewolf;

import net.dv8tion.jda.api.entities.User;

public class Player {

	private User user;
	private Werewolf.Role role, original;

	public Player(User user) {
		setUser(user);
	}

	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return this.user;
	}
	
	public void setOriginalRole(Werewolf.Role role) {
		this.original = role;
	}
	public Werewolf.Role getOriginalRole() {
		return this.original;
	}

	public void setRole(Werewolf.Role role) {
		this.role = role;
	}    
	public Werewolf.Role getRole() {
		return this.role;
	}

}
