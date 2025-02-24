package dev.faun.iridiumskyblocknpc.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NpcGui {
    public void send(Player player) {
        Gui gui = Gui.gui(GuiType.CHEST)
                .title(Component.text("IridiumSkyblock"))
                .rows(3)
                .create();

        GuiItem npcSettings = ItemBuilder.skull()
                .owner(player)
                .name(Component.text("NPC Settings"))
                .asGuiItem();

        GuiItem islandSettings = ItemBuilder.from(Material.GRASS_BLOCK)
                .name(Component.text("Island Settings"))
                .asGuiItem();

        GuiItem stats = ItemBuilder.from(Material.BOOK)
                .name(Component.text("Stats"))
                .asGuiItem();

        GuiItem close = ItemBuilder.from(Material.BARRIER)
                .name(Component.text("Close"))
                .asGuiItem(e -> gui.close(player));

        gui.setItem(11, npcSettings);
        gui.setItem(13, islandSettings);
        gui.setItem(15, stats);
        gui.setItem(21, close);
    }
}
