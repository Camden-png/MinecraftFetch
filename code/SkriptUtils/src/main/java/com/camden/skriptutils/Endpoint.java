// TODO: expand to levels, pull dynamically

package com.camden.skriptutils;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
                .sorted(Comparator.comparing(Player::getName))
                .map(player -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                    map.put("player_name", player.getName());

                    // Get Skript variable...
                    String uuid = player.getUniqueId().toString();

                    String variableName = format("game::{0}::metadata", uuid);
                    Object _skriptValue = Variables.getVariable(variableName, null, false);
                    Map<String, Object> data = new LinkedHashMap<>();
                    if (_skriptValue instanceof String) {
                        String skriptValue = (String) _skriptValue;
                        data = Utils.deserialize(skriptValue);
                        data.remove("mode");
                    }
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
