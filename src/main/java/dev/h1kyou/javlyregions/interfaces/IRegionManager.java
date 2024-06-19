package dev.h1kyou.javlyregions.interfaces;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IRegionManager {
    ProtectedRegion getRegionWithMaxPriority(Player player, Location location);

    ApplicableRegionSet getRegions(Location location);
}