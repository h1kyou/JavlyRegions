package dev.h1kyou.javlyregions.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.h1kyou.javlyregions.JavlyRegions;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * Заменяет символы цвета в строке на соответствующие цветовые коды.
     * Использует амперсанд (&) в качестве символа кодирования.
     *
     * @param path строка для замены символов цвета
     * @return строка с замененными символами цвета
     */
    public static String color(String path) {
        if (Bukkit.getVersion().contains("1.12")) {
            return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', path);
        }

        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher match = pattern.matcher(path);
        StringBuffer sb = new StringBuffer();
        while (match.find()) {
            String color = path.substring(match.start() + 2, match.end());
            match.appendReplacement(sb, net.md_5.bungee.api.ChatColor.of("#" + color) + "");
        }
        match.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    /**
     * Отправляет сообщение в виде ActionBar игроку.
     *
     * @param player  Игрок, которому нужно отправить сообщение.
     * @param message Сообщение для отправки.
     */
    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(color(PlaceholderAPI.setPlaceholders(player, message))));
    }

    /**
     * Отправляет отредактированное сообщение в ActionBar игроку на основе региона.
     *
     * @param player     игрок, которому отправляется сообщение
     * @param region     регион, для которого определяется сообщение
     * @param playerUUID UUID игрока
     */
    public static void sendActionBarBasedOnRegion(Player player, ProtectedRegion region, UUID playerUUID) {
        ConfigManager configManager = JavlyRegions.getConfigManager();

        String regionName = region.getId();
        Set<UUID> ownerUUIDs = region.getOwners().getUniqueIds();

        String customRegionTitle = configManager.getCustomRegion(regionName);
        if (customRegionTitle != null) {
            StringUtils.sendActionBar(player, customRegionTitle);
            return;
        }

        String title;
        if (ownerUUIDs.contains(playerUUID)) {
            title = configManager.getOwnerTitle();
        } else if (region.getMembers().contains(playerUUID)) {
            title = configManager.getMemberTitle();
        } else {
            title = configManager.getRegionTitle();
        }

        StringUtils.sendActionBar(player, title);
    }

    /**
     * Возвращает строку с именами владельцев региона, разделяя запятыми.
     *
     * @param ownerUUIDs множество UUID владельцев региона
     * @return строка с именами владельцев региона
     */
    public static String getOwnersNamesString(Set<UUID> ownerUUIDs) {
        StringBuilder regionOwnersBuilder = new StringBuilder();

        for (UUID ownerUUID : ownerUUIDs) {
            String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
            if (ownerName != null) {
                if (regionOwnersBuilder.length() > 0) {
                    regionOwnersBuilder.append(", ");
                }
                regionOwnersBuilder.append(ownerName);
            }
        }

        return regionOwnersBuilder.toString();
    }
}