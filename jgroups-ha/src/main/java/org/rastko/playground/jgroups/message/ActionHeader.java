package org.rastko.playground.jgroups.message;

import java.io.Serializable;

/**
 * Simple header which determines action
 * which should be taken when message arrives.
 */
public class ActionHeader implements Serializable {

    private static final long serialVersionUID = 8802951134579208329L;
    private Action action;

    public ActionHeader() {
    }

    public ActionHeader(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "ActionHeader{" +
                "action=" + action +
                '}';
    }

    public enum Action {
        ADD,
        REMOVE;
    }
}
