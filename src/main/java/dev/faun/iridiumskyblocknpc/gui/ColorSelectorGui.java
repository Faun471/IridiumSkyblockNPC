package dev.faun.iridiumskyblocknpc.gui;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.oliver.fancynpcs.api.Npc;
import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Inventories;
import dev.faun.iridiumskyblocknpc.configuration.beans.GuiItemBean;
import dev.faun.iridiumskyblocknpc.configuration.beans.InventoryBean;
import dev.faun.iridiumskyblocknpc.enums.GlowingColor;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * GUI for selecting NPC glowing colors
 */
public class ColorSelectorGui extends AbstractGui {
	private final Npc playerNpc;
	private final InventoryBean colorSelectorConfig;

	public ColorSelectorGui(ConfigManager configManager,
			MessageSender messageSender,
			GuiNavigationManager navigationManager, Npc playerNpc,
			Player player) {
		super(configManager, messageSender, navigationManager, player);
		this.playerNpc = playerNpc;
		this.colorSelectorConfig = configManager
				.getValue(Inventories.COLOR_SELECTOR_GUI);
	}

	@Override
	public void send(Player player) {
		String title = colorSelectorConfig.getTitle();
		int rows = colorSelectorConfig.getRows();

		Gui colorGui = Gui.gui(GuiType.CHEST)
				.title(formatter.format(title))
				.rows(rows)
				.disableAllInteractions()
				.create();

		if (colorSelectorConfig.getBorder()) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			colorGui.getFiller().fillBorder(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		}

		for (Map.Entry<String, GuiItemBean> entry : colorSelectorConfig
				.getItems().entrySet()) {
			GuiItemBean item = entry.getValue();

			if (item.getSlot() < 0 || item.getSlot() >= rows * 9) {
				continue;
			}

			try {
				if (item.getColor() != null && !item.getColor().isEmpty()) {
					GlowingColor color = GlowingColor.valueOf(item.getColor());
					boolean isSelected = playerNpc.getData()
							.getGlowingColor() == color.toNamedTextColor();

					colorGui.setItem(item.getSlot(),
							createColorItem(item, color, isSelected));
				} else {
					colorGui.setItem(item.getSlot(),
							createGuiItem(entry.getKey(), item));
				}
			} catch (IllegalArgumentException e) {
				messageSender.send(player,
						"<red>Invalid glowing color in config: "
								+ item.getColor());
			}
		}

		if (!colorSelectorConfig.getItems().containsKey("back")) {
			colorGui.setItem(colorSelectorConfig.getRows() * 9 - 1,
					ItemBuilder.from(Material.ARROW)
							.name(formatter.format("<red>Back")
									.decoration(TextDecoration.BOLD, true))
							.asGuiItem(event -> navigationManager
									.navigateBack(player)));
		}

		if (configManager.getValue(Inventories.GLOBAL_FILL_EMPTY_SLOTS)) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			colorGui.getFiller().fill(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		}

		navigationManager.registerGui(player,
				GuiNavigationManager.GuiType.COLOR_SELECTOR, playerNpc);
		colorGui.open(player);
	}

	private GuiItem createColorItem(GuiItemBean item, GlowingColor color,
			boolean isSelected) {
		return ItemBuilder.from(item.getMaterial())
				.name(formatter.format(item.getName())
						.decoration(TextDecoration.BOLD, true))
				.glow(isSelected)
				.asGuiItem(event -> {
					if (item.getAction().equals("SET_COLOR")) {
						playerNpc.getData()
								.setGlowingColor(color.toNamedTextColor());
						messageSender.send(player,
								"<green>Updated glowing color!");
						navigationManager.navigateBack(player);
					}
				});
	}
}