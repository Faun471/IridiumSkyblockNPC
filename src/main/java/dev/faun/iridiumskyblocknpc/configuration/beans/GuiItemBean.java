package dev.faun.iridiumskyblocknpc.configuration.beans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class GuiItemBean {
	private int slot;
	private Material material;
	private Material materialDisabled;
	private String name;
	private List<String> lore;
	private boolean glow;
	private String action;
	private String color;
	private List<GuiActionBean> actions;

	/**
	 * Constructor with all fields
	 */
	public GuiItemBean(int slot, Material material, Material materialDisabled, String name, List<String> lore, String action,
			String color, boolean glow) {
		this.slot = slot;
		this.material = material;
		this.materialDisabled = materialDisabled;
		this.name = name;
		this.lore = lore;
		this.action = action;
		this.color = color;
		this.glow = glow;
		this.actions = new ArrayList<>();
	}

	/**
	 * Default constructor for serialization
	 */
	public GuiItemBean() {
		this.slot = 0;
		this.material = Material.AIR;
		this.materialDisabled = Material.BARRIER;
		this.name = "";
		this.lore = new ArrayList<>();
		this.action = "";
		this.color = "";
		this.glow = false;
		this.actions = new ArrayList<>();
	}

	public int getSlot() {
		return slot;
	}

	public Material getMaterial() {
		return material;
	}

	public Material getMaterialDisabled() {
		return materialDisabled;
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public String getLore(int index) {
		return lore.get(index);
	}

	/**
	 * @return The legacy action string
	 * @deprecated Use getActions() instead
	 */
	@Deprecated
	public String getAction() {
		return action;
	}

	public String getColor() {
		return color;
	}

	public boolean getGlow() {
		return glow;
	}

	/**
	 * @return List of actions for this item, sorted by order
	 */
	public List<GuiActionBean> getActions() {
		return actions;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setMaterialDisabled(Material materialDisabled) {
		this.materialDisabled = materialDisabled;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	/**
	 * Sets the legacy action
	 * @param action The action string
	 * @deprecated Use addAction or setActions instead
	 */
	@Deprecated
	public void setAction(String action) {
		this.action = action;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setGlow(boolean glow) {
		this.glow = glow;
	}
	
	/**
	 * Set the actions list for this item
	 * @param actions The actions to set
	 */
	public void setActions(List<GuiActionBean> actions) {
		this.actions = actions;
	}
	
	/**
	 * Add an action to this item
	 * @param action The action to add
	 */
	public void addAction(GuiActionBean action) {
		if (this.actions == null) {
			this.actions = new ArrayList<>();
		}
		this.actions.add(action);
	}
}