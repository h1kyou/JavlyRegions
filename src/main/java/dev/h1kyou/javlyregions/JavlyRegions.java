package dev.h1kyou.javlyregions;

import com.sk89q.worldguard.WorldGuard;
import dev.h1kyou.javlyregions.commands.RegionsCommand;
import dev.h1kyou.javlyregions.interfaces.IRegionManager;
import dev.h1kyou.javlyregions.managers.LegacyRegionManager;
import dev.h1kyou.javlyregions.managers.ModernRegionManager;
import dev.h1kyou.javlyregions.metrics.Metrics;
import dev.h1kyou.javlyregions.services.RegionChecker;
import dev.h1kyou.javlyregions.utils.ConfigManager;
import dev.h1kyou.javlyregions.utils.RegionsExpansion;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public final class JavlyRegions extends JavaPlugin {

    private static JavlyRegions instance;
    private static ConfigManager configManager;
    private static IRegionManager regionManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager(getConfig());

        setupRegionManager();
        setupCommands();

        new RegionsExpansion(configManager, regionManager).register();
        new Metrics(this, 22260);

        RegionChecker regionChecker = new RegionChecker(configManager);
        regionChecker.start();
    }

    private void setupRegionManager() {
        regionManager = isVersionNewest() ? new ModernRegionManager(WorldGuard.getInstance().getPlatform()) : new LegacyRegionManager();
    }

    private boolean isVersionNewest() {
        Plugin wePlugin = getPluginManager().getPlugin("WorldEdit");
        if (wePlugin == null) {
            return false;
        }
        String version = wePlugin.getDescription().getVersion();
        return version.startsWith("7") || version.startsWith("8") || version.compareTo("7") >= 0;
    }

    private void setupCommands() {
        CommandExecutor commandExecutor = new RegionsCommand();
        getCommand("regions").setExecutor(commandExecutor);
        getCommand("regions").setTabCompleter((TabCompleter) commandExecutor);
    }

    public static JavlyRegions getInstance() {
        return instance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static IRegionManager getRegionManager() {
        return regionManager;
    }
}