package dev.faun.iridiumskyblocknpc.gui;

import org.bukkit.entity.Player;

/**
 * Interface for GUI providers that can be opened for players
 * 
 */
public interface GuiProvider {
    
    /**
     * Open and display the GUI to a player
     * 
     * @param player The player to show the GUI to
     */
    void send(Player player);
}