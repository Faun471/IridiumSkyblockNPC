package dev.faun.iridiumskyblocknpc.gui;

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
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class NpcSettingsGui extends AbstractGui {
	private final IridiumSkyblockNPC plugin;
	private final List<String> allowedEditOptions;
	private final boolean allowEdit;
	private final InventoryBean npcSettingsConfig;
	private Npc playerNpc;

	public NpcSettingsGui(ConfigManager configManager,
			IridiumSkyblockNPC plugin, MessageSender messageSender,
			GuiNavigationManager navigationManager, Player player) {
		super(configManager, messageSender, navigationManager, player);
		this.plugin = plugin;
		this.allowEdit = configManager.getValue(Config.ALLOW_PLAYER_EDIT);
		this.allowedEditOptions = configManager
				.getValue(Config.ALLOWED_PLAYER_EDIT_OPTIONS);
		this.npcSettingsConfig = configManager
				.getValue(Inventories.NPC_SETTINGS_GUI);
	}

	@Override
	public void send(Player player) {
		playerNpc = findPlayerNpc(player.getUniqueId());

		if (playerNpc == null) {
			messageSender.send(player,
					"<red>You don't have an NPC to configure!");
			return;
		}

		if (!allowEdit) {
			messageSender.send(player,
					"<red>You are not allowed to edit NPCs!");
			return;
		}

		String title = npcSettingsConfig.getTitle();
		int rows = npcSettingsConfig.getRows();

		Gui gui = Gui.gui(GuiType.CHEST)
				.title(formatter.format(title))
				.rows(rows)
				.disableAllInteractions()
				.create();

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

		for (Map.Entry<String, GuiItemBean> entry : npcSettingsConfig.getItems()
				.entrySet()) {
			String key = entry.getKey();
			GuiItemBean item = entry.getValue();

			if (item.getSlot() < 0 || item.getSlot() >= rows * 9) {
				continue;
			}

			if (!isOptionAllowed(getOptionKeyFromItemKey(key))) {
				gui.setItem(item.getSlot(), createDisabledItem(item));
			} else {
				gui.setItem(item.getSlot(), createGuiItem(key, item));
			}
		}

		if (configManager.getValue(Inventories.GLOBAL_FILL_EMPTY_SLOTS)) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			gui.getFiller().fill(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		}
		gui.open(player);
	}

	@Override
	protected String processPlaceholders(String text) {
		if (text == null)
			return "";
		if (playerNpc == null)
			return text;

		text = super.processPlaceholders(text);

		text = text.replace("%npc_name%", playerNpc.getData().getDisplayName());
		text = text.replace("%scale%",
				String.valueOf(playerNpc.getData().getScale()));
		text = text.replace("%visibility_distance%",
				String.valueOf(playerNpc.getData().getVisibilityDistance()));
		text = text.replace("%turn_status%",
				playerNpc.getData().isTurnToPlayer() ? "<green>Enabled"
						: "<red>Disabled");
		text = text.replace("%mirror_status%",
				playerNpc.getData().isMirrorSkin() ? "<green>Enabled"
						: "<red>Disabled");
		text = text.replace("%glowing_status%",
				playerNpc.getData().isGlowing() ? "<green>Enabled"
						: "<red>Disabled");
		text = text.replace("%collidable_status%",
				playerNpc.getData().isCollidable() ? "<green>Enabled"
						: "<red>Disabled");

		NamedTextColor glowingColor = playerNpc.getData().getGlowingColor();
		if (glowingColor != null) {
			text = text.replace("%glowing_color%",
					glowingColor.toString().toLowerCase().replace("_", " "));
		}

		return text;
	}

	@Override
	protected Material getMaterialForItem(String key, GuiItemBean item,
			boolean enabled) {
		Material material = super.getMaterialForItem(key, item, enabled);

		if (material == Material.AIR && key != null
				&& key.equals("glowing-color") && playerNpc != null) {
			return getColorMaterial(playerNpc.getData().getGlowingColor());
		}

		return material;
	}

	@Override
	protected void handleCustomAction(String actionValue) {
		System.out.println("Custom action: " + actionValue);

		if (actionValue == null || actionValue.isEmpty()) {
			return;
		}

		String[] parts = actionValue.split(":", 2);
		String actionType = parts[0].toUpperCase();
		String value = parts.length > 1 ? parts[1] : null;

		switch (actionType) {
			case "TOGGLE_TURN_TO_PLAYER":
				toggleBooleanOption("turnToPlayer");
				break;
			case "TOGGLE_MIRROR_SKIN":
				toggleBooleanOption("mirrorSkin");
				break;
			case "TOGGLE_GLOWING":
				toggleBooleanOption("glowing");
				break;
			case "TOGGLE_COLLIDABLE":
				toggleBooleanOption("collidable");
				break;
			case "OPEN_NAME_EDITOR":
				openNameEditor();
				break;
			case "OPEN_SCALE_EDITOR":
				openScaleEditor();
				break;
			case "OPEN_VISIBILITY_EDITOR":
				openVisibilityDistanceEditor();
				break;
			case "OPEN_COLOR_SELECTOR":
				openGlowingColorSelector();
				break;
			case "OPEN_ACTIONS_EDITOR":
				openActionsEditor();
				break;
			case "SET_COLOR":
				if (value != null) {
					try {
						GlowingColor color = GlowingColor.valueOf(value);
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
			case "SAVE_NPC":
				saveNpcChanges();
				messageSender.send(player,
						"<green>NPC settings saved successfully!");
				break;
		}
	}

	private String getOptionKeyFromItemKey(String itemKey) {
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

	private void toggleBooleanOption(String option) {
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

		send(player);
	}

	private Material getColorMaterial(NamedTextColor color) {
		if (color == null)
			return Material.WHITE_WOOL;

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

	private void openNameEditor() {
		if (!isOptionAllowed("display_name")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

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
										+ textComponent.content() + "<green>!");
					} else {
						messageSender.send(player,
								"<red>Name cannot be empty!");
					}
				},
				() -> navigationManager.reopenCurrentGui(player));
	}

	private void openGlowingColorSelector() {
		if (!isOptionAllowed("glowing_color")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		navigationManager.navigateTo(player,
				GuiNavigationManager.GuiType.COLOR_SELECTOR, playerNpc);
	}

	private void openScaleEditor() {
		if (!isOptionAllowed("scale")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		navigationManager.navigateTo(player,
				GuiNavigationManager.GuiType.SCALE_SELECTOR, playerNpc);
	}

	private void openVisibilityDistanceEditor() {
		if (!isOptionAllowed("visibility_distance")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

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
												+ distance + "<green> blocks!");
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

	private void openActionsEditor() {
		if (!isOptionAllowed("actions")) {
			messageSender.send(player,
					"<red>You are not allowed to change this setting!");
			return;
		}

		messageSender.send(player,
				"<red>Actions editor is not yet implemented.",
				"<yellow>This would allow configuring what happens when a player interacts with the NPC.");
	}

	private void saveNpcChanges() {
		FancyNpcsPlugin.get().getNpcManager().saveNpcs(true);
		playerNpc.removeForAll();
		playerNpc.spawnForAll();
	}

	private boolean isOptionAllowed(String option) {
		return allowEdit
				&& (option.isEmpty() || allowedEditOptions.contains(option));
	}
}