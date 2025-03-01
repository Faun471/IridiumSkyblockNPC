package dev.faun.iridiumskyblocknpc.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.wesjd.anvilgui.AnvilGUI;

/**
 * Utility class for handling anvil-based editors
 */
public class AnvilEditorUtil {

	/**
	 * Open an anvil editor for the player to input a string value
	 * 
	 * @param player
	 *            The player to open the anvil for
	 * @param npc
	 *            The NPC being edited
	 * @param title
	 *            The title of the anvil GUI
	 * @param defaultText
	 *            The default text to show in the anvil
	 * @param plugin
	 *            The plugin instance
	 * @param messageSender
	 *            The message sender service
	 * @param valueUpdater
	 *            Function to handle the input and update the NPC
	 * @param reopenGui
	 *            Runnable to reopen the GUI after successful input
	 */
	public static void openTextEditor(
			Player player,
			Npc npc,
			String title,
			String defaultText,
			Plugin plugin,
			MessageSender messageSender,
			BiConsumer<Component, Npc> valueUpdater,
			Runnable reopenGui) {

		// Create the item to rename
		ItemStack inputItem = new ItemStack(Material.PAPER);
		ItemMeta meta = inputItem.getItemMeta();
		meta.displayName(sanitizeInput(defaultText));
		inputItem.setItemMeta(meta);

		// Create the output item
		ItemStack outputItem = new ItemStack(Material.PAPER);
		ItemMeta outputMeta = outputItem.getItemMeta();
		outputMeta.displayName(sanitizeInput(defaultText));
		outputItem.setItemMeta(outputMeta);

		valueUpdater.accept(sanitizeInput(defaultText), npc);

		// Build the anvil GUI
		new AnvilGUI.Builder()
				.plugin(plugin)
				.title(title)
				.itemLeft(inputItem)
				.itemOutput(outputItem)
				.onClick((slot, stateSnapshot) -> {
					// Check if the player clicked the output slot
					if (slot != AnvilGUI.Slot.OUTPUT) {
						return Collections.emptyList();
					}

					// Get the input text
					String input = stateSnapshot.getText();

					// Sanitize input to remove unsafe patterns
					Component sanitizedInput = sanitizeInput(input);

					// Update the NPC with the new value
					valueUpdater.accept(sanitizedInput, npc);

					return Arrays.asList(
							AnvilGUI.ResponseAction.close(),
							AnvilGUI.ResponseAction.run(() -> {
								Bukkit.getScheduler().runTaskLater(plugin, () -> {
									reopenGui.run();
								}, 1L);
							}));
				})
				.onClose((e) -> {
					// Reopen the GUI if the player closes it
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						reopenGui.run();
					}, 2L);
				})
				.text(defaultText)
				.open(player);
	}

	/**
	 * Sanitizes input to remove potential exploits but keeps color codes
	 * 
	 * @param input
	 *            The input string to sanitize
	 * @return A sanitized version of the input
	 */
	private static Component sanitizeInput(String input) {
		// Check if input is not null
		if (input == null) {
			return Component.empty();
		}

		MiniMessage miniMessage = MiniMessage.builder()
				.tags(TagResolver.builder()
						.resolver(StandardTags.color())
						.resolver(StandardTags.decorations())
						.resolver(StandardTags.rainbow())
						.resolver(StandardTags.gradient())
						.resolver(StandardTags.shadowColor())
						.build())
				.build();

		return miniMessage.deserialize(input);
	}
}