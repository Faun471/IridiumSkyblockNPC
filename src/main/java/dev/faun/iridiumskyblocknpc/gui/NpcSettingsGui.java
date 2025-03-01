package dev.faun.iridiumskyblocknpc.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.Config;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class NpcSettingsGui implements GuiProvider {
	private final ConfigManager configManager;
	private final MessageSender messageSender;
	private final IridiumSkyblockNPC plugin;
	private final GuiNavigationManager navigationManager;
	private final List<String> allowedEditOptions;
	private final boolean allowEdit;
	private final InventoryBean npcSettingsConfig;
	private final MessageFormatter formatter;
	private Npc playerNpc;

	public NpcSettingsGui(ConfigManager configManager,
			IridiumSkyblockNPC plugin, MessageSender messageSender,
			GuiNavigationManager navigationManager) {
		this.configManager = configManager;
		this.messageSender = messageSender;
		this.plugin = plugin;
		this.navigationManager = navigationManager;
		this.allowEdit = configManager.getValue(Config.ALLOW_PLAYER_EDIT);
		this.allowedEditOptions = configManager
				.getValue(Config.ALLOWED_PLAYER_EDIT_OPTIONS);
		this.npcSettingsConfig = configManager
				.getValue(Inventories.NPC_SETTINGS_GUI);
		this.formatter = MessageFormatter.builder().configManager(configManager)
				.build();
		configManager.getValue(Inventories.SIGN_EDITORS);
	}

	@Override
	public void send(Player player) {

		// Find the player's NPC
		playerNpc = findPlayerNpc(player.getUniqueId());

		// If player has no NPC, show error message
		if (playerNpc == null) {
			messageSender.send(player,
					"<red>You don't have an NPC to configure!");
			return;
		}

		// Check if player is allowed to edit NPCs
		if (!allowEdit) {
			messageSender.send(player,
					"<red>You are not allowed to edit NPCs!");
			return;
		}

		// Create GUI from config
		String title = npcSettingsConfig.getTitle();
		int rows = npcSettingsConfig.getRows();

		Gui gui = Gui.gui(GuiType.CHEST)
				.title(formatter.format(title))
				.rows(rows)
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

		// Process all configured items
		for (Map.Entry<String, GuiItemBean> entry : npcSettingsConfig.getItems()
				.entrySet()) {
			String key = entry.getKey();
			GuiItemBean item = entry.getValue();

			// Skip if the position is invalid
			if (item.getSlot() < 0 || item.getSlot() >= rows * 9) {
				continue;
			}

			// Process item based on action
			if (!isOptionAllowed(getOptionKeyFromItemKey(key))) {
				// If option is not allowed, display disabled version
				addDisabledItem(gui, item, player);
			} else {
				// Add the enabled version of the item
				addEnabledItem(gui, key, item, player);
			}
		}

		// Fill empty slots if enabled
		if (configManager.getValue(Inventories.GLOBAL_FILL_EMPTY_SLOTS)) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			gui.getFiller().fill(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		}

		// Register GUI after setting it up but before opening
		navigationManager.registerGui(player,
				GuiNavigationManager.GuiType.NPC_SETTINGS, playerNpc);
		gui.open(player);
	}

	private void addEnabledItem(Gui gui, String key, GuiItemBean item,
			Player player) {
		// Process special properties and create the item
		Material material = getMaterialForItem(key, item, true);
		String displayName = processPlaceholders(item.getName(), player);
		String[] lore = item.getLore() != null
				? processPlaceholders(item.getLore().toArray(String[]::new),
						player)
				: new String[0];

		ItemBuilder builder = ItemBuilder.from(material)
				.name(formatter.format(displayName)
						.decoration(TextDecoration.BOLD, true));

		if (lore.length > 0) {
			builder.lore(Arrays.stream(lore).map(formatter::format)
					.toArray(Component[]::new));
		}

		// Add with click handler based on action
		gui.setItem(item.getSlot(), builder.asGuiItem(event -> {
			handleItemAction(key, item.getAction(), item.getAction(), player);
		}));
	}

	private void addDisabledItem(Gui gui, GuiItemBean item, Player player) {
		// For disabled items, use alternate material if provided
		Material material = getMaterialForItem(null, item, false);
		String displayName = processPlaceholders(item.getName(), player);

		// Create lore list with disabled message
		List<Component> lore = new java.util.ArrayList<>();
		if (item.getLore() != null) {
			for (String line : processPlaceholders(
					item.getLore().stream().toArray(String[]::new), player)) {
				if (!line.isEmpty()) {
					lore.add(formatter.format(line));
				}
			}
		}
		// Add disabled message
		lore.add(Component.empty());
		lore.add(formatter.format("You cannot edit this setting")
				.color(NamedTextColor.RED));

		ItemBuilder builder = ItemBuilder.from(material)
				.name(formatter.format(displayName)
						.color(NamedTextColor.GRAY)
						.decoration(TextDecoration.BOLD, true))
				.lore(lore);

		gui.setItem(item.getSlot(), builder.asGuiItem());
	}

	private Material getMaterialForItem(String key, GuiItemBean item,
			boolean enabled) {
		Material material;

		if (!enabled && item.getMaterialDisabled() != null) {
			// When item is disabled and we have a specific disabled material
			material = item.getMaterialDisabled();
		} else {
			// Use the regular material
			material = item.getMaterial();
		}

		// Handle special dynamic materials
		if (material == null) {
			return Material.BARRIER;
		}

		// For special placeholder materials
		if (material == Material.AIR && key != null
				&& key.equals("glowing-color") && playerNpc != null) {
			return getColorMaterial(playerNpc.getData().getGlowingColor());
		}

		return material;
	}

	private String[] processPlaceholders(String[] text, Player player) {
		if (text == null)
			return new String[0];

		String[] result = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			result[i] = processPlaceholders(text[i], player);
		}
		return result;
	}

	private String processPlaceholders(String text, Player player) {
		if (text == null)
			return "";
		if (playerNpc == null)
			return text;

		// Replace NPC-specific placeholders
		text = text.replace("%npc_name%", playerNpc.getData().getDisplayName());
		text = text.replace("%scale%",
				String.valueOf(playerNpc.getData().getScale()));
		text = text.replace("%visibility_distance%",
				String.valueOf(playerNpc.getData().getVisibilityDistance()));

		// Status placeholders
		boolean turnToPlayer = playerNpc.getData().isTurnToPlayer();
		text = text.replace("%turn_status%",
				turnToPlayer ? "<green>Enabled" : "<red>Disabled");

		boolean mirrorSkin = playerNpc.getData().isMirrorSkin();
		text = text.replace("%mirror_status%",
				mirrorSkin ? "<green>Enabled" : "<red>Disabled");

		boolean glowing = playerNpc.getData().isGlowing();
		text = text.replace("%glowing_status%",
				glowing ? "<green>Enabled" : "<red>Disabled");

		boolean collidable = playerNpc.getData().isCollidable();
		text = text.replace("%collidable_status%",
				collidable ? "<green>Enabled" : "<red>Disabled");

		// Color placeholder
		NamedTextColor glowingColor = playerNpc.getData().getGlowingColor();
		if (glowingColor != null) {
			text = text.replace("%glowing_color%",
					glowingColor.toString().toLowerCase().replace("_", " "));
		}

		return text;
	}

	private void handleItemAction(String key, String action, String actionValue,
			Player player) {
		if (action == null)
			return;

		switch (action.toUpperCase()) {
			case "TOGGLE_TURN_TO_PLAYER":
				toggleBooleanOption(player, "turnToPlayer");
				break;
			case "TOGGLE_MIRROR_SKIN":
				toggleBooleanOption(player, "mirrorSkin");
				break;
			case "TOGGLE_GLOWING":
				toggleBooleanOption(player, "glowing");
				break;
			case "TOGGLE_COLLIDABLE":
				toggleBooleanOption(player, "collidable");
				break;
			case "OPEN_NAME_EDITOR":
				openNameEditor(player);
				break;
			case "OPEN_SCALE_EDITOR":
				openScaleEditor(player);
				break;
			case "OPEN_VISIBILITY_EDITOR":
				openVisibilityDistanceEditor(player);
				break;
			case "OPEN_COLOR_SELECTOR":
				openGlowingColorSelector(player);
				break;
			case "OPEN_ACTIONS_EDITOR":
				openActionsEditor(player);
				break;
			case "SAVE_NPC":
				saveNpcChanges(player);
				messageSender.send(player,
						"<green>NPC settings saved successfully!");
				break;
			case "CLOSE":
				navigateBack(player);
				break;
			case "BACK":
				navigateBack(player);
				break;
			case "SET_COLOR":
				if (actionValue != null) {
					try {
						GlowingColor color = GlowingColor.valueOf(actionValue);
						playerNpc.getData()
								.setGlowingColor(color.toNamedTextColor());
						messageSender.send(player,
								"<green>Updated glowing color!");
						send(player);
					} catch (IllegalArgumentException e) {
						messageSender.send(player, "<red>Invalid color!");
					}
				}
				break;
		}
	}

	private String getOptionKeyFromItemKey(String itemKey) {
		// Map GUI item keys to option permission keys
		return switch (itemKey) {
			case "display-name" -> "display_name";
			case "turn-to-player" -> "turn_to_player";
			case "mirror-skin" -> "mirror_skin";
			case "glowing" -> "glowing";
			case "glowing-color" -> "glowing_color";
			case "collidable" -> "collidable";
			case "scale" -> "scale";
			case "visibility-distance" -> "visibility_distance";
			case "actions" -> "actions";
			// For buttons that should always be shown
			case "save", "close", "back" -> "";
			default -> itemKey.replace('-', '_');
		};
	}

	private Npc findPlayerNpc(UUID playerUuid) {
		return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
				.filter(npc -> npc.getData().getCreator() != null
						&& npc.getData().getCreator().equals(playerUuid))
				.findFirst()
				.orElse(null);
	}

	private void toggleBooleanOption(Player player, String option) {
		switch (option) {
			case "turnToPlayer":
				if (isOptionAllowed("turn_to_player")) {
					playerNpc.getData().setTurnToPlayer(
							!playerNpc.getData().isTurnToPlayer());
				}
				break;
			case "mirrorSkin":
				if (isOptionAllowed("mirror_skin")) {
					playerNpc.getData()
							.setMirrorSkin(!playerNpc.getData().isMirrorSkin());
				}
				break;
			case "glowing":
				if (isOptionAllowed("glowing")) {
					playerNpc.getData()
							.setGlowing(!playerNpc.getData().isGlowing());
				}
				break;
			case "collidable":
				if (isOptionAllowed("collidable")) {
					playerNpc.getData()
							.setCollidable(!playerNpc.getData().isCollidable());
				}
				break;
		}

		// Refresh the GUI
		send(player);
	}

	private Material getColorMaterial(NamedTextColor color) {
		if (color == null)
			return Material.WHITE_WOOL;

		// Convert NamedTextColor to Material
		return switch (color.toString().toLowerCase()) {
			case "black" -> Material.BLACK_WOOL;
			case "dark_blue" -> Material.BLUE_WOOL;
			case "dark_green" -> Material.GREEN_WOOL;
			case "dark_aqua" -> Material.CYAN_WOOL;
			case "dark_red" -> Material.RED_WOOL;
			case "dark_purple" -> Material.PURPLE_WOOL;
			case "gold" -> Material.ORANGE_WOOL;
			case "gray" -> Material.LIGHT_GRAY_WOOL;
			case "dark_gray" -> Material.GRAY_WOOL;
			case "blue" -> Material.LIGHT_BLUE_WOOL;
			case "green" -> Material.LIME_WOOL;
			case "aqua" -> Material.LIGHT_BLUE_WOOL;
			case "red" -> Material.RED_WOOL;
			case "light_purple" -> Material.MAGENTA_WOOL;
			case "yellow" -> Material.YELLOW_WOOL;
			default -> Material.WHITE_WOOL;
		};
	}

	private void openNameEditor(Player player) {
		if (!isOptionAllowed("display_name")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		// Use AnvilEditorUtil to open the name editor
		AnvilEditorUtil.openTextEditor(
				player,
				playerNpc,
				"Edit NPC Name",
				playerNpc.getData().getDisplayName(),
				plugin,
				messageSender,
				(input, npc) -> {
					if (input instanceof TextComponent textComponent
							&& input != null
							&& !textComponent.content().isEmpty()) {
						npc.getData().setDisplayName(MiniMessage.miniMessage()
								.serialize(textComponent));
						messageSender.send(player,
								"<green>Display name updated to <gold>"
										+ textComponent.content() + "<green>!",
								new HashMap<String, String>() {
									{
										put("npc_name",
												textComponent.content());
									}
								});
					} else {
						messageSender.send(player,
								"<red>Name cannot be empty!");
					}
				},
				() -> navigationManager.reopenCurrentGui(player));
	}

	private void openGlowingColorSelector(Player player) {
		if (!isOptionAllowed("glowing_color")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		// Use navigateTo to open the color selector
		navigationManager.navigateTo(player,
				GuiNavigationManager.GuiType.COLOR_SELECTOR, playerNpc);
	}

	private void openScaleEditor(Player player) {
		if (!isOptionAllowed("scale")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		// Use navigateTo to open the scale selector
		navigationManager.navigateTo(player,
				GuiNavigationManager.GuiType.SCALE_SELECTOR, playerNpc);
	}

	private void openVisibilityDistanceEditor(Player player) {
		if (!isOptionAllowed("visibility_distance")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		// Use AnvilEditorUtil to open the visibility editor
		AnvilEditorUtil.openTextEditor(
				player,
				playerNpc,
				"Edit Visibility Distance",
				String.valueOf(playerNpc.getData().getVisibilityDistance()),
				plugin,
				messageSender,
				(input, npc) -> {
					if (input instanceof TextComponent textComponent) {
						try {
							int distance = Integer
									.parseInt(textComponent.content());
							if (distance > 0) {
								npc.getData().setVisibilityDistance(distance);
								messageSender.send(player,
										"<green>Visibility distance updated to <yellow>"
												+ distance
												+ "<green> blocks!");
							} else {
								messageSender.send(player,
										"<red>Distance must be greater than 0!");
							}
						} catch (NumberFormatException e) {
							messageSender.send(player,
									"<red>Invalid number format!");
						}
					}
				},
				() -> navigationManager.reopenCurrentGui(player));
	}

	private void openActionsEditor(Player player) {
		if (!isOptionAllowed("actions")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		// This would be a more complex GUI to edit NPC actions
		// For now, just show a simple message
		messageSender.send(player,
				"<red>Actions editor is not yet implemented.",
				"<yellow>This would allow configuring what happens when a player interacts with the NPC.");
	}

	private void navigateBack(Player player) {
		// Use the navigation manager to go back to the previous GUI
		messageSender.send(player, "<gray>Returning to previous menu " +
				navigationManager.getPreviousGui(player).getType());

		navigationManager.navigateBack(player);
	}

	private void saveNpcChanges(Player player) {
		// Apply any pending changes and save the NPC data
		FancyNpcsPlugin.get().getNpcManager().saveNpcs(true);

		// Refresh the NPC for all players
		playerNpc.removeForAll();
		playerNpc.spawnForAll();
	}

	/**
	 * Check if the specified option is allowed for players to edit
	 * 
	 * @param option
	 *            The option to check
	 * @return True if the option is allowed, false otherwise
	 */
	private boolean isOptionAllowed(String option) {
		return allowEdit
				&& (option.isEmpty() || allowedEditOptions.contains(option));
	}
}