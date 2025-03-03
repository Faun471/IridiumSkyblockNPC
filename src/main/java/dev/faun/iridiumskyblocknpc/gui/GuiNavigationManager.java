package dev.faun.iridiumskyblocknpc.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;

/**
 * Manages GUI navigation history for players, allowing for back navigation
 * between GUIs
 */
public class GuiNavigationManager {

	private final Map<UUID, Stack<GuiNavigationEntry>> playerNavigationHistory;
	private final Map<GuiType, BiFunction<Player, Object, GuiProvider>> guiFactories;

	public GuiNavigationManager() {
		this.playerNavigationHistory = new HashMap<>();
		this.guiFactories = new HashMap<>();
	}

	public Map<UUID, Stack<GuiNavigationEntry>> getPlayerNavigationHistory() {
		return playerNavigationHistory;
	}

	public Map<GuiType, BiFunction<Player, Object, GuiProvider>> getGuiFactories() {
		return guiFactories;
	}

	public String getGuiTypes() {
		return guiFactories.keySet().toString();
	}

	/**
	 * Register a GUI factory function for a specific GUI type
	 * 
	 * @param guiType
	 *            The type of GUI
	 * @param factory
	 *            The function that creates the GUI provider with player and
	 *            data
	 */
	public void registerGuiFactory(GuiType guiType,
			BiFunction<Player, Object, GuiProvider> factory) {
		guiFactories.put(guiType, factory);
	}

	/**
	 * Register that a player has opened a specific GUI
	 * 
	 * @param player
	 *            The player who opened the GUI
	 * @param guiType
	 *            The type of GUI that was opened
	 * @param data
	 *            Additional data for recreating the GUI if needed
	 */
	public void registerGui(Player player, GuiType guiType, Object data) {
		UUID playerId = player.getUniqueId();

		playerNavigationHistory.putIfAbsent(playerId, new Stack<>());

		Stack<GuiNavigationEntry> history = playerNavigationHistory
				.get(playerId);

		if (history.isEmpty()) {
			history.push(new GuiNavigationEntry(guiType, data));
			return;
		}

		if (guiType.equals(history.peek().getType())
				&& data.equals(history.peek().getData())) {
			return;
		}

		history.push(new GuiNavigationEntry(guiType, data));
	}

	/**
	 * Navigate to a specific GUI
	 * 
	 * @param player
	 *            The player to navigate
	 * @param guiType
	 *            The type of GUI to navigate to
	 * @param data
	 *            Additional data for the GUI
	 * @param customProvider
	 *            Optional custom GUI provider that overrides the registered one
	 *            for
	 *            this call
	 * @return true if navigation was successful
	 */
	public boolean navigateTo(Player player, GuiType guiType, Object data,
			GuiProvider customProvider) {
		registerGui(player, guiType, data);

		if (customProvider != null) {
			customProvider.send(player);
			return true;
		}

		BiFunction<Player, Object, GuiProvider> factory = guiFactories
				.get(guiType);
		if (factory != null) {
			GuiProvider guiProvider = factory.apply(player, data);
			if (guiProvider != null) {
				guiProvider.send(player);
				System.out.println(
						"[GuiNavigationManager] Navigated to " + guiType);
				System.out.println("[GuiNavigationManager] Current history: "
						+ playerNavigationHistory
								.get(player.getUniqueId()).stream().map(
										entry -> entry.getType().toString())
								.reduce((a, b) -> a + ", " + b)
								.orElse("empty"));
				return true;
			}
		}

		return false;
	}

	/**
	 * Navigate to a specific GUI using a registered factory
	 * 
	 * @param player
	 *            The player to navigate
	 * @param guiType
	 *            The type of GUI to navigate to
	 * @param data
	 *            Additional data for the GUI
	 * @return true if navigation was successful
	 */
	public boolean navigateTo(Player player, GuiType guiType, Object data) {
		return navigateTo(player, guiType, data, null);
	}

	/**
	 * Reopen the current GUI for a player. This is helpful for refreshing the
	 * GUI
	 * or if a gui from another plugin was opened and we need to reopen the
	 * previous
	 * gui upon closing it.
	 * 
	 * @param player
	 *            The player to reopen the GUI for
	 * @return true if navigation was successful, false if there's no history
	 */
	public boolean reopenCurrentGui(Player player) {
		GuiNavigationEntry currentGui = getCurrentGui(player);
		if (currentGui == null) {
			return false;
		}

		return navigateTo(player, currentGui.getType(), currentGui.getData());
	}

	/**
	 * Go back to the previous GUI in the player's history
	 * 
	 * @param player
	 *            The player to navigate back
	 * @return true if navigation was successful, false if there's no history
	 */
	public boolean navigateBack(Player player) {
		UUID playerId = player.getUniqueId();

		if (!playerNavigationHistory.containsKey(playerId)) {
			System.out.println(
					"[GuiNavigationManager] Warning: No navigation history found for player "
							+ playerId);
			return false;
		}

		Stack<GuiNavigationEntry> history = playerNavigationHistory
				.get(playerId);
		if (history.size() <= 1) {
			System.out.println(
					"[GuiNavigationManager] Warning: No previous GUI found for player "
							+ playerId);
			return false;
		}

		GuiNavigationEntry previousGui = history.peek();

		BiFunction<Player, Object, GuiProvider> factory = guiFactories
				.get(previousGui.getType());
		if (factory != null) {
			GuiProvider guiProvider = factory.apply(player,
					previousGui.getData());
			if (guiProvider != null) {
				guiProvider.send(player);
				return true;
			}
		}

		return true;
	}

	/**
	 * Get the previous GUI entry without navigating back
	 * 
	 * @param player
	 *            The player to check history for
	 * @return The previous GUI entry or null if none exists
	 */
	public GuiNavigationEntry getPreviousGui(Player player) {
		UUID playerId = player.getUniqueId();

		if (!playerNavigationHistory.containsKey(playerId)) {
			return null;
		}

		Stack<GuiNavigationEntry> history = playerNavigationHistory
				.get(playerId);
		if (history.size() <= 1) {
			return null;
		}

		GuiNavigationEntry previousGui = history.get(history.size() - 1);

		return previousGui;
	}

	/**
	 * Clear navigation history for a player
	 * 
	 * @param player
	 *            The player whose history should be cleared
	 */
	public void clearHistory(Player player) {
		playerNavigationHistory.remove(player.getUniqueId());
	}

	/**
	 * Get the current GUI for a player
	 * 
	 * @param player
	 *            The player to check
	 * @return The current GUI entry or null if none exists
	 */
	public GuiNavigationEntry getCurrentGui(Player player) {
		UUID playerId = player.getUniqueId();

		if (!playerNavigationHistory.containsKey(playerId)
				|| playerNavigationHistory.get(playerId).isEmpty()) {
			return null;
		}

		return playerNavigationHistory.get(playerId).peek();
	}

	/**
	 * Enum representing different types of GUIs in the plugin
	 */
	public enum GuiType {
		MAIN, NPC_SETTINGS, COLOR_SELECTOR, SCALE_SELECTOR, STATS, CUSTOM;
	}

	/**
	 * Class to store GUI navigation entry information
	 */
	public static class GuiNavigationEntry {
		private final GuiType type;
		private final Object data;

		public GuiNavigationEntry(GuiType type, Object data) {
			this.type = type;
			this.data = data;
		}

		public GuiType getType() {
			return type;
		}

		public Object getData() {
			return data;
		}
	}
}