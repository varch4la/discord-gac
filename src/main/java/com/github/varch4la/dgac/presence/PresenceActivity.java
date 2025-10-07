package com.github.varch4la.dgac.presence;

import net.dv8tion.jda.api.entities.Activity.ActivityType;

public record PresenceActivity(String name, String state, String url, long startTime, long endTime, ActivityType type,
		String details, String icon) {

}
