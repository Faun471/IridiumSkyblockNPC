package dev.faun.iridiumskyblocknpc.services;

import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Messages;
import dev.faun.iridiumskyblocknpc.utils.StringUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import ch.jalu.configme.properties.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFormatter {
	private static final MiniMessage DEFAULT_MINI_MESSAGE = MiniMessage
			.miniMessage();

	private final MiniMessage miniMessage;
	private final ConfigManager configManager;
	private String text;
	private final Map<String, Object> placeholders = new HashMap<>();
	private final List<TagResolver> customResolvers = new ArrayList<>();

	private MessageFormatter(Builder builder) {
		this.miniMessage = builder.miniMessage;
		this.configManager = builder.configManager;

		// Add prefix placeholder if config manager is available
		if (this.configManager != null) {
			refreshPrefix();
		}
	}

	/**
	 * Refreshes the prefix placeholder with the latest value from the config
	 * Call this method after reloading configs to update the prefix
	 */
	public void refreshPrefix() {
		if (this.configManager != null) {
			placeholder("prefix", configManager.getValue(Messages.PREFIX));
		}
	}

	// Static builder creation method
	public static Builder builder() {
		return new Builder();
	}

	// Builder class
	public static class Builder {
		private MiniMessage miniMessage = DEFAULT_MINI_MESSAGE;
		private ConfigManager configManager;
		private List<TagResolver> baseResolvers = new ArrayList<>();

		private Builder() {
		}

		public Builder miniMessage(MiniMessage miniMessage) {
			this.miniMessage = miniMessage;
			return this;
		}

		public Builder configManager(ConfigManager configManager) {
			this.configManager = configManager;
			return this;
		}

		public Builder resolver(TagResolver resolver) {
			this.baseResolvers.add(resolver);
			return this;
		}

		public Builder resolvers(List<TagResolver> resolvers) {
			this.baseResolvers.addAll(resolvers);
			return this;
		}

		public MessageFormatter build() {
			if (baseResolvers.size() > 0) {
				this.miniMessage = MiniMessage.builder()
						.tags(TagResolver.resolver(baseResolvers))
						.build();
			}
			return new MessageFormatter(this);
		}
	}

	/**
	 * Set the text to format
	 * 
	 * @param text
	 *            The text to format
	 */
	public MessageFormatter text(@NotNull String text) {
		this.text = text;
		return this;
	}

	/**
	 * Set the text to format
	 * 
	 * @param text
	 *            The text property to format
	 */
	public MessageFormatter text(@NotNull Property<String> text) {
		this.text = configManager.getValue(text);
		return this;
	}

	/**
	 * Add a placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param value
	 *            The value to replace it with
	 */
	public MessageFormatter placeholder(@NotNull String key,
			@NotNull String value) {
		placeholders.put(key, value);
		return this;
	}

	/**
	 * Add a component placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param value
	 *            The component value to replace it with
	 */
	public MessageFormatter placeholder(@NotNull String key,
			@NotNull Component value) {
		placeholders.put(key, value);
		return this;
	}

	/**
	 * Add a placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param value
	 *            The value to replace it with (converted to string)
	 */
	public MessageFormatter placeholder(String key, Object value) {
		placeholders.put(key, String.valueOf(value));
		return this;
	}

	/**
	 * Add a location placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param location
	 *            The location value to replace it with
	 */
	public MessageFormatter location(@NotNull String key,
			@NotNull Location location) {
		customResolvers.add(Placeholder.component(key,
				StringUtils.formatLocation(location)));
		return this;
	}

	/**
	 * Add an item placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param item
	 *            The item value to replace it with
	 */
	public MessageFormatter item(@NotNull String key, @NotNull ItemStack item) {
		customResolvers
				.add(Placeholder.component(key, StringUtils.formatItem(item)));
		return this;
	}

	/**
	 * Add a material placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param material
	 *            The material value to replace it with
	 */
	public MessageFormatter material(@NotNull String key,
			@NotNull Material material) {
		customResolvers.add(Placeholder.component(key,
				StringUtils.formatMaterial(material)));
		return this;
	}

	/**
	 * Add a player placeholder to replace in the text
	 * 
	 * @param key
	 *            The placeholder key (without brackets)
	 * @param player
	 *            The player to use
	 */
	public MessageFormatter player(@NotNull String key,
			@NotNull Player player) {
		placeholder(key, player.getName());
		return this;
	}

	/**
	 * Add a custom tag resolver
	 * 
	 * @param resolver
	 *            The tag resolver to add
	 */
	public MessageFormatter resolver(@NotNull TagResolver resolver) {
		customResolvers.add(resolver);
		return this;
	}

	/**
	 * Build all placeholder tag resolvers
	 */
	private @NotNull List<TagResolver> buildResolvers() {
		List<TagResolver> resolvers = new ArrayList<>(customResolvers);

		// Add all string and component placeholders
		for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof Component) {
				resolvers.add(Placeholder.component(key, (Component) value));
			} else {
				resolvers.add(Placeholder.parsed(key, String.valueOf(value)));
			}
		}

		return resolvers;
	}

	/**
	 * Build the formatted message as a Component
	 */
	public Component buildComponent() {
		if (text == null) {
			return Component.empty();
		}

		return miniMessage.deserialize(text,
				TagResolver.resolver(buildResolvers()));
	}

	/**
	 * Build the formatted message as a plain String
	 */
	public String build() {
		return PlainTextComponentSerializer.plainText()
				.serialize(buildComponent());
	}

	/**
	 * Format a string with MiniMessage
	 */
	public Component format(String text) {
		if (text == null) {
			return Component.empty();
		}
		return miniMessage.deserialize(text);
	}

}
