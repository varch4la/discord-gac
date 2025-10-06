package com.github.varch4la.dgac.presence;

import java.util.List;

import net.dv8tion.jda.api.OnlineStatus;

public record Presence(String userId, OnlineStatus status, List<PresenceActivity> activities) {

}
