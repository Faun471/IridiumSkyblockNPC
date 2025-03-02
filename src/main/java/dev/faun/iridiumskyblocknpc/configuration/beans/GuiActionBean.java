package dev.faun.iridiumskyblocknpc.configuration.beans;

import dev.faun.iridiumskyblocknpc.gui.action.GuiActionType;
import dev.faun.iridiumskyblocknpc.gui.action.GuiClickTrigger;

/**
 * Bean representing a configurable GUI action
 */
public class GuiActionBean {

    /**
     * The click trigger to execute this action
     */
    private GuiClickTrigger clickTrigger;

    /**
     * The order/priority of execution (lower numbers execute first)
     */
    private int order;
    
    /**
     * The type of action to execute
     */
    private GuiActionType actionType;

    /**
     * The value associated with the action (e.g., command to run, GUI to navigate to)
     */
    private String value;

    /**
     * Default constructor for serialization
     */
    public GuiActionBean() {
        this.clickTrigger = GuiClickTrigger.ANY_CLICK;
        this.order = 1;
        this.actionType = GuiActionType.PLAYER_COMMAND;
        this.value = "";
    }

    /**
     * Full constructor
     */
    public GuiActionBean(GuiClickTrigger clickTrigger, int order, GuiActionType actionType, String value) {
        this.clickTrigger = clickTrigger;
        this.order = order;
        this.actionType = actionType;
        this.value = value;
    }

    public GuiClickTrigger getClickTrigger() {
        return clickTrigger;
    }

    public void setClickTrigger(GuiClickTrigger clickTrigger) {
        this.clickTrigger = clickTrigger;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public GuiActionType getActionType() {
        return actionType;
    }

    public void setActionType(GuiActionType actionType) {
        this.actionType = actionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}