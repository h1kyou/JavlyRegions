package dev.h1kyou.javlyregions.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.h1kyou.javlyregions.JavlyRegions;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Map<UUID, BossBar> playerBossBars = new HashMap<>();

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

    public static void sendBossBar(Player player, String message) {
        ConfigManager configManager = JavlyRegions.getConfigManager();

        String title = color(PlaceholderAPI.setPlaceholders(player, message));
        BarColor color = configManager.getBossBarColor();
        BarStyle style = configManager.getBossBarStyle();
        boolean isPersistent = configManager.isPersistentDisplay();

        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        UUID playerUUID = player.getUniqueId();

        clearPlayerBossBar(player);

        bossBar.addPlayer(player);
        playerBossBars.put(playerUUID, bossBar);

        if (!isPersistent) {
            Double displayDurationSeconds = configManager.getBossBarDuration();
            new BukkitRunnable() {
                @Override
                public void run() {
                    clearPlayerBossBar(player);
                }
            }.runTaskLater(JavlyRegions.getInstance(), (long) (displayDurationSeconds * 20L));
        }
    }

    /**
     * Удаляет BossBar у игрока.
     *
     * @param player Игрок, у которого нужно удалить BossBar.
     */
    private static void clearPlayerBossBar(Player player) {
        UUID playerUUID = player.getUniqueId();
        BossBar bossBar = playerBossBars.remove(playerUUID);

        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    /**
     * Удаляет BossBar у всех игроков.
     */
    public static void clearBossBars() {
        for (Map.Entry<UUID, BossBar> entry : playerBossBars.entrySet()) {
            BossBar bossBar = entry.getValue();
            if (bossBar != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    bossBar.removePlayer(player);
                }
            }
        }
        playerBossBars.clear();
    }

    /**
     * Отправляет отредактированное сообщение в ActionBar и BossBar игроку на основе региона.
     *
     * @param player     игрок, которому отправляется сообщение
     * @param region     регион, для которого определяется сообщение
     */
    public static void displayRegionInfo(Player player, ProtectedRegion region) {
        ConfigManager configManager = JavlyRegions.getConfigManager();

        boolean isActionBarEnabled = configManager.isActionBarEnabled();
        boolean isBossBarEnabled = configManager.isBossBarEnabled();

        String title = (region == null) ? configManager.getGlobalTitle() : getRegionTitle(configManager, region, player.getUniqueId());

        if (isActionBarEnabled) {
            sendActionBar(player, title);
        }

        if (isBossBarEnabled) {
            sendBossBar(player, title);
        }
    }

    /**
     * Определяет заголовок сообщения региона на основе конфигурации и принадлежности игрока.
     *
     * @param configManager  конфигурация
     * @param region         регион
     * @param playerUUID     UUID игрока
     * @return               заголовок сообщения
     */
    private static String getRegionTitle(ConfigManager configManager, ProtectedRegion region, UUID playerUUID) {
        String regionName = region.getId();
        Set<UUID> ownerUUIDs = region.getOwners().getUniqueIds();

        String customRegionTitle = configManager.getCustomRegion(regionName);
        if (customRegionTitle != null) {
            return customRegionTitle;
        }

        if (ownerUUIDs.contains(playerUUID)) {
            return configManager.getOwnerTitle();
        }

        if (region.getMembers().contains(playerUUID)) {
            return configManager.getMemberTitle();
        }

        return configManager.getRegionTitle();
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