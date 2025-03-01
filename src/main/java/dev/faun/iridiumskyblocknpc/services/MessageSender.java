package dev.faun.iridiumskyblocknpc.services;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

import ch.jalu.configme.properties.Property;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import net.kyori.adventure.text.Component;

public class MessageSender {
	private final ConfigManager configManager;
	private final MessageFormatter formatter;

	private MessageSender(Builder builder) {
		this.configManager = builder.configManager;
		this.formatter = builder.formatter != null
				? builder.formatter
				: MessageFormatter.builder()
						.configManager(configManager)
						.build();
	}

	// Static builder method
	public static Builder builder() {
		return new Builder();
	}

	// Builder class
	public static class Builder {
		private ConfigManager configManager;
		private MessageFormatter formatter;

		private Builder() {
		}

		public Builder configManager(ConfigManager configManager) {
			this.configManager = configManager;
			return this;
		}

		public Builder formatter(MessageFormatter formatter) {
			this.formatter = formatter;
			return this;
		}

		public MessageSender build() {
			if (configManager == null) {
				throw new IllegalStateException(
						"ConfigManager must be provided");
			}
			return new MessageSender(this);
		}
	}

	/**
	 * Refreshes the prefix in the formatter
	 * Call this method after config reload to ensure the prefix is updated
	 */
	public void refreshPrefix() {
		formatter.refreshPrefix();
	}

	// Get a new formatter instance
	public MessageFormatter formatter() {
		return MessageFormatter.builder()
				.configManager(configManager)
				.build();
	}

	/**
	 * Send a config message to a player
	 */
	public void send(CommandSender sender, Property<String> message) {
		send(sender, configManager.getValue(message), new HashMap<>());
	}

	/**
	 * Send a string message with placeholders
	 */
	public void send(CommandSender sender, String message,
			Map<String, String> placeholders) {
		Component component;
		synchronized (formatter) {
			formatter.text(message);
			for (Map.Entry<String, String> placeholder : placeholders
					.entrySet()) {
				formatter.placeholder(placeholder.getKey(),
						placeholder.getValue());
			}
			component = formatter.buildComponent();
		}
		sender.sendMessage(component);
	}

	/**
	 * Send a pre-built component
	 */
	public void send(CommandSender sender, Component... component) {
		for (Component comp : component) {
			sender.sendMessage(comp);
		}
	}

	/**
	 * Send a string message with placeholders
	 */
	public void send(CommandSender sender, String... message) {
		for (String msg : message) {
			send(sender, msg, new HashMap<>());
		}
	}
}