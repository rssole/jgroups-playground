package org.rastko.playground.jgroups.dto;


import java.io.Serializable;

public class JGroupsMessagePayload implements Serializable {
    private static final long serialVersionUID = 6095036870067087695L;

    private String payload;

    public JGroupsMessagePayload() {
    }

    public JGroupsMessagePayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JGroupsMessagePayload that = (JGroupsMessagePayload) o;

        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return payload != null ? payload.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "JGroupsMessagePayload{" +
                "payload='" + payload + '\'' +
                '}';
    }
}
