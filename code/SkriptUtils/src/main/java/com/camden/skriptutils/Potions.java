package com.camden.skriptutils;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import static java.text.MessageFormat.format;

public class Potions {
    public static List<String> getEffects(Player player) {
        try {
            List<String> idList = new ArrayList<>();
            player.getActivePotionEffects().forEach(effect -> {
                String id = effect.getType().getKey().getKey();
                idList.add(id);
            });

            return idList;
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Error: ''{0}''", ex.getMessage())
            );
        }
        return new ArrayList<>();
    }
}
