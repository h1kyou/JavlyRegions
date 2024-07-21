package dev.h1kyou.javlyregions.utils;

import dev.h1kyou.javlyregions.JavlyRegions;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Map<String, String> messagesCache = new HashMap<>();
    private final Map<String, String> customRegions = new HashMap<>();
    private final List<String> disabledWorlds = new ArrayList<>();
    private String global;
    private String region;
    private String owner;
    private String member;
    private BarColor bossBarColor;
    private BarStyle bossBarStyle;
    private double updateTime;
    private double bossBarDuration;
    private boolean persistentDisplay;
    private boolean actionBar;
    private boolean bossBar;

    public ConfigManager(FileConfiguration configuration) {
        loadConfiguration(configuration);
    }

    private void loadConfiguration(FileConfiguration configuration) {
        loadMessages(configuration);
        loadSettings(configuration);
        loadFormats(configuration);
        loadRegions(configuration.getConfigurationSection("custom-regions"));
    }

    public void reloadConfiguration() {
        JavlyRegions.getInstance().reloadConfig();
        FileConfiguration configuration = JavlyRegions.getInstance().getConfig();
        customRegions.clear();
        messagesCache.clear();
        disabledWorlds.clear();
        loadConfiguration(configuration);
    }

    private void loadRegions(ConfigurationSection section) {
        if (section != null) {
            for (String key : section.getKeys(false)) {
                customRegions.put(key, StringUtils.color(section.getString(key)));
            }
        }
    }

    private void loadMessages(FileConfiguration configuration) {
        loadSection(configuration.getConfigurationSection("messages"), messagesCache);
    }

    private void loadSettings(FileConfiguration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection("settings");
        if (section != null) {
            this.persistentDisplay = section.getBoolean("persistent-display");
            this.actionBar = section.getBoolean("actionbar-enabled");
            this.bossBar = section.getBoolean("bossbar.enabled");
            this.updateTime = section.getDouble("update_time");
            this.disabledWorlds.addAll(section.getStringList("disabled-worlds"));
        }

        if (!actionBar && !bossBar) {
            JavlyRegions javlyRegions = JavlyRegions.getInstance();
            Bukkit.getLogger().warning("Enable display ActionBar or BossBar in the configuration!");
            javlyRegions.getPluginLoader().disablePlugin(javlyRegions);
        }
    }

    private void loadFormats(FileConfiguration configuration) {
        ConfigurationSection bossbarSection = configuration.getConfigurationSection("settings.bossbar");
        if (bossbarSection != null && bossBar) {
            this.bossBarDuration = bossbarSection.getDouble("duration");
            this.bossBarColor = BarColor.valueOf(bossbarSection.getString("color"));
            this.bossBarStyle = BarStyle.valueOf(bossbarSection.getString("style"));
        }

        ConfigurationSection formatsSection = configuration.getConfigurationSection("formats");
        if (formatsSection != null) {
            this.global = StringUtils.color(formatsSection.getString("global"));
            this.region = StringUtils.color(formatsSection.getString("region"));
            this.owner = StringUtils.color(formatsSection.getString("owner"));
            this.member = StringUtils.color(formatsSection.getString("member"));
        }
    }

    private void loadSection(ConfigurationSection section, Map<String, String> targetMap) {
        if (section != null) {
            for (String key : section.getKeys(false)) {
                targetMap.put(key, StringUtils.color(section.getString(key)));
            }
        }
    }

    public String getNoPermissionsMessage() {
        return messagesCache.get("no-permissions");
    }

    public String getConfigReloadedMessage() {
        return messagesCache.get("config-reloaded");
    }

    public String getCommandUsage() {
        return messagesCache.get("reload-usage");
    }

    public List<String> getDisabledWorlds() {
        return new ArrayList<>(disabledWorlds);
    }

    public String getGlobalTitle() {
        return global;
    }

    public String getRegionTitle() {
        return region;
    }

    public String getOwnerTitle() {
        return owner;
    }

    public String getMemberTitle() {
        return member;
    }

    public Double getBossBarDuration() {
        return bossBarDuration;
    }

    public BarColor getBossBarColor() {
        return bossBarColor;
    }

    public BarStyle getBossBarStyle() {
        return bossBarStyle;
    }

    public boolean isPersistentDisplay() {
        return persistentDisplay;
    }

    public boolean isActionBarEnabled() {
        return actionBar;
    }

    public boolean isBossBarEnabled() {
        return bossBar;
    }

    public double getUpdateTime() {
        return updateTime;
    }

    public String getCustomRegion(String regionName) {
        return customRegions.get(regionName);
    }
}