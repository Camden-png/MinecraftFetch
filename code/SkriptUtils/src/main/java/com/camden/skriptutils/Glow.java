package com.camden.skriptutils;

import fr.skytasul.glowingentities.GlowingBlocks;
import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;  // Deprecated...
import org.bukkit.plugin.java.JavaPlugin;

import static java.text.MessageFormat.format;

@SuppressWarnings("deprecation")
public class Glow {
    private static GlowingEntities entitiesAPI;
    private static GlowingBlocks blocksAPI;  // TODO: use later...

    public static void init(JavaPlugin plugin) {
        if (isReady()) return;
        entitiesAPI = new GlowingEntities(plugin);
        blocksAPI = new GlowingBlocks(plugin);
        LoggerUtil.getLogger().info("Glowing entities & blocks initialized!");
    }

    public static void disable() {
        if (!isReady()) return;
        entitiesAPI.disable();
        blocksAPI.disable();
    }

    public static void setGlow(Entity entity, Player viewer, String color) {
        if (!isReady()) return;

        ChatColor chatColor = convertColor(color);

        try {
            entitiesAPI.setGlowing(entity, viewer, chatColor);
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Error: '{0}'", ex.getMessage())
            );
        }
    }

    public static void setGlow(Entity entity, Player viewer) {
        setGlow(entity, viewer, "white");
    }

    public static void unsetGlow(Entity entity, Player viewer) {
        if (!isReady()) return;
        try {
            entitiesAPI.unsetGlowing(entity, viewer);
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Error: '{0}'", ex.getMessage())
            );
        }
    }

    private static ChatColor convertColor(String color) {
        try {
            return ChatColor.valueOf(color.toUpperCase());
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Could not convert '{0}' to enum ChatColor", color)
            );
        }
        return ChatColor.WHITE;
    }

    private static boolean isReady() {
        return entitiesAPI != null && blocksAPI != null;
    }
}
