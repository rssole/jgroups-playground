package org.rastko.playground.jgroups.message;


import java.io.Serializable;

public class JGroupsMessagePayload<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 6095036870067087695L;

    private ActionHeader header;
    private T body;

    public JGroupsMessagePayload() {
    }

    public JGroupsMessagePayload(ActionHeader header, T body) {
        this.header = header;
        this.body = body;
    }

    public ActionHeader getHeader() {
        return header;
    }

    public void setHeader(ActionHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "JGroupsMessagePayload{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JGroupsMessagePayload that = (JGroupsMessagePayload) o;

        if (!body.equals(that.body)) return false;
        if (!header.equals(that.header)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = header.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
