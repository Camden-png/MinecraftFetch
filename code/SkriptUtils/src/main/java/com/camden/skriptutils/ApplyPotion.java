package com.camden.skriptutils;

import java.util.List;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ApplyPotion {
    public static List<String> getEffects(String playerName) {
        try {
            Player player = Bukkit.getPlayerExact(playerName);

            List<String> idList = new ArrayList<>();
            player.getActivePotionEffects().forEach(effect -> {
                String id = effect.getType().getKey().getKey();
                idList.add(id);
            });

            return idList;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}
