package dev.faun.iridiumskyblocknpc;

import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.listeners.IslandCreateListener;
import dev.faun.iridiumskyblocknpc.listeners.IslandDeleteListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class IridiumSkyblockNPC extends JavaPlugin {
    private static IridiumSkyblockNPC instance;

    public static IridiumSkyblockNPC getInstance() {
        return instance;
    }

    public IridiumSkyblockNPC() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // setup config
        ConfigManager configManager = new ConfigManager(this);
        configManager.reloadConfigs();

        // enable listener
        getLogger().info("--------------------");
        getLogger().info("");
        getLogger().info("IridiumSkyblockNPC enabled");
        getLogger().info("");
        getLogger().info("--------------------");

        Bukkit.getPluginManager().registerEvents(new IslandCreateListener(this, configManager), this);
        Bukkit.getPluginManager().registerEvents(new IslandDeleteListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
