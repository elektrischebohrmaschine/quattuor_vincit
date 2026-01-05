package fhtw.quattuor.common.net;

public class NetMessage {
    private NetType type;
    private String username;
    private String password;
    private String payload;
    private String error;

    public NetMessage() {
    }

    public NetMessage(NetType type) {
        this.type = type;
    }

    public NetType getType() {
        return type;
    }
    public void setType(NetType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
