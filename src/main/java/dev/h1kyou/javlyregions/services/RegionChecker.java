package dev.h1kyou.javlyregions.services;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.h1kyou.javlyregions.JavlyRegions;
import dev.h1kyou.javlyregions.utils.ConfigManager;
import dev.h1kyou.javlyregions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionChecker implements Runnable {

    private final ConfigManager configManager;
    private final Map<UUID, ProtectedRegion> lastPlayerRegions = new HashMap<>();

    public RegionChecker(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            check(player);
        }
    }

    private void check(Player player) {
        List<String> disabledWorlds = configManager.getDisabledWorlds();

        if (disabledWorlds != null && !disabledWorlds.isEmpty()) {
            World playerWorld = player.getWorld();
            for (String world : disabledWorlds) {
                if (world != null && playerWorld.getName().equalsIgnoreCase(world)) {
                    if (configManager.isBossBarEnabled()) {
                        StringUtils.clearPlayerBossBar(player);
                    }

                    return;
                }
            }
        }

        UUID playerUUID = player.getUniqueId();
        Location playerLocation = player.getLocation();
        ProtectedRegion currentRegion = JavlyRegions.getRegionManager().getRegionWithMaxPriority(player, playerLocation);
        ProtectedRegion lastRegion = lastPlayerRegions.get(playerUUID);
        boolean isPersistent = configManager.isPersistentDisplay();

        if (!isPersistent) {
            if (currentRegion != null && !currentRegion.equals(lastRegion)) {
                StringUtils.displayRegionInfo(player, currentRegion);
                lastPlayerRegions.put(playerUUID, currentRegion);
            } else if (currentRegion == null && lastRegion != null) {
                StringUtils.displayRegionInfo(player, null);
                lastPlayerRegions.remove(playerUUID);
            }
            return;
        }

        updatePlayerRegion(player, playerLocation);
    }

    private void updatePlayerRegion(Player player, Location location) {
        ApplicableRegionSet regions = JavlyRegions.getRegionManager().getRegions(location);

        if (regions.size() == 0) {
            StringUtils.displayRegionInfo(player, null);
            return;
        }

        ProtectedRegion maxPriorityRegion = JavlyRegions.getRegionManager().getRegionWithMaxPriority(player, location);

        if (maxPriorityRegion != null) {
            StringUtils.displayRegionInfo(player, maxPriorityRegion);
        }
    }

    public void start() {
        double updateTimeSeconds = configManager.getUpdateTime();
        long intervalTicks = (long) (updateTimeSeconds * 20L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(JavlyRegions.getInstance(), this, 0L, intervalTicks);
    }
}
