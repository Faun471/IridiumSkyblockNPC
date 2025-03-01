package dev.faun.iridiumskyblocknpc.configuration;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

public class Messages implements SettingsHolder {
	@Override
	public void registerComments(CommentsConfiguration config) {
		config.setComment("",
				"""
						 ______          __       __ __                        ______  __                __       __                   __       __    __ _______   ______ \s
						|      \\        |  \\     |  \\  \\                      /      \\|  \\              |  \\     |  \\                 |  \\     |  \\  |  \\       \\ /      \\\s
						 \\▓▓▓▓▓▓ ______  \\▓▓ ____| ▓▓\\▓▓__    __ ______ ____ |  ▓▓▓▓▓▓\\ ▓▓   __ __    __| ▓▓____ | ▓▓ ______   _______| ▓▓   __| ▓▓\\ | ▓▓ ▓▓▓▓▓▓▓\\  ▓▓▓▓▓▓\\
						  | ▓▓  /      \\|  \\/      ▓▓  \\  \\  |  \\      \\    \\| ▓▓___\\▓▓ ▓▓  /  \\  \\  |  \\ ▓▓    \\| ▓▓/      \\ /       \\ ▓▓  /  \\ ▓▓▓\\| ▓▓ ▓▓__/ ▓▓ ▓▓   \\▓▓
						  | ▓▓ |  ▓▓▓▓▓▓\\ ▓▓  ▓▓▓▓▓▓▓ ▓▓ ▓▓  | ▓▓ ▓▓▓▓▓▓\\▓▓▓▓\\\\▓▓    \\| ▓▓_/  ▓▓ ▓▓  | ▓▓ ▓▓▓▓▓▓▓\\ ▓▓  ▓▓▓▓▓▓\\  ▓▓▓▓▓▓▓ ▓▓_/  ▓▓ ▓▓▓▓\\ ▓▓ ▓▓    ▓▓ ▓▓     \s
						  | ▓▓ | ▓▓   \\▓▓ ▓▓ ▓▓  | ▓▓ ▓▓ ▓▓  | ▓▓ ▓▓ | ▓▓ | ▓▓_\\▓▓▓▓▓▓\\ ▓▓   ▓▓| ▓▓  | ▓▓ ▓▓  | ▓▓ ▓▓ ▓▓  | ▓▓ ▓▓     | ▓▓   ▓▓| ▓▓\\▓▓ ▓▓ ▓▓▓▓▓▓▓| ▓▓   __\s
						 _| ▓▓_| ▓▓     | ▓▓ ▓▓__| ▓▓ ▓▓ ▓▓__/ ▓▓ ▓▓ | ▓▓ | ▓▓  \\__| ▓▓ ▓▓▓▓▓▓\\| ▓▓__/ ▓▓ ▓▓__/ ▓▓ ▓▓ ▓▓__/ ▓▓ ▓▓_____| ▓▓▓▓▓▓\\| ▓▓ \\▓▓▓▓ ▓▓     | ▓▓__/  \\
						|   ▓▓ \\ ▓▓     | ▓▓\\▓▓    ▓▓ ▓▓\\▓▓    ▓▓ ▓▓ | ▓▓ | ▓▓\\▓▓    ▓▓ ▓▓  \\▓▓\\\\▓▓    ▓▓ ▓▓    ▓▓ ▓▓\\▓▓    ▓▓\\▓▓     \\ ▓▓  \\▓▓\\ ▓▓  \\▓▓▓ ▓▓      \\▓▓    ▓▓
						 \\▓▓▓▓▓▓\\▓▓      \\▓▓ \\▓▓▓▓▓▓▓\\▓▓ \\▓▓▓▓▓▓ \\▓▓  \\▓▓  \\▓▓ \\▓▓▓▓▓▓ \\▓▓   \\▓▓_\\▓▓▓▓▓▓▓\\▓▓▓▓▓▓▓ \\▓▓ \\▓▓▓▓▓▓  \\▓▓▓▓▓▓▓\\▓▓   \\▓▓\\▓▓   \\▓▓\\▓▓       \\▓▓▓▓▓▓\s
						                                                                       |  \\__| ▓▓                                                                 \s
						                                                                        \\▓▓    ▓▓                                                                 \s
						                                                                         \\▓▓▓▓▓▓                                                                  \s""");
	}

	public static final Property<String> PREFIX = PropertyInitializer.newProperty(
			"messages.prefix",
			"<green>[IridiumSkyblockNPC]");

	public static final Property<String> NPC_CREATED = PropertyInitializer.newProperty(
			"messages.npc_created",
			"<green>Your NPC has been created!");

	public static final Property<String> RELOAD_SUCCESS = PropertyInitializer.newProperty(
			"messages.reload_success",
			"<green>Reloaded configs in %time%ms");

	public static final Property<String> TELEPORT_HERE_SUCCESS = PropertyInitializer.newProperty(
			"messages.teleport_here_success",
			"<green>Your island npc is successfully teleported here!");

	public static final Property<String> NO_NPC_FOUND = PropertyInitializer.newProperty(
			"messages.no_npc_found",
			"<red>No NPC found! Please create one first!");
}
