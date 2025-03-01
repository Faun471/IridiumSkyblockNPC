package dev.faun.iridiumskyblocknpc.configuration.beans;

import de.oliver.fancynpcs.api.actions.ActionTrigger;

public class NpcActionBean {

	private ActionTrigger actionTrigger;

	private int order;

	private String action;

	private String value;

	public NpcActionBean() {
		this.actionTrigger = ActionTrigger.ANY_CLICK;
		this.order = 1;
		this.action = "player_command";
		this.value = "island";
	}

	public NpcActionBean(ActionTrigger actionTrigger, int order,
			String action,
			String value) {
		this.actionTrigger = actionTrigger;
		this.order = order;
		this.action = action;
		this.value = value;
	}

	public int getOrder() {
		return order;
	}

	public String getAction() {
		return action;
	}

	public String getValue() {
		return value;
	}

	public ActionTrigger getActionTrigger() {
		return actionTrigger;
	}

	public void setActionTrigger(ActionTrigger actionTrigger) {
		this.actionTrigger = actionTrigger;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
