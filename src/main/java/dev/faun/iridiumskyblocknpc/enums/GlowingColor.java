package dev.faun.iridiumskyblocknpc.enums;

import net.kyori.adventure.text.format.NamedTextColor;

public enum GlowingColor {
    BLACK("black", 0),
    DARK_BLUE("dark_blue", 170),
    DARK_GREEN("dark_green", 43520),
    DARK_AQUA("dark_aqua", 43690),
    DARK_RED("dark_red", 11141120),
    DARK_PURPLE("dark_purple", 11141290),
    GOLD("gold", 16755200),
    GRAY("gray", 11184810),
    DARK_GRAY("dark_gray", 5592405),
    BLUE("blue", 5592575),
    GREEN("green", 5635925),
    AQUA("aqua", 5636095),
    RED("red", 16733525),
    LIGHT_PURPLE("light_purple", 16733695),
    YELLOW("yellow", 16777045),
    WHITE("white", 16777215);

    private final String name;
    private final int color;

    GlowingColor(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public static GlowingColor fromNamedTextColor(NamedTextColor color) {
        return GlowingColor.valueOf(color.examinableName());
    }

    public NamedTextColor toNamedTextColor() {
        return NamedTextColor.namedColor(color);
    }

    public static GlowingColor fromString(String color) {
        return GlowingColor.valueOf(color.toUpperCase());
    }

    public String toString() {
        return this.name().toLowerCase();
    }

    public String getName() {
        return name;
    }
}
