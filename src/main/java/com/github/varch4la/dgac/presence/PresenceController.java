package com.github.varch4la.dgac.presence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.transform.TransformerException;

import com.github.varch4la.dgac.UserDatabase;
import com.github.varch4la.dgac.exceptions.UserOptedOutException;
import com.github.varch4la.dgac.svg.StatusCardFactory;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.Timestamps;
import net.dv8tion.jda.api.entities.Member;

public class PresenceController {

	private final JDA jda;
	private final UserDatabase database;
	private final StatusCardFactory factory = new StatusCardFactory();

	public PresenceController(JDA jda, UserDatabase database) {
		this.jda = jda;
		this.database = database;
	}

	private Optional<Member> getMember(String id) {
		return jda.getGuilds().stream().map(g -> g.getMemberById(id)).filter(Objects::nonNull).findAny();
	}

	public Presence getPresence(String userId) throws UserOptedOutException, SQLException {
		Optional<Member> member = getMember(userId);
		if (member.isPresent()) {
			Member m = member.get();
			if (!database.isOptedIn(m.getUser())) {
				throw new UserOptedOutException();
			}
			List<PresenceActivity> acts = new ArrayList<>();
			for (Activity act : m.getActivities()) {
				Timestamps timestamps = act.getTimestamps();
				acts.add(new PresenceActivity(act.getName(), act.getState(), act.getUrl(), timestamps.getStart(),
						timestamps.getEnd(), act.getType()));
			}
			return new Presence(userId, m.getOnlineStatus(), List.copyOf(acts));
		} else {
			return null;
		}
	}

	public void statusCard(Context ctx) throws UserOptedOutException, SQLException, IOException, TransformerException {
		String userId = ctx.pathParam("userId");
		Presence presence = getPresence(userId);
		if (presence == null) {
			ctx.result("User not found");
			ctx.status(HttpStatus.NOT_FOUND);
		} else {
			ctx.contentType(ContentType.IMAGE_SVG);
			ctx.result(factory.createStatusCard());
		}
	}

	public void userPresence(Context ctx) throws UserOptedOutException, SQLException {
		String userId = ctx.pathParam("userId");
		Presence presence = getPresence(userId);
		if (presence == null) {
			ctx.result("User not found");
			ctx.status(HttpStatus.NOT_FOUND);
		} else {
			ctx.json(presence);
		}
	}
}
