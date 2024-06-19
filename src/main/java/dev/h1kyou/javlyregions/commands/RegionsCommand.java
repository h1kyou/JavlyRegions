package dev.h1kyou.javlyregions.commands;

import dev.h1kyou.javlyregions.JavlyRegions;
import dev.h1kyou.javlyregions.utils.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RegionsCommand implements CommandExecutor, TabCompleter {

    private final ConfigManager configManager = JavlyRegions.getConfigManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(configManager.getCommandUsage());
            return false;
        }

        if (sender instanceof Player && !sender.hasPermission("regions.reload")) {
            sender.sendMessage(configManager.getNoPermissionsMessage());
            return true;
        }

        configManager.reloadConfiguration();
        sender.sendMessage(configManager.getConfigReloadedMessage());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
            return completions;
        }
        return Collections.emptyList();
    }
}
