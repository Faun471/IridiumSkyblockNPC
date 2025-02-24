package dev.faun.iridiumskyblocknpc.listeners;

    import com.iridium.iridiumskyblock.IridiumSkyblock;
    import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
    import com.iridium.iridiumskyblock.database.Island;
    import de.oliver.fancynpcs.api.FancyNpcsPlugin;
    import de.oliver.fancynpcs.api.NpcManager;
    import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
    import org.bukkit.Bukkit;
    import org.bukkit.Location;
    import org.bukkit.World;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;

    public class IslandDeleteListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onIslandDelete(IslandDeleteEvent event) {
            IridiumSkyblockNPC.getInstance().getLogger().info("IslandDeleteEvent triggered for " + event.getIsland().getName());
            Island island = event.getIsland();

            removeAllNpcsForIsland(island);
        }

        private void removeAllNpcsForIsland(Island island) {
            World world = Bukkit.getWorld(IridiumSkyblock.getInstance().getConfiguration().worldName);
            Location pos1 = island.getPosition1(world);
            Location pos2 = island.getPosition2(world);

            NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
            npcManager.getAllNpcs().stream()
                    .filter(npc -> isInsideBounds(npc.getData().getLocation(), pos1, pos2))
                    .forEach(npc -> {
                        npc.removeForAll();
                        npcManager.removeNpc(npc);
                    });
        }

        private boolean isInsideBounds(Location location, Location min, Location max) {
            return location.getX() >= min.getX() && location.getX() <= max.getX() &&
                    location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
        }
    }