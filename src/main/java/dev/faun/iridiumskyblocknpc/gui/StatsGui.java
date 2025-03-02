package dev.faun.iridiumskyblocknpc.gui;

import org.bukkit.entity.Player;

import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;

public class StatsGui extends AbstractGui {

	public StatsGui(ConfigManager configManager, MessageSender messageSender,
			GuiNavigationManager navigationManager) {
		super(configManager, messageSender, navigationManager, null);
	}

	@Override
	public void send(Player player) {
		Gui gui = Gui.gui(GuiType.CHEST)
				.title(Component.text("Stats"))
				.rows(3)
				.disableAllInteractions()
				.create();

		navigationManager.registerGui(player,
				GuiNavigationManager.GuiType.STATS, null);
		gui.open(player);
	}
}
