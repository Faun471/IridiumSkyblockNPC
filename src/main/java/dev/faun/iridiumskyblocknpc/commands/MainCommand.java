package dev.faun.iridiumskyblocknpc.commands;

import org.bukkit.entity.Player;

import com.iridium.iridiumskyblock.IridiumSkyblock;

import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.gui.GuiNavigationManager;
import dev.faun.iridiumskyblocknpc.gui.NpcGui;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.cmd.core.annotations.Command;

@Command(value = "iridiumskyblocknpc", alias = { "isnpc" })
public class MainCommand {
	private final IridiumSkyblock iridiumSkyblock;
	private final ConfigManager configManager;
	private final IridiumSkyblockNPC plugin;
	private final MessageSender messageSender;
	private final GuiNavigationManager navigationManager;

	public MainCommand(IridiumSkyblock iridiumSkyblockAPI, ConfigManager configManager, IridiumSkyblockNPC plugin,
			MessageSender messageSender, GuiNavigationManager navigationManager) {
		this.iridiumSkyblock = iridiumSkyblockAPI;
		this.configManager = configManager;
		this.plugin = plugin;
		this.messageSender = messageSender;
		this.navigationManager = navigationManager;
	}

	@Command
	public void onCommand(Player player) {
		NpcGui npcGui = new NpcGui(iridiumSkyblock, configManager, plugin, messageSender, navigationManager);
		npcGui.send(player);
	}
}
