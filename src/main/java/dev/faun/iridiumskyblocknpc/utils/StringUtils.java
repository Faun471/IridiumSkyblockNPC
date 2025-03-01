package dev.faun.iridiumskyblocknpc.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringUtils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Parse a string with MiniMessage
     *
     * @param text The text to parse
     * @return The parsed Component
     */
    public static Component parse(String text) {
        if (text == null) return Component.empty();
        return MINI_MESSAGE.deserialize(text);
    }

    /**
     * Parse a string with MiniMessage and replace placeholders
     *
     * @param text The text to parse
     * @param placeholders A map of placeholders to replace
     * @return The parsed Component with placeholders replaced
     */
    public static Component parse(String text, Map<String, String> placeholders) {
        if (text == null) return Component.empty();
        
        List<TagResolver> resolvers = new ArrayList<>();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            resolvers.add(Placeholder.parsed(entry.getKey(), entry.getValue()));
        }
        
        return MINI_MESSAGE.deserialize(text, TagResolver.resolver(resolvers));
    }

    /**
     * Parse a string with MiniMessage and replace placeholders
     *
     * @param text The text to parse
     * @param placeholders Array of key-value pairs for placeholders
     * @return The parsed Component with placeholders replaced
     */
    public static Component parse(String text, String... placeholders) {
        if (text == null) return Component.empty();
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in key-value pairs");
        }
        
        List<TagResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < placeholders.length; i += 2) {
            resolvers.add(Placeholder.parsed(placeholders[i], placeholders[i + 1]));
        }
        
        return MINI_MESSAGE.deserialize(text, TagResolver.resolver(resolvers));
    }
    
    /**
     * Format a Location into a Component with hover details
     *
     * @param location The location to format
     * @param includeWorld Whether to include the world name
     * @return A formatted Component representing the location
     */
    public static Component formatLocation(Location location, boolean includeWorld) {
        if (location == null) return Component.empty();
        
        // Build the main component for display
        String displayText = "<grey>"
                + location.getBlockX() + ", "
                + location.getBlockY() + ", "
                + location.getBlockZ();
                
        // Build the hover component with more detailed info
        TextComponent.Builder hoverBuilder = Component.text();
        hoverBuilder.append(Component.text("Location Details")
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.UNDERLINED, true));
        
        hoverBuilder.append(Component.newline());
        hoverBuilder.append(parse("<red>X:</red> " + location.getX()));
        hoverBuilder.append(Component.newline());
        hoverBuilder.append(parse("<green>Y:</green> " + location.getY()));
        hoverBuilder.append(Component.newline());
        hoverBuilder.append(parse("<blue>Z:</blue> " + location.getZ()));
        
        if (includeWorld && location.getWorld() != null) {
            displayText = "<grey>World: " + location.getWorld().getName() + " | " + displayText;
            
            hoverBuilder.append(Component.newline());
            hoverBuilder.append(parse("<yellow>World:</yellow> " + location.getWorld().getName()));
        }
        
        // Add yaw and pitch to hover
        hoverBuilder.append(Component.newline());
        hoverBuilder.append(parse("<gray>Yaw:</gray> " + String.format("%.2f", location.getYaw())));
        hoverBuilder.append(Component.newline());
        hoverBuilder.append(parse("<gray>Pitch:</gray> " + String.format("%.2f", location.getPitch())));
        
        // Combine display and hover components
        return parse(displayText).hoverEvent(HoverEvent.showText(hoverBuilder.build()));
    }
    
    /**
     * Format a Location into a Component with hover details
     *
     * @param location The location to format
     * @return A formatted Component representing the location
     */
    public static Component formatLocation(Location location) {
        return formatLocation(location, true);
    }
    
    /**
     * Format an ItemStack into a Component with hover details
     *
     * @param item The ItemStack to format
     * @return A formatted Component representing the item
     */
    public static Component formatItem(ItemStack item) {
        if (item == null) return Component.text("None");
        
        // Get basic item info
        Material material = item.getType();
        String itemName = material.name();
        
        // Get display name from item meta if available
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                itemName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
            }
        }
        
        // Create hover component with detailed info
        TextComponent.Builder hoverBuilder = Component.text();
        
        // Item name in the hover tooltip
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            hoverBuilder.append(item.getItemMeta().displayName().decoration(TextDecoration.ITALIC, false));
        } else {
            // Format material name nicely - DIAMOND_SWORD -> Diamond Sword
            String prettyName = formatMaterialName(material.name());
            hoverBuilder.append(Component.text(prettyName)
                .decoration(TextDecoration.ITALIC, false));
        }
        
        // Add amount if more than 1
        if (item.getAmount() > 1) {
            hoverBuilder.append(Component.newline());
            hoverBuilder.append(Component.text("Amount: " + item.getAmount()));
        }
        
        // Add lore if present
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<Component> lore = item.getItemMeta().lore();
            if (lore != null && !lore.isEmpty()) {
                hoverBuilder.append(Component.newline());
                for (Component line : lore) {
                    hoverBuilder.append(Component.newline());
                    hoverBuilder.append(line);
                }
            }
        }
        
        // Create the main displayed component with hover
        String displayText = "<aqua>" + itemName + (item.getAmount() > 1 ? " x" + item.getAmount() : "");
        return parse(displayText).hoverEvent(HoverEvent.showText(hoverBuilder.build()));
    }
    
    /**
     * Format a Material into a Component with hover details
     *
     * @param material The Material to format
     * @return A formatted Component representing the material
     */
    public static Component formatMaterial(Material material) {
        if (material == null) return Component.text("None");
        
        // Format material name nicely
        String prettyName = formatMaterialName(material.name());
        
        // Create hover details
        TextComponent hoverComponent = Component.text(prettyName)
            .decoration(TextDecoration.ITALIC, false);
        
        // Create the display component with hover
        return Component.text(prettyName)
            .color(net.kyori.adventure.text.format.NamedTextColor.AQUA)
            .hoverEvent(HoverEvent.showText(hoverComponent));
    }
    
    /**
     * Format a material name from ENUM_NAME to Pretty Name
     *
     * @param materialName The material name to format
     * @return Formatted material name
     */
    public static String formatMaterialName(String materialName) {
        StringBuilder result = new StringBuilder();
        String[] words = materialName.split("_");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.charAt(0))
                     .append(word.substring(1).toLowerCase())
                     .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * Convert a Component to a plain string
     *
     * @param component The Component to convert
     * @return Plain text representation
     */
    public static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
