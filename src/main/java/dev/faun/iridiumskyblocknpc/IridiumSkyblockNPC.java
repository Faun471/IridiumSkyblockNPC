package dev.faun.iridiumskyblocknpc;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.commands.MainCommand;
import dev.faun.iridiumskyblocknpc.commands.ReloadCommand;
import dev.faun.iridiumskyblocknpc.commands.TeleportHereCommand;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Inventories;
import dev.faun.iridiumskyblocknpc.configuration.beans.InventoryBean;
import dev.faun.iridiumskyblocknpc.gui.ColorSelectorGui;
import dev.faun.iridiumskyblocknpc.gui.CustomGuiRegistry;
import dev.faun.iridiumskyblocknpc.gui.GuiNavigationManager;
import dev.faun.iridiumskyblocknpc.gui.NpcGui;
import dev.faun.iridiumskyblocknpc.gui.NpcSettingsGui;
import dev.faun.iridiumskyblocknpc.gui.ScaleSelectorGui;
import dev.faun.iridiumskyblocknpc.gui.StatsGui;
import dev.faun.iridiumskyblocknpc.listeners.IslandCreateListener;
import dev.faun.iridiumskyblocknpc.listeners.IslandDeleteListener;
import dev.faun.iridiumskyblocknpc.services.MessageFormatter;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class IridiumSkyblockNPC extends JavaPlugin {
	private static IridiumSkyblockNPC instance;

	private ConfigManager configManager;
	private MessageSender messageSender;
	private GuiNavigationManager navigationManager;
	private CustomGuiRegistry customGuiRegistry;

	public static IridiumSkyblockNPC getInstance() {
		return instance;
	}

	public IridiumSkyblockNPC() {
		instance = this;
	}

	@Override
	public void onEnable() {
		// setup config
		configManager = new ConfigManager(this);
		configManager.reloadConfigs();

		// setup messages
		messageSender = MessageSender.builder()
				.configManager(configManager).build();
		MessageFormatter messageFormatter = MessageFormatter.builder()
				.configManager(configManager).build();

		// Get IridiumSkyblock instance
		IridiumSkyblock iridiumSkyblock = IridiumSkyblock.getInstance();

		// setup navigation
		navigationManager = new GuiNavigationManager();

		// Initialize custom GUI registry
		customGuiRegistry = new CustomGuiRegistry(configManager, messageSender,
				navigationManager);

		// Register GUI factories
		registerGuiFactories(navigationManager, configManager, messageSender,
				iridiumSkyblock);

		// setup commands
		BukkitCommandManager<CommandSender> manager = BukkitCommandManager
				.create(this);
		manager.registerCommand(
				new MainCommand(iridiumSkyblock, configManager, this,
						messageSender, navigationManager));
		manager.registerCommand(
				new TeleportHereCommand(messageSender, messageFormatter));
		manager.registerCommand(
				new ReloadCommand(configManager, messageSender,
						messageFormatter, this));

		// enable listeners
		Bukkit.getPluginManager().registerEvents(
				new IslandCreateListener(this, configManager), this);
		Bukkit.getPluginManager().registerEvents(new IslandDeleteListener(),
				this);

		// Register any custom GUIs from config
		registerCustomGuis();
	}

	/**
	 * Register GUI factories for the navigation manager
	 * 
	 * @param navigationManager
	 *            The navigation manager
	 * @param configManager
	 *            The config manager
	 * @param messageSender
	 *            The message sender
	 * @param iridiumSkyblock
	 *            The IridiumSkyblock instance
	 */
	private void registerGuiFactories(GuiNavigationManager navigationManager,
			ConfigManager configManager,
			MessageSender messageSender, IridiumSkyblock iridiumSkyblock) {

		// Main GUI factory
		navigationManager.registerGuiFactory(GuiNavigationManager.GuiType.MAIN,
				(player, data) -> new NpcGui(iridiumSkyblock, configManager,
						this, messageSender, navigationManager, player));

		// NPC Settings GUI factory
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.NPC_SETTINGS,
				(player, data) -> new NpcSettingsGui(configManager, this,
						messageSender, navigationManager, player));

		// Color selector GUI factory
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.COLOR_SELECTOR, (player, data) -> {
					if (data instanceof Npc npc) {
						return new ColorSelectorGui(configManager,
								messageSender, navigationManager, npc, player);
					}
					return null;
				});

		// Register the Scale Selector GUI
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.SCALE_SELECTOR, (player, data) -> {
					if (data instanceof Npc) {
						return new ScaleSelectorGui(configManager, this,
								messageSender, navigationManager, player);
					}
					return null;
				});

		// Stats GUI factory
		navigationManager.registerGuiFactory(GuiNavigationManager.GuiType.STATS,
				(player, data) -> new StatsGui(configManager, messageSender,
						navigationManager));
	}

	/**
	 * Register custom GUIs from the config
	 */
	private void registerCustomGuis() {
		// Get all custom GUIs from config
		Map<String, InventoryBean> customGuis = configManager
				.getValue(Inventories.CUSTOM_GUIS);

		if (customGuis == null || customGuis.isEmpty()) {
			getLogger().info("No custom GUIs found in configuration.");
			return;
		}

		// Register each custom GUI
		for (Map.Entry<String, InventoryBean> entry : customGuis.entrySet()) {
			String guiId = entry.getKey();
			String configPath = "inventories.custom-guis." + guiId;

			customGuiRegistry.registerGuiConfig(guiId, configPath);
			getLogger().info("Registered custom GUI: " + guiId
					+ " with config path: " + configPath);
		}
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	/**
	 * Get the config manager instance
	 * 
	 * @return The config manager
	 */
	public ConfigManager getConfigManager() {
		return configManager;
	}

	/**
	 * Get the message sender instance
	 * 
	 * @return The message sender
	 */
	public MessageSender getMessageSender() {
		return messageSender;
	}

	/**
	 * Get the GUI navigation manager instance
	 * 
	 * @return The GUI navigation manager
	 */
	public GuiNavigationManager getNavigationManager() {
		return navigationManager;
	}

	/**
	 * Get the custom GUI registry instance
	 * 
	 * @return The custom GUI registry
	 */
	public CustomGuiRegistry getCustomGuiRegistry() {
		return customGuiRegistry;
	}
}
