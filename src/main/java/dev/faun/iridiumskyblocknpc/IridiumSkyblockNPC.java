package dev.faun.iridiumskyblocknpc;

import com.iridium.iridiumskyblock.IridiumSkyblock;

import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.commands.MainCommand;
import dev.faun.iridiumskyblocknpc.commands.ReloadCommand;
import dev.faun.iridiumskyblocknpc.commands.TeleportHereCommand;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.gui.ColorSelectorGui;
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

		// setup messages
		MessageSender messageSender = MessageSender.builder()
				.configManager(configManager).build();
		MessageFormatter messageFormatter = MessageFormatter.builder()
				.configManager(configManager).build();

		// Get IridiumSkyblock instance
		IridiumSkyblock iridiumSkyblock = IridiumSkyblock.getInstance();

		// setup navigation
		GuiNavigationManager navigationManager = new GuiNavigationManager();

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
						this, messageSender, navigationManager));

		// NPC Settings GUI factory
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.NPC_SETTINGS,
				(player, data) -> new NpcSettingsGui(configManager, this,
						messageSender, navigationManager));

		// Color selector GUI factory
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.COLOR_SELECTOR, (player, data) -> {
					if (data instanceof Npc npc) {
						return new ColorSelectorGui(configManager,
								messageSender, navigationManager, npc);
					}
					return null;
				});

		// Register the Scale Selector GUI
		navigationManager.registerGuiFactory(
				GuiNavigationManager.GuiType.SCALE_SELECTOR, (player, data) -> {
					if (data instanceof Npc) {
						return new ScaleSelectorGui(configManager, this,
								messageSender, navigationManager);
					}
					return null;
				});

		// Stats GUI factory
		navigationManager.registerGuiFactory(GuiNavigationManager.GuiType.STATS,
				(player, data) -> new StatsGui());
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}
}
