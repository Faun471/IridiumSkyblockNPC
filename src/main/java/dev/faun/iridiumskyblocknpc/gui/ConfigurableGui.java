package dev.faun.iridiumskyblocknpc.gui;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import dev.faun.iridiumskyblocknpc.configuration.ConfigManager;
import dev.faun.iridiumskyblocknpc.configuration.Inventories;
import dev.faun.iridiumskyblocknpc.configuration.beans.GuiItemBean;
import dev.faun.iridiumskyblocknpc.configuration.beans.InventoryBean;
import dev.faun.iridiumskyblocknpc.services.MessageSender;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;

/**
 * A configurable GUI that can be defined in configuration
 */
public class ConfigurableGui extends AbstractGui {

	private final InventoryBean config;
	private final String guiId;
	private final Object customData;

	public ConfigurableGui(InventoryBean config, ConfigManager configManager,
			MessageSender messageSender, GuiNavigationManager navigationManager,
			CustomGuiRegistry customGuiRegistry, Player player,
			String guiId, Object customData) {
		super(configManager, messageSender, navigationManager, player);
		this.config = config;
		this.guiId = guiId;
		this.customData = customData;
	}

	@Override
	public void send(Player player) {
		Gui gui = Gui.gui(GuiType.CHEST)
				.title(formatter.format(config.getTitle()))
				.rows(config.getRows())
				.disableAllInteractions()
				.create();

		for (Map.Entry<String, GuiItemBean> entry : config.getItems()
				.entrySet()) {
			String key = entry.getKey();
			GuiItemBean item = entry.getValue();

			if (item.getSlot() < 0 || item.getSlot() >= config.getRows() * 9) {
				continue;
			}

			gui.setItem(item.getSlot(), createGuiItem(key, item));
		}

		if (config.getBorder()) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			gui.getFiller().fillBorder(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		} else if (configManager
				.getValue(Inventories.GLOBAL_FILL_EMPTY_SLOTS)) {
			Material fillMaterial = configManager
					.getValue(Inventories.GLOBAL_FILL_ITEM);
			String fillName = configManager
					.getValue(Inventories.GLOBAL_FILL_NAME);

			gui.getFiller().fill(ItemBuilder.from(fillMaterial)
					.name(formatter.format(fillName))
					.asGuiItem());
		}

		navigationManager.registerGui(player,
				GuiNavigationManager.GuiType.CUSTOM, guiId);

		gui.open(player);
	}

	@Override
	protected void handleCustomAction(String actionValue) {
		super.handleCustomAction(actionValue);
	}

	/**
	 * Get the custom data for this GUI
	 * 
	 * @return The custom data
	 */
	protected Object getCustomData() {
		return customData;
	}
}