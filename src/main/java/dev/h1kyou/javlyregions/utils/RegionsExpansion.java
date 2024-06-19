package dev.h1kyou.javlyregions.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.h1kyou.javlyregions.interfaces.IRegionManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class RegionsExpansion extends PlaceholderExpansion {

    private final ConfigManager configManager;
    private final IRegionManager regionManager;

    public RegionsExpansion(ConfigManager configManager, IRegionManager regionManager) {
        this.configManager = configManager;
        this.regionManager = regionManager;
    }

    @Override
    public @NotNull String getAuthor() {
        return "h1kyou";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "javlyregions";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.6";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        ProtectedRegion protectedRegion = regionManager.getRegionWithMaxPriority(player, player.getLocation());

        if (protectedRegion == null) {
            return null;
        }

        switch (params.toLowerCase()) {
            case "name":
                return protectedRegion.getId();
            case "custom":
                return configManager.getCustomRegion(protectedRegion.getId());
            case "owners":
                Set<UUID> ownerUUIDs = protectedRegion.getOwners().getUniqueIds();
                return StringUtils.getOwnersNamesString(ownerUUIDs);
        }
        return null;
    }
}
