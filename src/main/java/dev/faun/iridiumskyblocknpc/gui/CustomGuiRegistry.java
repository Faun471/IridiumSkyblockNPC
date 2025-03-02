package dev.faun.iridiumskyblocknpc.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.entity.Player;

import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Inventories;
import dev.faun.iridiumskyblocknpc.configuration.beans.InventoryBean;
import dev.faun.iridiumskyblocknpc.services.MessageSender;

/**
 * Registry for custom GUIs defined in configuration
 */
public class CustomGuiRegistry {

	private final ConfigManager configManager;
	private final MessageSender messageSender;
	private final GuiNavigationManager navigationManager;
	private final Map<String, String> guiConfigPaths;
	private final Map<String, Function<Player, Object>> dataProviders;

	/**
	 * Create a new CustomGuiRegistry
	 * 
	 * @param configManager
	 *            The config manager
	 * @param messageSender
	 *            The message sender
	 * @param navigationManager
	 *            The navigation manager
	 */
	public CustomGuiRegistry(ConfigManager configManager,
			MessageSender messageSender,
			GuiNavigationManager navigationManager) {
		this.configManager = configManager;
		this.messageSender = messageSender;
		this.navigationManager = navigationManager;
		this.guiConfigPaths = new HashMap<>();
		this.dataProviders = new HashMap<>();

		registerGuiFactory();
	}

	/**
	 * Register a custom GUI from config
	 * 
	 * @param guiId
	 *            The ID of the GUI
	 * @param configPath
	 *            The path to the GUI config
	 */
	public void registerGuiConfig(String guiId, String configPath) {
		guiConfigPaths.put(guiId, configPath);
	}

	/**
	 * Register a custom data provider for a GUI
	 * 
	 * @param guiId
	 *            The ID of the GUI
	 * @param dataProvider
	 *            A function that returns data for the GUI based on the player
	 */
	public void registerDataProvider(String guiId,
			Function<Player, Object> dataProvider) {
		dataProviders.put(guiId, dataProvider);
	}

	/**
	 * Get the GUI config for a GUI ID
	 * 
	 * @param guiId
	 *            The ID of the GUI
	 * @return The GUI config or null if not found
	 */
	public InventoryBean getGuiConfig(String guiId) {
		return configManager.getValue(Inventories.CUSTOM_GUIS).get(guiId);
	}

	/**
	 * Get custom data for a GUI
	 * 
	 * @param guiId
	 *            The ID of the GUI
	 * @param player
	 *            The player
	 * @return The custom data or null if no provider is registered
	 */
	public Object getGuiData(String guiId, Player player) {
		Function<Player, Object> provider = dataProviders.get(guiId);
		if (provider == null) {
			return null;
		}

		return provider.apply(player);
	}

	/**
	 * Register the GUI factory with the navigation manager
	 */
	private void registerGuiFactory() {
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.CUSTOM,
				(player, data) -> {
					if (data instanceof String guiId) {
						InventoryBean guiConfig = getGuiConfig(guiId);
						if (guiConfig != null) {
							Object customData = getGuiData(guiId, player);
							return new ConfigurableGui(guiConfig, configManager,
									messageSender,
									navigationManager, this, player, guiId,
									customData);
						}
					}
					return null;
				});
	}

	/**
	 * Open a custom GUI for a player
	 * 
	 * @param player
	 *            The player
	 * @param guiId
	 *            The ID of the GUI
	 * @return true if the GUI was opened successfully
	 */
	public boolean openGui(Player player, String guiId) {
		return navigationManager.navigateTo(player,
				GuiNavigationManager.GuiType.CUSTOM, guiId);
	}
}