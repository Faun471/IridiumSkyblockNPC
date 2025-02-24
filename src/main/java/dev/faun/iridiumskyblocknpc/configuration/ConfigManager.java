package dev.faun.iridiumskyblocknpc.configuration;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.properties.Property;
import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class ConfigManager {
    private final IridiumSkyblockNPC plugin;
    private final HashMap<Configs, SettingsManager> configs = new HashMap<>();

    public ConfigManager(IridiumSkyblockNPC plugin) {
        this.plugin = plugin;
    }

    public void reloadConfigs() {
        configs.clear();
        loadConfig(Configs.CONFIG);
    }

    public void loadConfig(Configs name) {
        String fileName = name.name().toLowerCase() + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        SettingsManager settingsManager = initSettings(name, file);
        configs.put(name, settingsManager);
    }

    public SettingsManager getConfig(Configs name) {
        if (!configs.containsKey(name)) {
            loadConfig(name);
        }

        return configs.get(name);
    }

    public SettingsManager initSettings(Configs name, File config) {
        Class<? extends SettingsHolder> clazz = switch (name) {
            case CONFIG -> Config.class;
        };

        Path configFile = Path.of(config.getPath());
        return SettingsManagerBuilder
                .withYamlFile(configFile)
                .configurationData(clazz)
                .useDefaultMigrationService()
                .create();
    }

    public <T> T getValue(Property<T> property) {
        Configs configSource = Configs.CONFIG;
        SettingsManager settingsManager = getConfig(configSource);
        return settingsManager.getProperty(property);
    }
}