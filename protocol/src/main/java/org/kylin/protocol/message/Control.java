package org.kylin.protocol.message;

/**
 * Created by jimmey on 15-7-28.
 */
public class Control extends Message {

    /**
     * server to client
     */
    private boolean heartbeat;

    /**
     * server to client
     */
    private boolean offline;

    public Control() {
        super(MessageType.CONTROL, 0);
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }
}
