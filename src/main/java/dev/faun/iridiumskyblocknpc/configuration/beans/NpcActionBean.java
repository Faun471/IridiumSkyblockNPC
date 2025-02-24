package dev.faun.iridiumskyblocknpc.configuration.beans;


import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.jetbrains.annotations.NotNull;

@NotNull
public class NpcActionBean {
    private final ActionTrigger actionTrigger;
    private final int order;
    private final String action;
    private final String value;

    public NpcActionBean() {
        this.actionTrigger = ActionTrigger.ANY_CLICK;
        this.order = 1;
        this.action = "player_command";
        this.value = "island";
    }

    public NpcActionBean(ActionTrigger actionTrigger, int order, String action, String value) {
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
}
