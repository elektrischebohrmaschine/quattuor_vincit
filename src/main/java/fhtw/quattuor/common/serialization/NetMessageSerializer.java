package fhtw.quattuor.common.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fhtw.quattuor.common.net.NetMessage;

public class NetMessageSerializer {
    private final ObjectMapper om = new ObjectMapper();

    public String toJson(NetMessage msg) {
        try { return om.writeValueAsString(msg); }
        catch (JsonProcessingException e) { return "{\"type\":\"ERROR\",\"error\":\"serialize failed\"}"; }
    }

    public NetMessage fromJson(String json) {
        try { return om.readValue(json, NetMessage.class); }
        catch (Exception e) { return null; }
    }
}
