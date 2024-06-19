package dev.h1kyou.javlyregions.utils;

import dev.h1kyou.javlyregions.JavlyRegions;
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
    private boolean persistentActionBar;
    private double updateTime;

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
            this.persistentActionBar = section.getBoolean("persistent-actionbar");
            this.updateTime = section.getDouble("update_time");
            this.disabledWorlds.addAll(section.getStringList("disabled-worlds"));
        }
    }

    private void loadFormats(FileConfiguration configuration) {
        ConfigurationSection section = configuration.getConfigurationSection("formats");
        if (section != null) {
            this.global = StringUtils.color(section.getString("global"));
            this.region = StringUtils.color(section.getString("region"));
            this.owner = StringUtils.color(section.getString("owner"));
            this.member = StringUtils.color(section.getString("member"));
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

    public boolean getPersistentActionBarBoolean() {
        return persistentActionBar;
    }

    public double getUpdateTime() {
        return updateTime;
    }

    public String getCustomRegion(String regionName) {
        return customRegions.get(regionName);
    }
}