package org.rastko.playground.jgroups.message;

import org.jgroups.Global;
import org.jgroups.Header;
import org.jgroups.util.Streamable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

/**
 * Simple header which determines action
 * which should be taken when message arrives.
 */
public class ActionHeader extends Header implements Streamable, Serializable {

    public static final short ActionHeaderMagicNumber = 1888;
    private static final long serialVersionUID = 2758706403793636277L;

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
    public int size() {
        return Global.SHORT_SIZE;
    }

    @Override
    public void writeTo(DataOutput dataOutput) throws Exception {
        dataOutput.writeInt(action.getId());
    }

    @Override
    public void readFrom(DataInput dataInput) throws Exception {
        action = Action.fromInt(dataInput.readInt());
    }

    public enum Action {
        ADD(0),
        REMOVE(1);

        private int id;

        Action(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Action fromInt(int id) {
            for (Action a : Action.values()) {
                if (a.getId() == id) {
                    return a;
                }
            }

            final String message = String.format("Illegal action id: %d for org.rastko.playground.jgroups.message.ActionHeader", id);
            throw new IllegalArgumentException(message);
        }
    }
}
