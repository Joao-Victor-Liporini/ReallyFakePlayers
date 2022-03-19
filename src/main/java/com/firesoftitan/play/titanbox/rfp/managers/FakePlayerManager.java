package com.firesoftitan.play.titanbox.rfp.managers;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.info.FakePlayerInfo;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
public class FakePlayerManager {

    private HashMap<String, FakePlayerInfo> playerHashMap;

    private List<EntityPlayer> playerListFree;
    private Map<UUID, EntityPlayer> onlineListFree;
    private Random random = new Random(System.currentTimeMillis());
    public FakePlayerManager() {
        playerHashMap = new HashMap<String, FakePlayerInfo>();

        // bf = getPlayerList
        DedicatedPlayerList playerList = ((CraftServer) Bukkit.getServer()).getServer().bf();

        Field privateStringField = null;
        try {
            privateStringField = PlayerList.class.getDeclaredField("j");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        privateStringField.setAccessible(true);

        try {
            playerListFree =  (List<EntityPlayer>)privateStringField.get(playerList);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            privateStringField = PlayerList.class.getDeclaredField("k");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        privateStringField.setAccessible(true);

        try {
            onlineListFree =  (Map<UUID, EntityPlayer>)privateStringField.get(playerList);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public List<FakePlayerInfo> getPlayerInfoList()
    {
        return new ArrayList<>(playerHashMap.values());
    }
    public FakePlayerInfo getPlayer(String name)
    {
        for(FakePlayerInfo pl: getPlayerInfoList())
        {
            if (pl.getName().equals(name)) return pl;
        }
        return null;
    }
    public void welcomePlayer(Player player) {

        if (!TitanBoxRFP.configManager.isJoinMessages()) return;
        FakePlayerManager fakePlayerManager = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> firstJoinMessages = null;
                if (player.getFirstPlayed() + 10000 > System.currentTimeMillis()) {
                    firstJoinMessages = TitanBoxRFP.configManager.getFirstJoinMessages();
                }
                else
                {
                    firstJoinMessages = TitanBoxRFP.configManager.getReJoinMessages();
                }
                if (firstJoinMessages == null) return;
                for (FakePlayerInfo entityPlayer : fakePlayerManager.getPlayerInfoList()) {
                    if (random.nextInt(3) < 2 && !player.getName().equals(entityPlayer.getName())) {
                        String message = firstJoinMessages.get(random.nextInt(firstJoinMessages.size()));
                        if (message == null || message.length() < 1) return;
                        message = message.replace("<name>", player.getName());
                        message = ChatColor.translateAlternateColorCodes('&', message);
                        String finalMessage = message;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                entityPlayer.sendChatMessageOut(finalMessage);
                            }
                        }.runTaskLater(TitanBoxRFP.instants, random.nextInt(100));
                    }
                }
            }
        }.runTaskLater(TitanBoxRFP.instants, 30);
    }
    public void add(String name, boolean welcome)
    {
        if (name.length() > 16) name = name.substring(0, 16);
        FakePlayerInfo npc = new FakePlayerInfo(name);
        npc.setSkin(TitanBoxRFP.configManager.getRandomSkin());
        playerHashMap.put(name, npc);
        playerListFree.add(npc.getEntityPlayer());
        onlineListFree.put(npc.getEntityPlayer().getBukkitEntity().getUniqueId(), npc.getEntityPlayer());
        // co = getScoreboardDisplayName
        ChatMessage chatmessage = new ChatMessage("multiplayer.player.joined", new Object[]{npc.getEntityPlayer().co()});
        String joinMessage = CraftChatMessage.fromComponent(chatmessage);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> groups = TitanBoxRFP.configManager.getGroups();
                if (groups.size() == 0) {
                    removeOldGroups(npc);
                    TitanBoxRFP.permission.playerAddGroup(null, npc.getCraftPlayer(), "default");
                }
                else
                {
                    removeOldGroups(npc);
                    TitanBoxRFP.permission.playerAddGroup(null, npc.getCraftPlayer(), groups.get(random.nextInt(groups.size())));
                }
                npc.setTextFormat();
            }
        }.runTaskLater(TitanBoxRFP.instants, 1);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(npc.getCraftPlayer(), joinMessage);
        Bukkit.getPluginManager().callEvent(playerJoinEvent);

        String joining = playerJoinEvent.getJoinMessage();
        if (joining == null || joining.length() < 1) joining = ChatColor.YELLOW + name + " joined the game.";
        joinMessage = ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',joining);

        Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());
        for(Player player: playerSet) {
            if (TitanBoxRFP.configManager.isOpsFakeTag()) {
                if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show")) {
                    player.sendMessage(joinMessage + ChatColor.GRAY + " [Fake Player]");
                } else {
                    player.sendMessage(joinMessage);
                }
            }
            else
            {
                player.sendMessage(joinMessage);
            }
        }
        if (welcome) this.welcomePlayer(npc.getCraftPlayer());
    }

    private void removeOldGroups(FakePlayerInfo npc) {
        String[] oldGroups = TitanBoxRFP.permission.getPlayerGroups(null, npc.getCraftPlayer());
        for(String g: oldGroups)
        {
            TitanBoxRFP.permission.playerRemoveGroup(null, npc.getCraftPlayer(), g);
        }
    }

    public int count()
    {
        return playerHashMap.size();
    }
    public void addMore(int number)
    {
        boolean welcome = false;
        if (number == 1) welcome = true;
        for(int i = 0; i < number; i++)
        {
            String nameF = getRandomName();
            add(nameF, welcome);
        }
        for(Player all: Bukkit.getOnlinePlayers()) {
            showList(all);
        }
    }
    public boolean contains(String name)
    {
        return playerHashMap.containsKey(name);
    }
    public void removeAll()
    {
        for (FakePlayerInfo pl: getPlayerInfoList()) {
            remove(pl);
        }
    }
    public void remove(FakePlayerInfo npc)
    {
        if (npc != null)
        {
            if (npc.getName().equalsIgnoreCase(npc.getName().toLowerCase())) {
                this.playerHashMap.remove(npc.getName());

                this.playerListFree.remove(npc.getEntityPlayer());
                this.onlineListFree.remove(npc.getUniqueID());

               /* if (TitanBoxRFP.configManager.isSafeLogging()) {

                    List<EntityPlayer> test = new ArrayList<EntityPlayer>();
                    test.add(npc.getEntityPlayer());

                    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, test);
                    try {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) player).getHandle().b.sendPacket(packet);
                        }
                    } catch (Exception e) {

                    }
                }
                else
                {
                    try {
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) all).getHandle().b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc.getEntityPlayer())); //REMOVE PLAYER
                        }
                    }
                    catch (Exception e)
                    {

                    }
                }*/
                try {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                    	// b.a = sendPacket
                        ((CraftPlayer) all).getHandle().b.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc.getEntityPlayer())); //REMOVE PLAYER
                    }
                }
                catch (Exception e)
                {

                }
                PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(npc.getCraftPlayer(), "§e" + npc.getName() + " left the game");
                Bukkit.getPluginManager().callEvent(playerQuitEvent);
                String quiting = playerQuitEvent.getQuitMessage();
                if (quiting == null || quiting.length() < 1) quiting = "§e" + npc.getName() + " left the game";
                String quitMessage = ChatColor.translateAlternateColorCodes('&',quiting);

                Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());
                for(Player player: playerSet) {
                    if (TitanBoxRFP.configManager.isOpsFakeTag()) {
                        if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show")) {
                            player.sendMessage(quitMessage + ChatColor.GRAY + " [Fake Player]");
                        } else {
                            player.sendMessage(quitMessage);
                        }
                    }
                    else
                    {
                        player.sendMessage(quitMessage);
                    }
                }
            }
        }
    }

    public void remove(String name)
    {
        FakePlayerInfo pl = getPlayer(name);
        remove(pl);
    }
    public void showList(Player player)
    {
        if (player != null)
        {
            for (FakePlayerInfo pl: getPlayerInfoList()) {
            	// b.a = sendPacket
                ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, pl.getEntityPlayer())); //ADD_PLAYER
            }
        }
    }
    public String getRandomName()
    {
      return getRandomName(0);
    }
    private String getRandomName(int count)
    {
        List<String> playerNames = TitanBoxRFP.configManager.getNameList();
        count++;
        Random random = new Random(System.currentTimeMillis());
        int nameIndex = random.nextInt(playerNames.size());
        String nameF = playerNames.get(nameIndex);
        if (count > 5) nameF = nameF + "_" + random.nextInt(100000);
        if (playerHashMap.containsKey(nameF)) return getRandomName(count);
        return nameF;
    }
}
