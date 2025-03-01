package dev.faun.iridiumskyblocknpc.listeners;

import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.database.Island;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.Config;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.UUID;

public class IslandCreateListener implements Listener {
    private final IridiumSkyblockNPC plugin;
    private final ConfigManager configManager;

    public IslandCreateListener(IridiumSkyblockNPC plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onIslandCreate(IslandCreateEvent event) {
        UUID playerUUID = event.getUser().getPlayer().getUniqueId();

        new BukkitRunnable() {
            int attempts = 0;

            @Override
            public void run() {
                Optional<Island> islandOptional = event.getUser().getIsland();

                if (islandOptional.isPresent()) {
                    Island island = islandOptional.get();
                    Location location = island.getHome();
                    NpcData data = createNpcData(island, playerUUID, location);

                    Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
                    npc.create();
                    npc.spawnForAll();
                    FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
                    cancel();
                }

                if (++attempts >= 10) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private NpcData createNpcData(Island island, UUID playerUUID, Location location) {
        NpcData data = new NpcData(island.getName(), playerUUID, location);

        data.setDisplayName(
                configManager.getValue(Config.DEFAULT_DISPLAY_NAME).replace("%island_name%", island.getName()));
        data.setMirrorSkin(configManager.getValue(Config.MIRROR_SKIN));
        data.setTurnToPlayer(configManager.getValue(Config.TURN_TO_PLAYER));
        data.setGlowing(configManager.getValue(Config.GLOWING));
        data.setGlowingColor(configManager.getValue(Config.GLOWING_COLOR).toNamedTextColor());
        data.setCollidable(configManager.getValue(Config.COLLIDABLE));
        data.setScale(configManager.getValue(Config.SCALE).floatValue());
        data.setVisibilityDistance(configManager.getValue(Config.VISIBILITY_DISTANCE));

        configManager.getValue(Config.NPC_ACTIONS).forEach(action -> {
            data.addAction(
                    action.getActionTrigger(),
                    action.getOrder(),
                    FancyNpcsPlugin.get().getActionManager().getActionByName(action.getAction()),
                    action.getValue());
        });

        return data;
    }
}