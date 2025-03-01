package dev.faun.iridiumskyblocknpc.commands;

import dev.faun.iridiumskyblocknpc.IridiumSkyblockNPC;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Messages;
import dev.faun.iridiumskyblocknpc.services.MessageFormatter;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.cmd.core.annotations.Command;
import net.kyori.adventure.text.Component;

import org.bukkit.command.CommandSender;

@Command(value = "iridiumskyblocknpc", alias = { "isnpc" })
public class ReloadCommand {
	private final ConfigManager configManager;
	private final MessageFormatter messageFormatter;
	private final MessageSender messageSender;

	public ReloadCommand(ConfigManager configManager,
			MessageSender messageSender, MessageFormatter formatter,
			IridiumSkyblockNPC plugin) {
		this.configManager = configManager;
		this.messageSender = messageSender;
		this.messageFormatter = formatter;
	}

	@Command(value = "reload")
	public void reloadCommand(CommandSender sender) {
		long start = System.currentTimeMillis();
		configManager.reloadConfigs();
		long end = System.currentTimeMillis();

		// Refresh the prefix in both the message formatter and message sender
		messageFormatter.refreshPrefix();
		messageSender.refreshPrefix();
		
		// Build the success message with the updated formatter (which now has
		// the refreshed prefix)
		Component message = messageFormatter.text(Messages.RELOAD_SUCCESS)
				.placeholder("time", String.valueOf(end - start))
				.buildComponent();
		messageSender.send(sender, message);
	}
}
