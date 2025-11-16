package com.camden.skriptutils;

import org.bukkit.entity.Player;

import static java.text.MessageFormat.format;

public class Slot {
    public static void setSlot(Player player, Object index) {
        try {
            // Indirect convert, using Java code as Skript handles Longs / ints strangely...
            int slotIndex = Integer.parseInt(String.valueOf(index));

            if (slotIndex < 0) slotIndex = 0;
            if (slotIndex > 8) slotIndex = 8;

            player.getInventory().setHeldItemSlot(slotIndex);
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Error: '{0}'", ex.getMessage())
            );
        }
    }
}
