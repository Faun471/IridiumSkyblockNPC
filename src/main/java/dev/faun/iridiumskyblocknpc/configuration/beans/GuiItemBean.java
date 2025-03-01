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
	}

	public GuiItemBean() {
		this.slot = 0;
		this.material = Material.AIR;
		this.materialDisabled = Material.BARRIER;
		this.name = "";
		this.lore = new ArrayList<>();
		this.action = "";
		this.color = "";
		this.glow = false;
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

	public String getAction() {
		return action;
	}

	public String getColor() {
		return color;
	}

	public boolean getGlow() {
		return glow;
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

	public void setAction(String action) {
		this.action = action;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setGlow(boolean glow) {
		this.glow = glow;
	}
}