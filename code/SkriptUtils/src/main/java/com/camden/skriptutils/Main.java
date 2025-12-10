package com.camden.skriptutils;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import static java.text.MessageFormat.format;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        Glow.init(instance);

        try {
            new Endpoint(8081);
        } catch (IOException ex) {
            getLogger().warning(format("Error: ''{0}''", ex.getMessage()));
        }

        getLogger().info("SkriptUtils loaded!");
    }

    @Override
    public void onDisable() {
        Glow.disable();
        getLogger().info("SkriptUtils unloaded!");
    }

    public static Main getInstance() {
        return instance;
    }
}
