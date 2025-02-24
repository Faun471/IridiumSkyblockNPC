package dev.faun.iridiumskyblocknpc.configuration;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.*;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import ch.jalu.configme.properties.types.PropertyType;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import dev.faun.iridiumskyblocknpc.configuration.beans.NpcActionBean;
import dev.faun.iridiumskyblocknpc.enums.GlowingColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @Comment({"The default name of the npc.", "Default value: %island_name%"})
    public static final Property<String> DEFAULT_DISPLAY_NAME = PropertyInitializer.newProperty(
            "npc.default_display_name",
            "<green>\uD800\uDDD0 <red>%island_name%"
    );

    @Comment({"\n", "Should the npc look at the player?", "Default value: true"})
    public static final Property<Boolean> TURN_TO_PLAYER = PropertyInitializer.newProperty(
            "npc.turn_to_player",
            true
    );

    @Comment({"\n", "Should the npc mirror the player's skin?", "Default value: true"})
    public static final Property<Boolean> MIRROR_SKIN = PropertyInitializer.newProperty(
            "npc.mirror_skin",
            true
    );

    @Comment({"\n", "Should the npc be glowing?", "Default value: true"})
    public static final Property<Boolean> GLOWING = PropertyInitializer.newProperty(
            "npc.glowing",
            true
    );


    @Comment({"\n", "The color of the glow.", "Default value: white", "Possible values: black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, white"})
    public static final EnumProperty<GlowingColor> GLOWING_COLOR = PropertyInitializer.newProperty(
            GlowingColor.class,
            "npc.glowing_color",
            GlowingColor.WHITE
    );

    @Comment({"\n", "Should the npc be collidable?", "Default value: false"})
    public static final Property<Boolean> COLLIDABLE = PropertyInitializer.newProperty(
            "npc.collidable",
            false
    );

    @Comment({"\n", "The scale of the npc.", "Default value: 1.0"})
    public static final Property<Double> SCALE = PropertyInitializer.newProperty(
            "npc.scale",
            1.0
    );

    @Comment({"\n", "The visibility distance of the npc.", "Default value: 10"})
    public static final Property<Integer> VISIBILITY_DISTANCE = PropertyInitializer.newProperty(
            "npc.visibility_distance",
            10
    );

    private static final PropertyType<NpcActionBean> NPC_ACTION_TYPE = new PropertyType<>() {
        @Override
        public @Nullable NpcActionBean convert(@Nullable Object object, @NotNull ConvertErrorRecorder errorRecorder) {
            if (object instanceof Map<?, ?> map) {
                ActionTrigger actionTrigger;
                if (map.get("action_trigger") instanceof String obj) {
                    try {
                        actionTrigger = ActionTrigger.getByName(obj);
                    } catch (IllegalArgumentException ignored) {
                        actionTrigger = ActionTrigger.ANY_CLICK;
                    }
                } else if (map.get("action_trigger") instanceof ActionTrigger obj) {
                    actionTrigger = obj;
                } else {
                    actionTrigger = ActionTrigger.ANY_CLICK;
                }

                int order = (Integer) map.get("order");
                String action = (String) map.get("action");
                String value = (String) map.get("value");
                return new NpcActionBean(actionTrigger, order, action, value);
            }
            return null;
        }

        @Override
        public Object toExportValue(NpcActionBean bean) {
            Map<String, Object> map = new HashMap<>();
            assert bean != null;
            map.put("action_trigger", bean.getActionTrigger());
            map.put("order", bean.getOrder());
            map.put("action", bean.getAction());
            map.put("value", bean.getValue());
            return map;
        }
    };

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
    public static final ListProperty<NpcActionBean> NPC_ACTIONS =
            new ListProperty<>("npc.actions", NPC_ACTION_TYPE, Collections.singletonList(new NpcActionBean()));
}
