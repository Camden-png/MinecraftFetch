package com.camden.skriptutils;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        Glow.init(instance);
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
