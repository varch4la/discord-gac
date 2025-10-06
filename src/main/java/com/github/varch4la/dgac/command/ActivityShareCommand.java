package com.github.varch4la.dgac.command;

import com.github.varch4la.dgac.UserDatabase;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class ActivityShareCommand extends ListenerAdapter {

	private final UserDatabase userDatabase;

	public ActivityShareCommand(UserDatabase userDatabase) {
		this.userDatabase = userDatabase;
	}

	public CommandData getData() {
		return Commands.slash("share-activity",
				"Permits the bot to share your activity through web API. You can opt-out by re-running this command.");
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.isFromGuild() && event.getFullCommandName().equals("share-activity")) {
			event.deferReply(true).queue();
			User user = event.getUser();
			try {
				String responseMessage;
				if (userDatabase.isOptedIn(user)) {
					userDatabase.optOut(user);
					responseMessage = "Opted out of activity sharing.";
				} else {
					userDatabase.optIn(user);
					responseMessage = "Opted in to activity sharing!";
				}
				event.getHook().sendMessage(responseMessage).queue();
			} catch (Exception e) {
				e.printStackTrace();
				event.getHook().sendMessage("An error happened.").queue();
			}
		}
	}

}
