package dev.faun.iridiumskyblocknpc.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.iridium.iridiumskyblock.IridiumSkyblock;

import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;

public class NpcGui extends AbstractGui {
	private final IridiumSkyblock iridiumSkyblock;

	public NpcGui(IridiumSkyblock iridiumSkyblock, ConfigManager configManager,
			IridiumSkyblockNPC plugin, MessageSender messageSender,
			GuiNavigationManager navigationManager, Player player) {
		super(configManager, messageSender, navigationManager, player);
		this.iridiumSkyblock = iridiumSkyblock;
	}

	@Override
	public void send(Player player) {
		navigationManager.clearHistory(player);

		Gui gui = Gui.gui(GuiType.CHEST)
				.title(Component.text("IridiumSkyblock"))
				.rows(3)
				.disableAllInteractions()
				.create();

		// NPC Settings button
		gui.setItem(11, ItemBuilder.skull()
				.owner(player)
				.name(Component.text("NPC Settings"))
				.asGuiItem(e -> navigationManager.navigateTo(player,
						GuiNavigationManager.GuiType.NPC_SETTINGS, null)));

		// Island Settings button
		gui.setItem(13, ItemBuilder.from(Material.GRASS_BLOCK)
				.name(Component.text("Island Settings"))
				.asGuiItem(e -> ((Player) e.getWhoClicked()).performCommand(
						iridiumSkyblock.getCommandManager().getCommand())));

		// Stats button
		gui.setItem(15, ItemBuilder.from(Material.BOOK)
				.name(Component.text("Stats"))
				.asGuiItem(e -> navigationManager.navigateTo(player,
						GuiNavigationManager.GuiType.STATS, null)));

		// Close button
		gui.setItem(22, ItemBuilder.from(Material.BARRIER)
				.name(Component.text("Close"))
				.asGuiItem(e -> gui.close(player)));

		// Register this GUI when opened
		navigationManager.registerGui(player, GuiNavigationManager.GuiType.MAIN,
				null);
		gui.open(player);
	}
}
