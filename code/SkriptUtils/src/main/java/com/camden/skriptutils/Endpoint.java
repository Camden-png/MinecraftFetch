package com.camden.skriptutils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import ch.njol.skript.variables.Variables;
import fi.iki.elonen.NanoHTTPD;

import static java.text.MessageFormat.format;

public class Endpoint extends NanoHTTPD {
    public static final String APPLICATION_JSON = "application/json";

    public Endpoint(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);  // 5 seconds...
        LoggerUtil.getLogger().info(
            format("Started endpoint on port: ''{0}''", port)
        );
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if ("/get_players".equalsIgnoreCase(uri)) {
            List<LinkedHashMap<String, Object>> players = Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                    map.put("player_name", player.getName());

                    String uuid = player.getUniqueId().toString();
                    map.put("uuid", uuid);

                    // Get Skript variable...
                    String variableName = format("game::{0}::metadata", uuid);
                    Object _skriptValue = Variables.getVariable(variableName, null, false);
                    String skriptValue = _skriptValue instanceof String ? (String) _skriptValue : null;
                    Map<String, Object> data = Utils.deserialize(skriptValue);
                    map.put("data", data);

                    return map;
                })
                .collect(Collectors.toList());

            return newFixedLengthResponse(
                Response.Status.OK,
                APPLICATION_JSON,
                createPayload(players)
            );
        }

        return newFixedLengthResponse(
            Response.Status.NOT_FOUND,
            APPLICATION_JSON,
            createPayload("Not found!")
        );
    }

    private String createPayload(String key, Object data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, data);
        return Utils.serialize(map);
    }

    private String createPayload(Object data) {
        return createPayload("message", data);
    }
}
