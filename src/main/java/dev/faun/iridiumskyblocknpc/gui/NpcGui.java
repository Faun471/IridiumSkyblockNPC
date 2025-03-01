package dev.faun.iridiumskyblocknpc.gui;

import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.iridium.iridiumskyblock.IridiumSkyblock;

public class NpcGui implements GuiProvider {
	private final IridiumSkyblock iridiumSkyblock;
	private final GuiNavigationManager navigationManager;

	public NpcGui(IridiumSkyblock iridiumSkyblock, ConfigManager configManager, IridiumSkyblockNPC plugin,
			MessageSender messageSender, GuiNavigationManager navigationManager) {
		this.iridiumSkyblock = iridiumSkyblock;
		this.navigationManager = navigationManager;
	}

	@Override
	public void send(Player player) {
		// Clear previous history as this is the main entry point
		navigationManager.clearHistory(player);

		Gui gui = Gui.gui(GuiType.CHEST)
				.title(Component.text("IridiumSkyblock"))
				.rows(3)
				.disableAllInteractions()
				.create();

		GuiItem npcSettings = ItemBuilder.skull()
				.owner(player)
				.name(Component.text("NPC Settings"))
				.asGuiItem(e -> {
					// Use navigateTo to open NPC settings
					navigationManager.navigateTo(player, GuiNavigationManager.GuiType.NPC_SETTINGS, null);
				});

		GuiItem islandSettings = ItemBuilder.from(Material.GRASS_BLOCK)
				.name(Component.text("Island Settings"))
				.asGuiItem(e -> {
					// Open the island gui from iridium skyblock
					((Player) e.getWhoClicked()).performCommand(iridiumSkyblock.getCommandManager().getCommand());
				});

		GuiItem stats = ItemBuilder.from(Material.BOOK)
				.name(Component.text("Stats"))
				.asGuiItem(e -> {
					// Use navigateTo to open stats
					navigationManager.navigateTo(player, GuiNavigationManager.GuiType.STATS, null);
				});

		GuiItem close = ItemBuilder.from(Material.BARRIER)
				.name(Component.text("Close"))
				.asGuiItem(e -> gui.close(player));

		gui.setItem(11, npcSettings);
		gui.setItem(13, islandSettings);
		gui.setItem(15, stats);
		gui.setItem(22, close);

		// Register this GUI when opened
		navigationManager.registerGui(player, GuiNavigationManager.GuiType.MAIN, null);
		gui.open(player);
	}
}
