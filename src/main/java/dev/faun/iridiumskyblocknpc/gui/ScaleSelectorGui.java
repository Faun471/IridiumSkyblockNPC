package dev.faun.iridiumskyblocknpc.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.Config;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Inventories;
import dev.faun.iridiumskyblocknpc.services.MessageFormatter;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;

public class ScaleSelectorGui implements GuiProvider {

	private final ConfigManager configManager;
	private final MessageSender messageSender;
	private final IridiumSkyblockNPC plugin;
	private final GuiNavigationManager navigationManager;
	private final MessageFormatter formatter;

	private Npc npc;
	private final boolean allowEdit;

	public ScaleSelectorGui(ConfigManager configManager,
			IridiumSkyblockNPC plugin,
			MessageSender messageSender,
			GuiNavigationManager navigationManager) {
		this.configManager = configManager;
		this.messageSender = messageSender;
		this.formatter = MessageFormatter.builder()
				.configManager(configManager)
				.build();
		this.plugin = plugin;
		this.navigationManager = navigationManager;
		this.allowEdit = configManager.getValue(Config.ALLOW_PLAYER_EDIT);
	}

	@Override
	public void send(Player player) {
		// Get the current NPC being edited
		npc = navigationManager.getCurrentGui(player).getData() instanceof Npc
				? (Npc) navigationManager.getCurrentGui(player).getData()
				: null;

		if (npc == null) {
			messageSender.send(player, "<red>No NPC selected to edit.");
			return;
		}

		if (!allowEdit
				|| !configManager.getValue(Config.ALLOWED_PLAYER_EDIT_OPTIONS)
						.contains("scale")) {
			messageSender.send(player,
					"<red>You are not allowed to edit this setting!");
			return;
		}

		// Create the GUI
		Gui gui = Gui.gui()
				.title(formatter.format("<green>Adjust NPC Scale"))
				.rows(3)
				.disableAllInteractions()
				.create();

		// Fill border if enabled
		if (configManager
				.getValue(Inventories.GLOBAL_FILL_ITEM) != Material.AIR) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			gui.getFiller().fillBorder(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		}

		// Current scale display
		float currentScale = npc.getData().getScale();
		gui.setItem(13, ItemBuilder.from(Material.NAME_TAG)
				.name(formatter
						.format("<yellow>Current Scale: <white>" + currentScale)
						.decoration(TextDecoration.ITALIC, false))
				.asGuiItem());

		// Decrement buttons (left side)
		gui.setItem(10,
				createButton(Material.RED_CONCRETE, "<red>-10", -10.0f));
		gui.setItem(11, createButton(Material.RED_CONCRETE, "<red>-1", -1.0f));
		gui.setItem(12,
				createButton(Material.RED_CONCRETE, "<red>-0.1", -0.1f));

		// Increment buttons (right side)
		gui.setItem(14,
				createButton(Material.LIME_CONCRETE, "<green>+0.1", 0.1f));
		gui.setItem(15,
				createButton(Material.LIME_CONCRETE, "<green>+1", 1.0f));
		gui.setItem(16,
				createButton(Material.LIME_CONCRETE, "<green>+10", 10.0f));

		// Custom input button
		gui.setItem(22, ItemBuilder.from(Material.OAK_SIGN)
				.name(formatter.format("<yellow>Type Custom Value")
						.decoration(TextDecoration.ITALIC, false))
				.lore(formatter.format("<gray>Click to enter a specific value"))
				.asGuiItem(event -> {
					player.closeInventory();
					openAnvilEditor(player, npc);
				}));

		// Cancel button
		gui.setItem(18, ItemBuilder.from(Material.BARRIER)
				.name(formatter.format("<red>Back")
						.decoration(TextDecoration.ITALIC, false))
				.asGuiItem(event -> {
					navigationManager.navigateBack(player);
				}));

		// Save button
		gui.setItem(26, ItemBuilder.from(Material.EMERALD)
				.name(formatter.format("<green>Save")
						.decoration(TextDecoration.ITALIC, false))
				.asGuiItem(event -> {
					// Save NPC changes
					navigationManager.navigateBack(player);
				}));

		// Register and open the GUI
		navigationManager.registerGui(player,
				GuiNavigationManager.GuiType.SCALE_SELECTOR, npc);
		gui.open(player);
	}

	/**
	 * Creates a button that adjusts the NPC scale by the given amount
	 */
	private GuiItem createButton(Material material, String label,
			float adjustment) {
		return ItemBuilder.from(material)
				.name(formatter.format(label)
						.decoration(TextDecoration.ITALIC, false))
				.asGuiItem(event -> {
					float currentScale = npc.getData().getScale();
					float newScale = Math.max(0.1f, currentScale + adjustment);

					// Update the scale
					npc.getData().setScale(newScale);

					// Refresh the GUI
					send((Player) event.getWhoClicked());

					// Show message
					messageSender.send((Player) event.getWhoClicked(),
							Component.text("Scale adjusted to: " + newScale));
				});
	}

	/**
	 * Opens an anvil GUI for custom scale input
	 */
	private void openAnvilEditor(Player player, Npc npc) {
		float currentScale = npc.getData().getScale();

		AnvilEditorUtil.openTextEditor(
				player,
				npc,
				"Enter Scale Value",
				String.valueOf(currentScale),
				plugin,
				messageSender,
				(input, targetNpc) -> {
					try {
						float scale = Float
								.parseFloat(((TextComponent) input).content());
						if (scale > 0) {
							targetNpc.getData().setScale(scale);
							messageSender.send(player,
									"<green>Scale updated to <yellow>" + scale);
						} else {
							messageSender.send(player,
									"<red>Scale must be greater than 0");
						}
					} catch (NumberFormatException e) {
						messageSender.send(player,
								"<red>Invalid number: <yellow>" + input);
					}
				},
				() -> navigationManager.reopenCurrentGui(player));
	}
}