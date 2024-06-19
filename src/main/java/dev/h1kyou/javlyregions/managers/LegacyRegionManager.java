package dev.h1kyou.javlyregions.managers;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.h1kyou.javlyregions.interfaces.IRegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LegacyRegionManager implements IRegionManager {

    @Override
    public ProtectedRegion getRegionWithMaxPriority(Player player, Location location) {
        ApplicableRegionSet regions = getRegions(location);

        ProtectedRegion maxPriorityRegion = null;
        for (ProtectedRegion region : regions) {
            if (maxPriorityRegion == null || region.getPriority() > maxPriorityRegion.getPriority()) {
                maxPriorityRegion = region;
            }
        }

        return maxPriorityRegion;
    }

    @Override
    public ApplicableRegionSet getRegions(Location location) {
        RegionManager regionManager = WGBukkit.getRegionManager(location.getWorld());
        return regionManager.getApplicableRegions(location);
    }
}