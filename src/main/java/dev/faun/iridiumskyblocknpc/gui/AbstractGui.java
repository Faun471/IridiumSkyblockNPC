package dev.faun.iridiumskyblocknpc.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.beans.GuiActionBean;
import dev.faun.iridiumskyblocknpc.configuration.beans.GuiItemBean;
import dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger;
import dev.faun.iridiumskyblocknpc.services.MessageFormatter;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Abstract base class for GUIs providing common functionality
 */
public abstract class AbstractGui implements GuiProvider {

	protected final ConfigManager configManager;
	protected final MessageSender messageSender;
	protected final GuiNavigationManager navigationManager;
	protected final Player player;
	protected final MessageFormatter formatter;
	private final Map<String, Function<String, String>> placeholderHandlers;

	public AbstractGui(ConfigManager configManager, MessageSender messageSender,
			GuiNavigationManager navigationManager, Player player) {
		this.configManager = configManager;
		this.messageSender = messageSender;
		this.navigationManager = navigationManager;
		this.player = player;
		this.formatter = MessageFormatter.builder().configManager(configManager)
				.build();
		this.placeholderHandlers = new HashMap<>();
	}

	/**
	 * Add a custom placeholder handler to process placeholders in item text
	 * 
	 * @param placeholderPrefix
	 *            The prefix that identifies this placeholder (without %)
	 * @param handler
	 *            Function to process the placeholder content
	 */
	public void registerPlaceholderHandler(String placeholderPrefix,
			Function<String, String> handler) {
		placeholderHandlers.put(placeholderPrefix, handler);
	}

	/**
	 * Process placeholders in text using registered handlers
	 * 
	 * @param text
	 *            The text to process
	 * @return The processed text
	 */
	protected String processPlaceholders(String text) {
		if (text == null) {
			return "";
		}

		for (Map.Entry<String, Function<String, String>> handler : placeholderHandlers
				.entrySet()) {
			String prefix = handler.getKey();
			Function<String, String> processor = handler.getValue();

			String pattern = "%" + prefix + "_";
			if (text.contains(pattern)) {
				int startIdx = text.indexOf(pattern);
				while (startIdx >= 0) {
					int endIdx = text.indexOf('%', startIdx + 1);
					if (endIdx > startIdx) {
						String placeholder = text.substring(startIdx,
								endIdx + 1);
						String content = text
								.substring(startIdx + pattern.length(), endIdx);

						String replacement = processor.apply(content);
						text = text.replace(placeholder, replacement);

						startIdx = text.indexOf(pattern,
								startIdx + replacement.length());
					} else {
						break;
					}
				}
			}
		}

		return text;
	}

	/**
	 * Process placeholders in an array of strings
	 * 
	 * @param text
	 *            Array of strings to process
	 * @return Processed strings
	 */
	protected String[] processPlaceholders(String[] text) {
		if (text == null) {
			return new String[0];
		}

		String[] result = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			result[i] = processPlaceholders(text[i]);
		}
		return result;
	}

	/**
	 * Create a GUI item from item configuration
	 * 
	 * @param key
	 *            The item key
	 * @param item
	 *            The item config
	 * @return The GUI item
	 */
	protected GuiItem createGuiItem(String key, GuiItemBean item) {
		Material material = getMaterialForItem(key, item, true);
		String displayName = processPlaceholders(item.getName());
		String[] lore = item.getLore() != null
				? processPlaceholders(item.getLore().toArray(String[]::new))
				: new String[0];

		ItemBuilder builder = ItemBuilder.from(material)
				.name(formatter.format(displayName)
						.decoration(TextDecoration.ITALIC, false))
				.glow(item.getGlow());

		if (lore.length > 0) {
			builder.lore(Arrays.stream(lore)
					.filter(line -> line != null && !line.isEmpty())
					.map(line -> formatter.format(line)
							.decoration(TextDecoration.ITALIC, false))
					.collect(Collectors.toList()));
		}

		return builder.asGuiItem(event -> {
			handleActions(item, event.getClick());
		});
	}

	/**
	 * Handle actions for a GUI item click
	 * 
	 * @param item
	 *            The item that was clicked
	 * @param clickType
	 *            The type of click
	 */
	protected void handleActions(GuiItemBean item, ClickType clickType) {
		if (item.getAction() != null && !item.getAction().isEmpty()) {
			handleLegacyAction(item.getAction(), item.getColor());
		}

		List<GuiActionBean> applicableActions = getActionsForClick(item,
				clickType);

		for (GuiActionBean action : applicableActions) {
			System.out.println("Executing action: " + action.getActionType()
					+ " - " + action.getValue());
			executeAction(action);
		}
	}

	/**
	 * Get actions that apply to a specific click type
	 * 
	 * @param item
	 *            The item with actions
	 * @param clickType
	 *            The type of click
	 * @return List of applicable actions, sorted by order
	 */
	protected List<GuiActionBean> getActionsForClick(GuiItemBean item,
			ClickType clickType) {
		List<GuiActionBean> result = new ArrayList<>();

		if (item.getActions() == null || item.getActions().isEmpty()) {
			return result;
		}

		for (GuiActionBean action : item.getActions()) {
			GuiClickTrigger trigger = action.getClickTrigger();

			boolean applies = switch (trigger) {
				case LEFT_CLICK -> clickType == ClickType.LEFT;
				case RIGHT_CLICK -> clickType == ClickType.RIGHT;
				case SHIFT_LEFT_CLICK -> clickType == ClickType.SHIFT_LEFT;
				case SHIFT_RIGHT_CLICK -> clickType == ClickType.SHIFT_RIGHT;
				case ANY_LEFT_CLICK -> clickType == ClickType.LEFT
						|| clickType == ClickType.SHIFT_LEFT;
				case ANY_RIGHT_CLICK -> clickType == ClickType.RIGHT
						|| clickType == ClickType.SHIFT_RIGHT;
				case ANY_CLICK -> true;
				default -> false;
			};

			if (applies) {
				result.add(action);
			}
		}

		result.sort(Comparator.comparingInt(GuiActionBean::getOrder));

		return result;
	}

	/**
	 * Execute a GUI action
	 * 
	 * @param action
	 *            The action to execute
	 */
	protected void executeAction(GuiActionBean action) {
		String value = processPlaceholders(action.getValue());

		switch (action.getActionType()) {
			case PLAYER_COMMAND:
				player.performCommand(value);
				break;

			case CONSOLE_COMMAND:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
				break;

			case OP_COMMAND:
				boolean wasOp = player.isOp();
				try {
					player.setOp(true);
					player.performCommand(value);
				} finally {
					if (!wasOp) {
						player.setOp(false);
					}
				}
				break;

			case NAVIGATE_GUI:
				try {
					GuiNavigationManager.GuiType guiType = GuiNavigationManager.GuiType
							.valueOf(value.toUpperCase());
					navigationManager.navigateTo(player, guiType, null);
				} catch (IllegalArgumentException e) {
					if (!navigationManager.navigateTo(player,
							GuiNavigationManager.GuiType.CUSTOM, value)) {
						messageSender.send(player,
								"<red>Unknown GUI: " + value
										+ " - check config.");
					}

				}
				break;

			case BACK:
				navigationManager.navigateBack(player);
				break;

			case CLOSE:
				player.closeInventory();
				break;

			case CUSTOM:
				handleCustomAction(value);
				break;

			default:
				break;
		}
	}

	/**
	 * Handle legacy actions (for backward compatibility)
	 * 
	 * @param action
	 *            The action string
	 * @param actionValue
	 *            The optional action value
	 */
	protected void handleLegacyAction(String action, String actionValue) {
		if (action == null || action.isEmpty()) {
			return;
		}

		switch (action.toUpperCase()) {
			case "BACK":
				navigationManager.navigateBack(player);
				break;

			case "CLOSE":
				player.closeInventory();
				break;

			default:
				if (action.startsWith("PLAYER_COMMAND:")) {
					String cmd = action.substring("PLAYER_COMMAND:".length());
					player.performCommand(cmd);
				}

				if (action.startsWith("CONSOLE_COMMAND:")) {
					String cmd = action.substring("CONSOLE_COMMAND:".length());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
				}

				if (action.startsWith("OP_COMMAND:")) {
					String cmd = action.substring("OP_COMMAND:".length());
					boolean wasOp = player.isOp();
					try {
						player.setOp(true);
						player.performCommand(cmd);
					} finally {
						if (!wasOp) {
							player.setOp(false);
						}
					}
				}

				if (action.startsWith("CUSTOM:")) {
					String customAction = action.substring("CUSTOM:".length());
					handleCustomAction(customAction);
				}
				break;
		}
	}

	/**
	 * Get the material to use for an item, handling disabled states and special
	 * cases
	 * 
	 * @param key
	 *            The item key
	 * @param item
	 *            The item config
	 * @param enabled
	 *            Whether the item is enabled
	 * @return The material to use
	 */
	protected Material getMaterialForItem(String key, GuiItemBean item,
			boolean enabled) {
		if (!enabled && item.getMaterialDisabled() != null) {
			return item.getMaterialDisabled();
		}
		return item.getMaterial() != null ? item.getMaterial()
				: Material.BARRIER;
	}

	/**
	 * Handle a custom action - to be overridden by subclasses for specific
	 * behaviors
	 * 
	 * @param actionValue
	 *            The action value
	 */
	protected void handleCustomAction(String actionValue) {
		// Default implementation does nothing
	}

	/**
	 * Create a disabled item with red lore text indicating it cannot be used
	 * 
	 * @param item
	 *            The item config
	 * @return The disabled GUI item
	 */
	protected GuiItem createDisabledItem(GuiItemBean item) {
		Material material = getMaterialForItem(null, item, false);
		String displayName = processPlaceholders(item.getName());
		List<Component> lore = new ArrayList<>();

		// Add existing lore
		if (item.getLore() != null) {
			lore.addAll(Arrays
					.stream(processPlaceholders(
							item.getLore().toArray(String[]::new)))
					.filter(line -> !line.isEmpty())
					.map(formatter::format)
					.collect(Collectors.toList()));
		}

		lore.add(Component.empty());
		lore.add(formatter.format("<red>You cannot use this item"));

		return ItemBuilder.from(material)
				.name(formatter.format(displayName))
				.lore(lore)
				.asGuiItem();
	}
}