package com.camden.skriptutils;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("SkriptUtils loaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkriptUtils unloaded!");
    }
}
