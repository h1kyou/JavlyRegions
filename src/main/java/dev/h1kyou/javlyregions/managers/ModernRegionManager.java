package dev.h1kyou.javlyregions.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.h1kyou.javlyregions.interfaces.IRegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ModernRegionManager implements IRegionManager {

    private final WorldGuardPlatform worldGuardPlatform;

    public ModernRegionManager(WorldGuardPlatform worldGuardPlatform) {
        this.worldGuardPlatform = worldGuardPlatform;
    }

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
        RegionContainer regionContainer = worldGuardPlatform.getRegionContainer();
        RegionQuery query = regionContainer.createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(location));
    }
}