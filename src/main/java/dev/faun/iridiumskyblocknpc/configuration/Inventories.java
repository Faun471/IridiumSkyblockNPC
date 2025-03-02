package dev.faun.iridiumskyblocknpc.configuration;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import ch.jalu.configme.properties.types.BeanPropertyType;
import ch.jalu.configme.properties.MapProperty;
import dev.faun.iridiumskyblocknpc.configuration.beans.GuiItemBean;
import dev.faun.iridiumskyblocknpc.configuration.beans.InventoryBean;
import dev.faun.iridiumskyblocknpc.configuration.beans.SignEditorBean;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Inventories implements SettingsHolder {

	@Comment({ "The material used to fill empty slots",
			"Set to AIR to display nothing",
			"Default value: BLACK_STAINED_GLASS_PANE",
			"Possible values: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html" })
	public static final Property<Material> GLOBAL_FILL_ITEM = PropertyInitializer
			.newBeanProperty(
					Material.class,
					"inventories.global.fill-item",
					Material.BLACK_STAINED_GLASS_PANE);

	@Comment({ "\n", "The name of the fill item" })
	public static final Property<String> GLOBAL_FILL_NAME = PropertyInitializer
			.newProperty(
					"inventories.global.fill_name",
					" ");

	@Comment({ "\n", "Whether to fill all empty slots with the fill item" })
	public static final Property<Boolean> GLOBAL_FILL_EMPTY_SLOTS = PropertyInitializer
			.newProperty(
					"inventories.global.fill-empty-slots",
					false);

	// NPC Settings GUI
	@Comment({ "\n", "Main NPC settings GUI configuration" })
	public static final Property<InventoryBean> NPC_SETTINGS_GUI = PropertyInitializer
			.newBeanProperty(
					InventoryBean.class,
					"inventories.npc-settings",
					new InventoryBean(
							"<green>NPC Settings",
							5,
							true,
							createDefaultNpcSettingsItems()));

	// Color Selector GUI
	@Comment({ "\n", "Color selector GUI configuration" })
	public static final Property<InventoryBean> COLOR_SELECTOR_GUI = PropertyInitializer
			.newBeanProperty(
					InventoryBean.class,
					"inventories.color-selector",
					new InventoryBean(
							"<green>Select Glowing Color",
							4,
							true,
							createDefaultColorSelectorItems()));

	// Sign Editors configuration
	@Comment({ "\n", "Sign editor configurations" })
	public static final MapProperty<SignEditorBean> SIGN_EDITORS = PropertyInitializer
			.mapProperty(
					BeanPropertyType.of(SignEditorBean.class))
			.path("inventories.sign-editors")
			.defaultValue(createDefaultSignEditors())
			.build();

	// Custom GUIs
	@Comment({ "\n", "Custom GUI configurations",
			"You can define your own GUIs here and reference them by their key",
			"Use the 'custom-guis' section to define as many custom GUIs as you want" })
	public static final MapProperty<InventoryBean> CUSTOM_GUIS = PropertyInitializer
			.mapProperty(BeanPropertyType.of(InventoryBean.class))
			.path("inventories.custom-guis")
			.defaultValue(createDefaultCustomGuis())
			.build();

	// Helper methods to create default configurations
	private static Map<String, GuiItemBean> createDefaultNpcSettingsItems() {
		Map<String, GuiItemBean> items = new HashMap<>();

		// Display Name
		items.put("display-name", new GuiItemBean(
				10, Material.PLAYER_HEAD, null, "<grey>Display Name",
				List.of("<grey>Current: <white>%npc_name%", "",
						"<gold>Click to change"),
				"CUSTOM:OPEN_NAME_EDITOR", null, false));

		// Turn to Player
		items.put("turn-to-player", new GuiItemBean(
				11, Material.ENDER_EYE, Material.ENDER_PEARL,
				"<grey>Turn to Player",
				List.of("<grey>Current: %turn_status%", "",
						"<gold>Click to toggle"),
				"CUSTOM:TOGGLE_TURN_TO_PLAYER", null, false));

		// Mirror Skin
		items.put("mirror-skin", new GuiItemBean(
				12, Material.PLAYER_HEAD, Material.SKELETON_SKULL,
				"<grey>Mirror Skin",
				List.of("<grey>Current: %mirror_status%", "",
						"<gold>Click to toggle"),
				"CUSTOM:TOGGLE_MIRROR_SKIN", null, false));

		// Glowing
		items.put("glowing", new GuiItemBean(
				13, Material.GLOWSTONE, Material.COAL_BLOCK, "<grey>Glowing",
				List.of("<grey>Current: %glowing_status%", "",
						"<gold>Click to toggle"),
				"CUSTOM:TOGGLE_GLOWING", null, false));

		// Glowing Color
		items.put("glowing-color", new GuiItemBean(
				14, Material.GLOW_INK_SAC, null, "<grey>Glowing Color",
				List.of("<grey>Current: <white>%glowing_color%", "",
						"<gold>Click to change"),
				"CUSTOM:OPEN_COLOR_SELECTOR", null, false));

		// Collidable
		items.put("collidable", new GuiItemBean(
				15, Material.BARRIER, Material.STRUCTURE_VOID,
				"<grey>Collidable",
				List.of("<grey>Current: %collidable_status%", "",
						"<gold>Click to toggle"),
				"CUSTOM:TOGGLE_COLLIDABLE", null, false));

		// Scale
		items.put("scale", new GuiItemBean(
				16, Material.WHEAT_SEEDS, null, "<grey>Scale",
				List.of("<grey>Current: <white>%scale%", "",
						"<gold>Click to change"),
				"CUSTOM:OPEN_SCALE_EDITOR", null, false));

		// Visibility Distance
		items.put("visibility-distance", new GuiItemBean(
				19, Material.SPYGLASS, null, "<grey>Visibility Distance",
				List.of("<grey>Current: <white>%visibility_distance%", "",
						"<gold>Click to change"),
				"CUSTOM:OPEN_VISIBILITY_EDITOR", null, false));

		// Actions
		items.put("actions", new GuiItemBean(
				20, Material.COMMAND_BLOCK, null, "<grey>Actions",
				List.of("<grey>Configure NPC actions", "",
						"<gold>Click to edit"),
				"CUSTOM:OPEN_ACTIONS_EDITOR", null, false));

		// Save
		items.put("save", new GuiItemBean(
				44, Material.EMERALD, null, "<green><bold>Save Changes",
				null, "CUSTOM:SAVE_NPC", null, false));

		// Close
		items.put("close", new GuiItemBean(
				40, Material.NETHER_STAR, null, "<red><bold>Close",
				null, "BACK", null, true));

		return items;
	}

	private static Map<String, GuiItemBean> createDefaultColorSelectorItems() {
		Map<String, GuiItemBean> items = new HashMap<>();

		// Color items
		items.put("white", new GuiItemBean(
				10, Material.WHITE_WOOL, null, "<white><bold>White",
				null, "SET_COLOR", "WHITE", false));

		items.put("light_gray", new GuiItemBean(
				11, Material.LIGHT_GRAY_WOOL, null, "<grey>Light Gray",
				null, "SET_COLOR", "GRAY", false));

		items.put("gray", new GuiItemBean(
				12, Material.GRAY_WOOL, null, "<dark_grey>Gray",
				null, "SET_COLOR", "DARK_GRAY", false));

		items.put("black", new GuiItemBean(
				13, Material.BLACK_WOOL, null, "<black>Black",
				null, "SET_COLOR", "BLACK", false));

		items.put("red", new GuiItemBean(
				14, Material.RED_WOOL, null, "<red>Red",
				null, "SET_COLOR", "RED", false));

		items.put("orange", new GuiItemBean(
				15, Material.ORANGE_WOOL, null, "<grey>Orange",
				null, "SET_COLOR", "GOLD", false));

		items.put("yellow", new GuiItemBean(
				16, Material.YELLOW_WOOL, null, "<yellow>Yellow",
				null, "SET_COLOR", "YELLOW", false));

		items.put("lime", new GuiItemBean(
				19, Material.LIME_WOOL, null, "<green>Lime",
				null, "SET_COLOR", "GREEN", false));

		items.put("green", new GuiItemBean(
				20, Material.GREEN_WOOL, null, "<dark_green>Green",
				null, "SET_COLOR", "DARK_GREEN", false));

		items.put("cyan", new GuiItemBean(
				21, Material.CYAN_WOOL, null, "<dark_aqua>Cyan",
				null, "SET_COLOR", "DARK_AQUA", false));

		items.put("light_blue", new GuiItemBean(
				22, Material.LIGHT_BLUE_WOOL, null, "<aqua>Light Blue",
				null, "SET_COLOR", "AQUA", false));

		items.put("blue", new GuiItemBean(
				23, Material.BLUE_WOOL, null, "<dark_blue>Blue",
				null, "SET_COLOR", "DARK_BLUE", false));

		items.put("purple", new GuiItemBean(
				24, Material.PURPLE_WOOL, null, "<dark_purple>Purple",
				null, "SET_COLOR", "DARK_PURPLE", false));

		items.put("magenta", new GuiItemBean(
				25, Material.MAGENTA_WOOL, null, "<light_purple>Magenta",
				null, "SET_COLOR", "LIGHT_PURPLE", false));

		// Back button
		items.put("back", new GuiItemBean(
				31, Material.NETHER_STAR, null, "<red><bold>Back",
				null, "BACK", null, true));

		return items;
	}

	/**
	 * Create default custom GUIs as examples
	 * 
	 * @return Map of example custom GUIs
	 */
	private static Map<String, InventoryBean> createDefaultCustomGuis() {
		Map<String, InventoryBean> customGuis = new HashMap<>();

		// Example custom GUI
		Map<String, GuiItemBean> exampleItems = new HashMap<>();

		// Example item with multiple actions
		GuiItemBean multiActionItem = new GuiItemBean(
				13, Material.DIAMOND, null, "<aqua>Multi-Action Item",
				List.of("<gray>This item demonstrates multiple actions",
						"<gray>Left-click to run a player command",
						"<gray>Right-click to run a console command",
						"<gray>Shift-click to navigate back"),
				null, null, true);

		// Add actions to the item
		multiActionItem.addAction(
				new dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean(
						dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger.LEFT_CLICK,
						1, 
						dev.faun.iridiumskyblocknpc.gui.action.GuiActionType.PLAYER_COMMAND,
						"me clicked with left click"));

		multiActionItem.addAction(
				new dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean(
						dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger.RIGHT_CLICK,
						1,
						dev.faun.iridiumskyblocknpc.gui.action.GuiActionType.CONSOLE_COMMAND,
						"say %player_name% right-clicked a button!"));

		multiActionItem.addAction(
				new dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean(
						dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger.SHIFT_LEFT_CLICK,
						1,
						dev.faun.iridiumskyblocknpc.gui.action.GuiActionType.BACK,
						""));

		exampleItems.put("multi_action", multiActionItem);

		// Another example showing navigation between custom GUIs
		GuiItemBean navigationItem = new GuiItemBean(
				15, Material.COMPASS, null, "<yellow>Navigate",
				List.of("<gray>Click to navigate to another GUI"),
				null, null, false);

		navigationItem.addAction(
				new dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean(
						dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger.ANY_CLICK,
						1,
						dev.faun.iridiumskyblocknpc.gui.action.GuiActionType.NAVIGATE_GUI,
						"second_example"));

		exampleItems.put("navigate", navigationItem);

		// Add close button
		GuiItemBean closeItem = new GuiItemBean(
				22, Material.BARRIER, null, "<red>Close",
				List.of("<gray>Click to close this GUI"),
				null, null, false);

		closeItem.addAction(
				new dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean(
						dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger.ANY_CLICK,
						1,
						dev.faun.iridiumskyblocknpc.gui.action.GuiActionType.CLOSE,
						""));

		exampleItems.put("close", closeItem);

		// Create and add the first example GUI
		customGuis.put("example_gui", new InventoryBean(
				"<gold>Example Custom GUI",
				3,
				true,
				exampleItems));

		// Create a second example GUI for navigation demonstration
		Map<String, GuiItemBean> secondExampleItems = new HashMap<>();

		// Go back button
		GuiItemBean backItem = new GuiItemBean(
				13, Material.ARROW, null, "<yellow>Back to First GUI",
				List.of("<gray>Click to go back to the first example GUI"),
				null, null, false);

		backItem.addAction(
				new dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean(
						dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger.ANY_CLICK,
						1,
						dev.faun.iridiumskyblocknpc.gui.action.GuiActionType.BACK,
						""));

		secondExampleItems.put("back", backItem);

		// Create and add the second example GUI
		customGuis.put("second_example", new InventoryBean(
				"<gold>Second Example GUI",
				3,
				true,
				secondExampleItems));

		return customGuis;
	}

	private static Map<String, SignEditorBean> createDefaultSignEditors() {
		Map<String, SignEditorBean> signEditors = new HashMap<>();

		signEditors.put("name-editor", new SignEditorBean(
				"",
				"Current name:",
				"%npc_name%",
				"Enter new name"));

		signEditors.put("scale-editor", new SignEditorBean(
				"",
				"Current: %scale%",
				"Enter new scale",
				"(e.g. 1.5)"));

		signEditors.put("visibility-editor", new SignEditorBean(
				"",
				"Current: %visibility_distance%",
				"Enter new distance in",
				"(blocks)"));

		return signEditors;
	}
}