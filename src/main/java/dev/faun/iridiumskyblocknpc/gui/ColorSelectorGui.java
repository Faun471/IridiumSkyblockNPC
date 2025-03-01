package dev.faun.iridiumskyblocknpc.gui;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Inventories;
import dev.faun.iridiumskyblocknpc.configuration.beans.GuiItemBean;
import dev.faun.iridiumskyblocknpc.configuration.beans.InventoryBean;
import dev.faun.iridiumskyblocknpc.enums.GlowingColor;
import dev.faun.iridiumskyblocknpc.services.MessageFormatter;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * GUI for selecting NPC glowing colors
 */
public class ColorSelectorGui implements GuiProvider {

	private final ConfigManager configManager;
	private final MessageSender messageSender;
	private final GuiNavigationManager navigationManager;
	private final Npc playerNpc;

	/**
	 * Constructor for ColorSelectorGui
	 * 
	 * @param configManager
	 *            The config manager
	 * @param messageSender
	 *            The message sender service
	 * @param navigationManager
	 *            The GUI navigation manager
	 * @param playerNpc
	 *            The player's NPC
	 */
	public ColorSelectorGui(ConfigManager configManager,
			MessageSender messageSender,
			GuiNavigationManager navigationManager, Npc playerNpc) {
		this.configManager = configManager;
		this.messageSender = messageSender;
		this.navigationManager = navigationManager;
		this.playerNpc = playerNpc;
	}

	/**
	 * Open the color selector GUI for a player
	 * 
	 * @param player
	 *            The player to show the GUI to
	 */
	@Override
	public void send(Player player) {
		InventoryBean colorSelectorConfig = configManager
				.getValue(Inventories.COLOR_SELECTOR_GUI);

		// Create GUI from config
		String title = colorSelectorConfig.getTitle();
		int rows = colorSelectorConfig.getRows();

		MessageFormatter formatter = MessageFormatter.builder()
				.configManager(configManager).build();

		Gui colorGui = Gui.gui(GuiType.CHEST)
				.title(formatter.text(title).buildComponent())
				.rows(rows)
				.disableAllInteractions()
				.create();

		// Fill border if enabled
		if (colorSelectorConfig.getBorder()) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			colorGui.getFiller().fillBorder(ItemBuilder.from(fillMaterial)
					.name(formatter.text(fillName)
							.buildComponent())
					.asGuiItem());
		}

		// Add all colors from configuration
		for (Map.Entry<String, GuiItemBean> entry : colorSelectorConfig
				.getItems().entrySet()) {
			GuiItemBean item = entry.getValue();

			// Skip if position is invalid
			if (item.getSlot() < 0 || item.getSlot() >= rows * 9) {
				continue;
			}

			// Create item and add click handler for the color
			String displayName = item.getName();
			Material material = item.getMaterial();

			try {
				if (item.getColor() != null && !item.getColor().isEmpty()) {
					GlowingColor color = GlowingColor.valueOf(item.getColor());
					boolean isSelected = playerNpc.getData()
							.getGlowingColor() == color.toNamedTextColor();

					colorGui.setItem(item.getSlot(),
							ItemBuilder.from(material)
									.name(formatter
											.text(displayName)
											.buildComponent()
											.decoration(TextDecoration.BOLD,
													true))
									.glow(isSelected)
									.asGuiItem(event -> {
										if ("SET_COLOR"
												.equals(item.getAction())) {
											playerNpc.getData().setGlowingColor(
													color.toNamedTextColor());
											messageSender.send(player,
													"<green>Updated glowing color!");
											// Go back to previous GUI
											navigateBack(player);
										}
									}));
				}
				// Handle back button and other non-color items
				else if (item.getAction().equals("BACK")) {
					colorGui.setItem(item.getSlot(),
							ItemBuilder.from(material)
									.name(formatter
											.text(displayName)
											.buildComponent()
											.decoration(TextDecoration.BOLD,
													true))
									.asGuiItem(event -> navigateBack(player)));
				}
			} catch (IllegalArgumentException e) {
				// Skip invalid color entries
			}
		}

		// Add back button if not already defined in config
		if (!colorSelectorConfig.getItems().containsKey("back")) {
			colorGui.setItem(colorSelectorConfig.getRows() * 9 - 1,
					ItemBuilder.from(Material.ARROW)
							.name(formatter
									.text("Back")
									.buildComponent()
									.decoration(TextDecoration.BOLD, true))
							.asGuiItem(event -> navigateBack(player)));
		}

		// Fill empty slots if enabled
		if (configManager.getValue(Inventories.GLOBAL_FILL_EMPTY_SLOTS)) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			colorGui.getFiller().fill(ItemBuilder.from(fillMaterial)
					.name(formatter.text(fillName)
							.buildComponent())
					.asGuiItem());
		}

		// Register GUI after setup but before opening
		navigationManager.registerGui(player,
				GuiNavigationManager.GuiType.COLOR_SELECTOR, playerNpc);
		colorGui.open(player);
	}

	/**
	 * Navigate back to the previous GUI
	 * 
	 * @param player
	 *            The player to navigate
	 */
	private void navigateBack(Player player) {
		// Use the navigation manager to go back to the previous GUI
		navigationManager.navigateBack(player);
	}
}