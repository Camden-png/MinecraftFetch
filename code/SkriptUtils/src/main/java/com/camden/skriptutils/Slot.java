package com.camden.skriptutils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Slot {
    public static void setSlot(String playerName, Object index) {
        try {
            Player player = Bukkit.getPlayerExact(playerName);

            // Indirect convert, using Java code as Skript handles Longs / ints strangely...
            int slotIndex = Integer.parseInt(String.valueOf(index));

            if (slotIndex < 0) slotIndex = 0;
            if (slotIndex > 8) slotIndex = 8;
            
            player.getInventory().setHeldItemSlot(slotIndex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
