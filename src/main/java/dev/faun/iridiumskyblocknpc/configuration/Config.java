package dev.faun.iridiumskyblocknpc.configuration;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.*;
import ch.jalu.configme.properties.types.BeanPropertyType;
import dev.faun.iridiumskyblocknpc.configuration.beans.NpcActionBean;
import dev.faun.iridiumskyblocknpc.enums.GlowingColor;

import java.util.Collections;

public final class Config implements SettingsHolder {
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

	@Comment({ "The default name of the npc.", "Default value: %island_name%" })
	public static final Property<String> DEFAULT_DISPLAY_NAME = PropertyInitializer.newProperty(
			"config.npc.default_display_name",
			"<green>\uD800\uDDD0 <red>%island_name%");

	@Comment({ "\n", "Should the npc look at the player?", "Default value: true" })
	public static final Property<Boolean> TURN_TO_PLAYER = PropertyInitializer.newProperty(
			"config.npc.turn_to_player",
			true);

	@Comment({ "\n", "Should the npc mirror the player's skin?", "Default value: true" })
	public static final Property<Boolean> MIRROR_SKIN = PropertyInitializer.newProperty(
			"config.npc.mirror_skin",
			true);

	@Comment({ "\n", "Should the npc be glowing?", "Default value: true" })
	public static final Property<Boolean> GLOWING = PropertyInitializer.newProperty(
			"config.npc.glowing",
			true);

	@Comment({ "\n", "The color of the glow.", "Default value: white",
			"Possible values: black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, white" })
	public static final EnumProperty<GlowingColor> GLOWING_COLOR = PropertyInitializer.newProperty(
			GlowingColor.class,
			"config.npc.glowing_color",
			GlowingColor.WHITE);

	@Comment({ "\n", "Should the npc be collidable?", "Default value: false" })
	public static final Property<Boolean> COLLIDABLE = PropertyInitializer.newProperty(
			"config.npc.collidable",
			false);

	@Comment({ "\n", "The scale of the npc.", "Default value: 1.0" })
	public static final Property<Double> SCALE = PropertyInitializer.newProperty(
			"config.npc.scale",
			1.0);

	@Comment({ "\n", "The visibility distance of the npc.", "Default value: 10" })
	public static final Property<Integer> VISIBILITY_DISTANCE = PropertyInitializer.newProperty(
			"config.npc.visibility_distance",
			10);

	@Comment({
			"\n",
			"The list of actions the npc should perform.",
			"Default value:",
			"-   action: player_command",
			"    action_trigger: ANY_CLICK",
			"    value: island",
			"    order: 1",
			"For possible values, please visit the wiki: 🚧WIKI UNDER CONSTRUCTION🚧"
	})
	public static final ListProperty<NpcActionBean> NPC_ACTIONS = new ListProperty<>("config.npc.actions",
			BeanPropertyType.of(NpcActionBean.class), Collections.singletonList(new NpcActionBean()));

	@Comment({ "\n", "Should the player that created the npc be able to change the npc's data?",
			"Default value: true" })
	public static final Property<Boolean> ALLOW_PLAYER_EDIT = PropertyInitializer.newProperty(
			"config.npc.allow_player_edit",
			true);

	@Comment({ "\n", "The list of config options that the player can change.",
			"Default value: [display_name, turn_to_player, mirror_skin, glowing, glowing_color, collidable, scale, visibility_distance, actions]" })
	public static final ListProperty<String> ALLOWED_PLAYER_EDIT_OPTIONS = PropertyInitializer.newListProperty(
			"config.npc.allowed_player_edit_options",
			"display_name", "turn_to_player", "mirror_skin", "glowing", "glowing_color", "collidable",
			"scale", "visibility_distance", "actions");
}
