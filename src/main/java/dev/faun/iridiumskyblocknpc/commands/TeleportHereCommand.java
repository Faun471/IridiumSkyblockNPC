package dev.faun.iridiumskyblocknpc.commands;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.configuration.Messages;
import dev.faun.iridiumskyblocknpc.services.MessageFormatter;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.faun.iridiumskyblocknpc.utils.StringUtils;
import dev.triumphteam.cmd.core.annotations.Command;
import net.kyori.adventure.text.Component;

import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command(value = "iridiumskyblocknpc", alias = { "isnpc" })
public class TeleportHereCommand {
	private final MessageSender messageSender;
	private final MessageFormatter messageFormatter;

	public TeleportHereCommand(MessageSender messageSender, MessageFormatter messageFormatter) {
		this.messageSender = messageSender;
		this.messageFormatter = messageFormatter;
	}

	@Command(value = "teleporthere", alias = { "tphere" })
	public void teleportHereCommand(Player player) {
		Location location = player.getLocation();
		Npc selectedNpc = FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream().filter(
				npc -> npc.getData().getCreator() != null && npc.getData().getCreator().equals(player.getUniqueId()))
				.findFirst().orElse(null);

		if (selectedNpc == null) {
			messageSender.send(player, Messages.NO_NPC_FOUND);
			return;
		}

		Component message = messageFormatter.text(Messages.TELEPORT_HERE_SUCCESS)
				.placeholder("full_location", StringUtils.formatLocation(location))
				.placeholder("player", player.getName())
				.placeholder("x", location.getBlockX())
				.placeholder("y", location.getBlockY())
				.placeholder("z", location.getBlockZ())
				.placeholder("world", location.getWorld().getName())
				.placeholder("npx_name", selectedNpc.getData().getName())
				.buildComponent();

		messageSender.send(player, message);
	}
}
