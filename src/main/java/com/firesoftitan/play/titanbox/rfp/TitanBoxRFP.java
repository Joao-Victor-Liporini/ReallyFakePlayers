package com.firesoftitan.play.titanbox.rfp;

import com.firesoftitan.play.titanbox.rfp.info.FakePlayerInfo;
import com.firesoftitan.play.titanbox.rfp.listeners.MainListener;
import com.firesoftitan.play.titanbox.rfp.managers.AutoUpdateManager;
import com.firesoftitan.play.titanbox.rfp.managers.ConfigManager;
import com.firesoftitan.play.titanbox.rfp.managers.FakePlayerManager;
import com.firesoftitan.play.titanbox.rfp.runnables.AutoLogerRunnable;
import net.milkbowl.vault.permission.Permission;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class TitanBoxRFP extends JavaPlugin {
    public static ConfigManager configManager;
    public static TitanBoxRFP instants;
    public static MainListener mainListener;
    public static FakePlayerManager fakePlayerManager;
    public static Permission permission;
    private static boolean isVaultInstalled;
    public static boolean update = false;
    public void onEnable() {
        instants = this;
        mainListener = new MainListener();
        mainListener.registerEvents();

        createMissingFile("NameList.yml");
        createMissingFile("SkinList.yml");

        configManager = new ConfigManager();
        fakePlayerManager = new FakePlayerManager();
        isVaultInstalled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (isVaultInstalled)
        {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            permission = permissionProvider.getProvider();
            sendMessageSystem(TitanBoxRFP.this, "Vault Hooked!");
        }
        else
        {
            sendMessageSystem(TitanBoxRFP.this, "No Vault Installed!");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                new AutoUpdateManager(TitanBoxRFP.this, 95927).getVersion(version -> {
                    if (TitanBoxRFP.this.getDescription().getVersion().equalsIgnoreCase(version)) {
                        sendMessageSystem(TitanBoxRFP.this, "Plugin is up to date.");
                    } else {
                        TitanBoxRFP.update = true;
                        sendMessageSystem(TitanBoxRFP.this, "There is a new update available.");
                        sendMessageSystem(TitanBoxRFP.this, "https://www.spigotmc.org/resources/really-fake-players.95927");
                    }
                });
            }
        }.runTaskLater(this,20);


        TitanBoxRFP.instants.setupAutoSpawn();
    }

    public void setupAutoSpawn() {
        if (configManager.isAutoEnable()) {
            int delayJoinStartup = configManager.getDelayJoinStartup() * 20;
            if (delayJoinStartup < 1) delayJoinStartup = 1;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Random random = new Random(System.currentTimeMillis());
                    int min = configManager.getAutoMinimum();
                    int max = configManager.getAutoMaximum() - min;
                    if (max < 1) max = 1;
                    int number = random.nextInt(max) + min;
                    int addN = Math.min(number, configManager.getMaximum());
                    fakePlayerManager.addMore(addN);
                }
            }.runTaskLater(this, delayJoinStartup);

            new AutoLogerRunnable().runTaskTimer(this, 30*20, 30*20);
        }
    }

    public void createMissingFile(String filename) {
        File nfile = new File("plugins" + File.separator + this.getName() + File.separator +   filename);
        if (!nfile.exists())
        {
            InputStream link = getResource(filename);
            try {
                FileUtils.copyInputStreamToFile(link, nfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean isVaultInstalled()
    {
        return isVaultInstalled;
    }
    public static boolean hasAdminPermission(CommandSender sender)
    {
        if (sender.hasPermission("titanbox.rfp.admin") || sender.hasPermission("titanbox.admin") || sender.isOp()) return true;
        return false;
    }
    public void sendMessage(CommandSender sender, String text)
    {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            sendMessagePlayer(this, player,  text);
        }
        else
        {
            sendMessageSystem(this, text);
        }
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (args.length > 0)
            {
                if (!(sender instanceof Player) || TitanBoxRFP.hasAdminPermission((Player) sender)) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        configManager.reload();
                        sendMessage(sender,ChatColor.AQUA + "Config has been reloaded!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("add")) {
                        if (args.length > 1) {
                            if (TitanBoxRFP.isNumber(args[1])) {
                                int number = Integer.parseInt(args[1]);
                                int addN = Math.min(number, configManager.getMaximum() - fakePlayerManager.count());
                                if (addN < 1)
                                {
                                    sendMessage(sender,   ChatColor.AQUA + "You have reached the max, you can change this in the settings.");
                                    return true;
                                }
                                fakePlayerManager.addMore(addN);
                                sendMessage(sender,  ChatColor.WHITE + "" + addN + "" + ChatColor.AQUA + " players have been added.");
                            } else {
                                if (fakePlayerManager.count() >= configManager.getMaximum())
                                {
                                    sendMessage(sender,   ChatColor.AQUA + "You have reached the max, you can change this in the settings.");
                                    return true;
                                }
                                fakePlayerManager.add(args[1], true);
                                for(Player all: Bukkit.getOnlinePlayers()) {
                                    fakePlayerManager.showList(all);
                                }
                                sendMessage(sender,  ChatColor.WHITE + args[1] + ChatColor.AQUA + " player has been added.");
                            }
                            return true;
                        }
                        sendMessage(sender,  ChatColor.GOLD + "/rfp add <#,name>");
                        sendMessage(sender,  ChatColor.GRAY + "Example: " + ChatColor.WHITE + "/rfp add 12 " + ChatColor.GRAY + "or" + ChatColor.WHITE + " /rfp add mice");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("all")) {
                                fakePlayerManager.removeAll();
                                sendMessage(sender,  ChatColor.AQUA + "all players have been removed.");
                            } else {
                                if (fakePlayerManager.contains(args[1])) {
                                    fakePlayerManager.remove(args[1]);
                                    sendMessage(sender,  ChatColor.WHITE + args[1] + ChatColor.AQUA + " player has been removed.");
                                }
                                else
                                {
                                    sendMessage(sender,  ChatColor.WHITE + args[1] + ChatColor.AQUA + " no player with that name.");
                                }
                            }
                            return true;
                        }
                        sendMessage(sender,  ChatColor.GOLD + "/rfp remove <all,name>");
                        sendMessage(sender,  ChatColor.GRAY + "Example: " + ChatColor.WHITE + "/rfp remove all " + ChatColor.GRAY + "or" + ChatColor.WHITE + " /rfp remove mice");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("list")) {
                        List<String> names = new ArrayList<String>();
                        for (FakePlayerInfo entityPlayer : fakePlayerManager.getPlayerInfoList()) {
                            names.add(entityPlayer.getName());
                        }
                        sendMessageList(this, sender,  names);
                        return true;
                    }
                }
            }
        this.help(sender);
        return true;
    }
    public void help(CommandSender sender)
    {
        List<String> commandHelp = new ArrayList<String>();

        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp help");
        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp add <#>");
        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp add <name>");
        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp remove <name>");
        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp remove all");
        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp list");
        if (TitanBoxRFP.hasAdminPermission(sender)) commandHelp.add(ChatColor.GOLD + "/rfp reload");
        if (TitanBoxRFP.hasAdminPermission(sender) || sender.hasPermission("titanbox.rfp.show") || sender.isOp()) commandHelp.add(ChatColor.GOLD + "You can see " + ChatColor.GRAY + " [Fake Player]" + ChatColor.GOLD + " label");
        TitanBoxRFP.sendMessageList(this, sender, commandHelp);
    }
    public static boolean isNumber(String message)
    {
        try {
            int i = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    public static void sendMessageSystem(JavaPlugin plugin, String message)
    {
        sendMessageSystem(plugin, message, Level.INFO);
    }
    public static void sendMessageSystem(JavaPlugin plugin, String message, Level level)
    {
        String subName = plugin.getName().replace("TitanBox", "");
        plugin.getLogger().log(level,  ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
        //ChatColor.GREEN + "("+ ChatColor.AQUA + subName + ChatColor.GREEN + "): " +
    }
    public static void sendMessageList(JavaPlugin plugin, CommandSender sender, List<String> messages)
    {
        String subName = plugin.getName().replace("TitanBox", "");
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-------------" + ChatColor.RESET + ChatColor.GREEN + "[" + ChatColor.BLUE + "TitanBox" + ChatColor.GREEN + "](" + ChatColor.AQUA + subName + ChatColor.GREEN + ")" + ChatColor.GRAY + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-------------");
            for (String s : messages) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "-------------" + "-------------");
        }
        else
        {
            TitanBoxRFP.sendMessageSystem(plugin,ChatColor.GRAY + "" +ChatColor.BOLD +  ChatColor.STRIKETHROUGH + "-------------" + ChatColor.RESET + ChatColor.GREEN + "[" + ChatColor.BLUE + "TitanBox" + ChatColor.GREEN + "]("+ ChatColor.AQUA + subName + ChatColor.GREEN + ")" + ChatColor.GRAY + "" +ChatColor.BOLD +  ChatColor.STRIKETHROUGH + "-------------");
            for(String s: messages) {
                TitanBoxRFP.sendMessageSystem(plugin,ChatColor.translateAlternateColorCodes('&', s));
            }
            TitanBoxRFP.sendMessageSystem(plugin,ChatColor.GRAY + "" +ChatColor.BOLD +  ChatColor.STRIKETHROUGH + "-------------" + "-------------");
            return;
        }
    }
    public static void sendMessageList(JavaPlugin plugin, Player player, String... messages)
    {
        List<String> mes = new ArrayList<String>();
        for(String s: messages)
        {
            mes.add(s);
        }
        sendMessageList(plugin, player, mes);
    }
    public static void sendMessagePlayer(JavaPlugin plugin, Player player, String message)
    {
        if (player == null || !player.isOnline())
        {
            TitanBoxRFP.sendMessageSystem(plugin, message);
            return;
        }
        String subName = plugin.getName().replace("TitanBox", "");
        player.sendMessage(ChatColor.GREEN + "[" + ChatColor.BLUE + "TitanBox" + ChatColor.GREEN + "]("+ ChatColor.AQUA + subName + ChatColor.GREEN + "): " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', message));
    }

    public void onDisable()
    {
        fakePlayerManager.removeAll();
        this.saveALL();
    }
    public void saveALL()
    {

    }
}
