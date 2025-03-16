package dev.h1kyou.javlyregions.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.h1kyou.javlyregions.JavlyRegions;
import dev.h1kyou.javlyregions.interfaces.IRegionManager;
import me.clip.placeholderapi.PlaceholderAPI;
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
        return JavlyRegions.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        ProtectedRegion protectedRegion = regionManager.getRegionWithMaxPriority(player, player.getLocation());

        switch (params.toLowerCase()) {
            case "title":
                String parsedTitle = PlaceholderAPI.setPlaceholders(player, StringUtils.getRegionTitleForPlayer(player, protectedRegion));
                return StringUtils.color(parsedTitle);
            case "name":
                return protectedRegion != null ? protectedRegion.getId() : null;
            case "custom":
                return protectedRegion != null ? configManager.getCustomRegion(protectedRegion.getId()) : null;
            case "owners":
                if (protectedRegion == null) return null;
                Set<UUID> ownerUUIDs = protectedRegion.getOwners().getUniqueIds();
                return StringUtils.getOwnersNamesString(ownerUUIDs);
        }
        return null;
    }
}
