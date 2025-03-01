package dev.faun.iridiumskyblocknpc.configuration.beans;

import java.util.HashMap;
import java.util.Map;

public class InventoryBean {
	/**
	 * The title of the inventory.
	 */
	private String title;

	/**
	 * The number of rows in the inventory.
	 */
	private int rows;

	/**
	 * Whether the inventory has a border.
	 */
	private boolean border;

	/**
	 * The items in the inventory.
	 */
	private Map<String, GuiItemBean> items;

	/**
	 * Create a new InventoryBean.
	 *
	 * @param title
	 *            The title of the inventory
	 * @param rows
	 *            The number of rows in the inventory
	 * @param border
	 *            Whether the inventory has a border
	 * @param items
	 *            The items in the inventory
	 */
	public InventoryBean(String title,
			int rows,
			boolean border,
			Map<String, GuiItemBean> items) {
		this.title = title;
		this.rows = rows;
		this.border = border;
		this.items = items;
	}

	/**
	 * Create a new InventoryBean.
	 */
	public InventoryBean() {
		this.title = "";
		this.rows = 0;
		this.border = false;
		this.items = new HashMap<>();
	}

	/**
	 * Get the title of the inventory.
	 *
	 * @return The title of the inventory
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * Get the number of rows in the inventory.
	 *
	 * @return The number of rows in the inventory
	 */
	public final int getRows() {
		return rows;
	}

	/**
	 * Check if the inventory has a border.
	 *
	 * @return Whether the inventory has a border
	 */
	public final boolean getBorder() {
		return border;
	}

	/**
	 * Get the items in the inventory.
	 *
	 * @return The items in the inventory
	 */
	public final Map<String, GuiItemBean> getItems() {
		return new HashMap<>(items);
	}

	/**
	 * Get an item from the inventory.
	 *
	 * @param key
	 *            The key of the item
	 * @return The item
	 */
	public final GuiItemBean getItem(final String key) {
		return items.get(key);
	}

	/**
	 * Set the title of the inventory.
	 *
	 * @param newTitle
	 *            The title of the inventory
	 */
	public final void setTitle(final String newTitle) {
		this.title = newTitle;
	}

	/**
	 * Set the number of rows in the inventory.
	 *
	 * @param newRows
	 *            The number of rows in the inventory
	 */
	public final void setRows(final int newRows) {
		this.rows = newRows;
	}

	/**
	 * Set whether the inventory has a border.
	 *
	 * @param newBorder
	 *            Whether the inventory has a border
	 */
	public final void setBorder(final boolean newBorder) {
		this.border = newBorder;
	}

	/**
	 * Set the items in the inventory.
	 *
	 * @param items
	 *            The items in the inventory
	 */
	public final void setItems(final Map<String, GuiItemBean> items) {
		this.items = items;
	}

	/**
	 * Add an item to the inventory.
	 *
	 * @param key
	 *            The key of the item
	 * @param item
	 *            The item
	 */
	public final void addItem(final String key, final GuiItemBean item) {
		items.put(key, item);
	}

	/**
	 * Remove an item from the inventory.
	 *
	 * @param key
	 *            The key of the item
	 */
	public final void removeItem(final String key) {
		items.remove(key);
	}

	/**
	 * Check if the inventory contains an item.
	 *
	 * @param key
	 *            The key of the item
	 * @return Whether the inventory contains the item
	 */
	public final boolean containsItem(final String key) {
		return items.containsKey(key);
	}

	/**
	 * Check if the inventory contains an item.
	 *
	 * @param item
	 *            The item
	 * @return Whether the inventory contains the item
	 */
	public final boolean containsItem(final GuiItemBean item) {
		return items.containsValue(item);
	}

	/**
	 * Check if the inventory is empty.
	 *
	 * @return Whether the inventory is empty
	 */
	public final boolean isEmpty() {
		return items.isEmpty();
	}
}
