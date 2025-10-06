package com.github.varch4la.dgac;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import com.github.varch4la.dgac.cfg.Config;
import com.github.varch4la.dgac.command.ActivityShareCommand;
import com.github.varch4la.dgac.presence.PresenceController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.javalin.Javalin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class GACMain {
	public static void main(String[] args) throws Exception {

		File cfgFile = new File("config.json");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		if (!cfgFile.isFile()) {
			try (Writer writer = new FileWriter(cfgFile)) {
				gson.toJson(new Config(), writer);
			}
		}

		Config config;
		try (Reader reader = new FileReader(cfgFile)) {
			config = gson.fromJson(reader, Config.class);
		}

		if (config.getToken().equals(new Config().getToken())) {
			System.err.println("You need to specify a Discord bot token in %s!".formatted(cfgFile.getName()));
			return;
		}

		UserDatabase userDatabase = new UserDatabase(new File(config.getDatabase()));

		JDA jda = JDABuilder.createLight(config.getToken(), GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL).enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY)
				.build().awaitReady();

		ActivityShareCommand shareCommand = new ActivityShareCommand(userDatabase);

		for (Guild guild : jda.getGuilds())
			guild.updateCommands().addCommands(shareCommand.getData()).queue();

		jda.addEventListener(shareCommand);

		PresenceController controller = new PresenceController(jda);

		Javalin javalin = Javalin.create(cfg -> {
			cfg.router.apiBuilder(() -> {
				path("/presence", () -> {
					get("{userId}", controller::userPresence);
				});
			});
		});

		javalin.start(8080);
	}

}
