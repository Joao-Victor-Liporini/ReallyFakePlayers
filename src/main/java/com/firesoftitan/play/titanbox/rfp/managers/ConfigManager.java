package com.firesoftitan.play.titanbox.rfp.managers;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConfigManager {
    private SaveManager configFile;
    private int auto_minimum = 3;
    private int auto_maximum = 7;
    private int auto_logging_minimum = 3;
    private int auto_logging_maximum = 7;
    private boolean auto_enable = true;
    private int maximum = 60;
    private boolean interaction = true;
    private String message = "This player is on DND (Do Not Disturb)!";
    private boolean joinMessages= true;
    private List<String> firstJoinMessages;
    private List<String> reJoinMessages;
    private SaveManager nameList = new SaveManager(TitanBoxRFP.instants.getName(), "NameList");
    private SaveManager skinList = new SaveManager(TitanBoxRFP.instants.getName(), "SkinList");
    private boolean auto_Join = true;
    private boolean auto_Quit = true;
    private boolean ops_fake_tag = true;
    private int delayJoinStartup = 30;
    private List<String> groups = new ArrayList<String>();
    public ConfigManager() {
        reload();
    }
    public void reload()
    {
        configFile = new SaveManager(TitanBoxRFP.instants.getName(), "config");
        if (configFile.contains("settings.safeLogging"))
        {
            configFile.set("settings.safe_logging", configFile.getBoolean("settings.safeLogging"));
            configFile.delete("settings.safeLogging");
        }
        if (configFile.contains("settings.safe_logging"))
        {
            configFile.delete("settings.safe_logging");
        }
        if (!configFile.contains("settings.maximum"))
        {
            configFile.set("settings.maximum", 60);
        }
        if (configFile.contains("settings.interaction"))
        {
            configFile.set("settings.block_interaction", configFile.getBoolean("settings.interaction"));
            configFile.delete("settings.interaction");
        }
        if (!configFile.contains("settings.block_interaction"))
        {
            configFile.set("settings.block_interaction", true);
        }
        if (!configFile.contains("settings.message"))
        {
            configFile.set("settings.message", "This player is on DND (Do Not Disturb)!");
        }
        if (!configFile.contains("settings.ops_fake_tag"))
        {
            configFile.set("settings.ops_fake_tag", true);
        }
        if (TitanBoxRFP.isVaultInstalled()) {
            if (!configFile.contains("settings.groups")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        List<String> groups = Arrays.asList(TitanBoxRFP.permission.getGroups());
                        if (groups.size() < 1) groups.add("default");
                        configFile.set("settings.groups", groups, true);
                        configFile.save();
                        groups = configFile.getStringListFromText("settings.groups");
                    }
                }.runTaskLater(TitanBoxRFP.instants, 1);

            }
        }

        if (configFile.contains("settings.auto.textFormat")) configFile.delete("settings.auto.textFormat");
        if (!configFile.contains("settings.auto.enable"))
        {
            configFile.set("settings.auto.enable", true);
        }
        if (!configFile.contains("settings.auto.join_messages"))
        {
            configFile.set("settings.auto.join_messages", true);
        }
        if (!configFile.contains("settings.auto.firstJoinMessages"))
        {
            firstJoinMessages = new ArrayList<String>();
            firstJoinMessages.add("Welcome!");
            firstJoinMessages.add("Welcome <name>!");
            firstJoinMessages.add("Whats up <name>!");
            firstJoinMessages.add("<name>!");
            firstJoinMessages.add("Finally the one and only <name>!");
            configFile.set("settings.auto.firstJoinMessages", firstJoinMessages, true);
        }
        if (!configFile.contains("settings.auto.reJoinMessages"))
        {
            reJoinMessages = new ArrayList<String>();
            reJoinMessages.add("WB");
            reJoinMessages.add("Welcome back <name>!");
            reJoinMessages.add("Hey, <name>!");
            reJoinMessages.add("Whats up <name>!");
            reJoinMessages.add("<name>!");
            reJoinMessages.add("Hello <name>!");
            reJoinMessages.add("WB <name>!");
            configFile.set("settings.auto.reJoinMessages", reJoinMessages, true);
        }
        if (!configFile.contains("settings.auto.minimum"))
        {
            configFile.set("settings.auto.minimum", 3);
        }
        if (!configFile.contains("settings.auto.minimum"))
        {
            configFile.set("settings.auto.minimum", 3);
        }
        if (!configFile.contains("settings.auto.maximum"))
        {
            configFile.set("settings.auto.maximum", 7);
        }




        if (configFile.contains("settings.auto.loging.minimum"))
        {
            configFile.set("settings.auto.logging.minimum_minutes", configFile.getInt("settings.auto.loging.minimum"));
            configFile.delete("settings.auto.loging.minimum");
        }
        if (configFile.contains("settings.auto.loging.maximum"))
        {
            configFile.set("settings.auto.logging.maximum_minutes", configFile.getInt("settings.auto.loging.maximum"));
            configFile.delete("settings.auto.loging.maximum");
        }
        if (configFile.contains("settings.auto.loging"))
        {
            configFile.delete("settings.auto.loging");
        }
        if (!configFile.contains("settings.auto.logging.seconds_delay_join"))
        {
            configFile.set("settings.auto.logging.seconds_delay_join", 3);
        }
        if (!configFile.contains("settings.auto.logging.minimum_minutes"))
        {
            configFile.set("settings.auto.logging.minimum_minutes", 5);
        }
        if (!configFile.contains("settings.auto.logging.maximum_minutes"))
        {
            configFile.set("settings.auto.logging.maximum_minutes", 60);
        }
        if (!configFile.contains("settings.auto.logging.join"))
        {
            configFile.set("settings.auto.logging.join", true);
        }
        if (!configFile.contains("settings.auto.logging.quit"))
        {
            configFile.set("settings.auto.logging.quit", true);
        }



        this.ops_fake_tag = configFile.getBoolean("settings.ops_fake_tag");
        this.auto_enable = configFile.getBoolean("settings.auto.enable");
        this.joinMessages = configFile.getBoolean("settings.auto.join_messages");
        this.firstJoinMessages = configFile.getStringListFromText("settings.auto.firstJoinMessages");
        this.reJoinMessages = configFile.getStringListFromText("settings.auto.reJoinMessages");
        this.auto_minimum = configFile.getInt("settings.auto.minimum");
        this.auto_maximum = configFile.getInt("settings.auto.maximum");
        this.auto_Join = configFile.getBoolean("settings.auto.logging.join");
        this.auto_Quit = configFile.getBoolean("settings.auto.logging.quit");
        this.delayJoinStartup = configFile.getInt("settings.auto.logging.seconds_delay_join");
        this.auto_logging_minimum = configFile.getInt("settings.auto.logging.minimum_minutes");
        this.auto_logging_maximum = configFile.getInt("settings.auto.logging.maximum_minutes");
        this.maximum = configFile.getInt("settings.maximum");
        this.interaction = configFile.getBoolean("settings.block_interaction");
        this.message = configFile.getString("settings.message");
        this.groups = configFile.getStringListFromText("settings.groups");

        configFile.save();
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean isJoinMessages() {
        return joinMessages;
    }

    public int getDelayJoinStartup() {
        return delayJoinStartup;
    }


    public boolean isOpsFakeTag() {
        return ops_fake_tag;
    }

    public boolean isAutoJoin() {
        return auto_Join;
    }

    public boolean isAutoQuit() {
        return auto_Quit;
    }

    public List<String> getNameList()
    {
        return nameList.getStringListFromText("names");
    }
    public List<UUID> getSkinList()
    {
        List<String> skins = skinList.getStringListFromText("skins");
        List<UUID> uuids = new ArrayList<UUID>();
        for(String s: skins)
        {
            uuids.add(UUID.fromString(s));
        }
        return uuids;
    }

    public int getAutologgingMinimum() {
        return auto_logging_minimum;
    }

    public int getAutologgingMaximum() {
        return auto_logging_maximum;
    }

    public List<String> getFirstJoinMessages() {
        return firstJoinMessages;
    }

    public List<String> getReJoinMessages() {
        return reJoinMessages;
    }

    public boolean isAutoEnable() {
        return auto_enable;
    }

    public int getMaximum() {
        return maximum;
    }

    public boolean isInteraction() {
        return interaction;
    }

    public String getMessage() {
        return message;
    }

    public int getAutoMinimum() {
        return auto_minimum;
    }

    public int getAutoMaximum() {
        return auto_maximum;
    }

    public UUID getRandomSkin() {
        List<String> skins = skinList.getStringListFromText("skins");
        Random random = new Random(System.currentTimeMillis());
        int i = random.nextInt(skins.size());
        return UUID.fromString(skins.get(i));
    }
}
