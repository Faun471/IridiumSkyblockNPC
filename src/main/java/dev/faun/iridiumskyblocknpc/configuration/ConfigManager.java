package dev.faun.iridiumskyblocknpc.configuration;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.properties.Property;
import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
	private final IridiumSkyblockNPC plugin;

	private final Map<Class<? extends SettingsHolder>, SettingsManager> configs = new HashMap<>();
	private final Map<Class<? extends SettingsHolder>, String> configFileNames = new HashMap<>();
	private final Map<String, Class<? extends SettingsHolder>> propertyPrefixToConfig = new HashMap<>();

	public ConfigManager(IridiumSkyblockNPC plugin) {
		this.plugin = plugin;
		registerConfig(Config.class, "config.yml", "config");
		registerConfig(Messages.class, "messages.yml", "messages");
		registerConfig(Inventories.class, "inventories.yml", "inventories");
	}

	public SettingsManager getSettingsManager(
			Class<? extends SettingsHolder> configClass) {
		return getConfig(configClass);
	}

	private void registerConfig(Class<? extends SettingsHolder> configClass,
			String fileName, String propertyPrefix) {
		configFileNames.put(configClass, fileName);
		propertyPrefixToConfig.put(propertyPrefix, configClass);
	}

	public void reloadConfigs() {
		configs.clear();
		configFileNames.keySet().forEach(this::loadConfig);
	}

	private void loadConfig(Class<? extends SettingsHolder> configClass) {
		String fileName = configFileNames.get(configClass);
		File file = new File(plugin.getDataFolder(), fileName);

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		SettingsManager settingsManager = SettingsManagerBuilder
				.withYamlFile(Path.of(file.getPath()))
				.configurationData(configClass)
				.useDefaultMigrationService()
				.create();

		configs.put(configClass, settingsManager);
	}

	private SettingsManager getConfig(
			Class<? extends SettingsHolder> configClass) {
		if (!configs.containsKey(configClass)) {
			loadConfig(configClass);
		}
		return configs.get(configClass);
	}

	public <T> T getValue(Property<T> property) {
		String propertyPath = property.getPath();
		String prefix = propertyPath.split("\\.")[0];

		Class<? extends SettingsHolder> configClass = propertyPrefixToConfig
				.get(prefix);
		if (configClass == null) {
			throw new IllegalArgumentException(
					"No config found for property prefix: " + prefix);
		}

		SettingsManager settingsManager = getConfig(configClass);
		return settingsManager.getProperty(property);
	}
}