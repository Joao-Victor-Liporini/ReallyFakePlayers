package com.firesoftitan.play.titanbox.rfp.info;

import com.firesoftitan.play.titanbox.rfp.TitanBoxRFP;
import com.firesoftitan.play.titanbox.rfp.fakes.FakeNetworkManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class FakePlayerInfo {

    public static Property getPlayerTextureProperty(UUID uuid) throws IOException
    {
        return getPlayerTextureProperty(uuid.toString());
    }
    public static Property getPlayerTextureProperty(String uuid) throws IOException
    {
        if (uuid == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            InputStreamReader profileReader = null;
            Iterator var7;
            try {
                InputStreamReader sessionReader = null;

                try {
                    uuid = uuid.replace("-", "");
                    URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                    InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                    BufferedReader in = new BufferedReader(reader_1);
                    String inputLine;
                    String allInput = "";
                    while ((inputLine = in.readLine()) != null)
                        allInput = allInput + inputLine;
                    in.close();
                    String[] NotTheRightWay = allInput.split("value\" : \"");
                    NotTheRightWay =  NotTheRightWay[1].split("\",");
                    String texture = NotTheRightWay[0];

                    NotTheRightWay = allInput.split("signature\" : \"");
                    NotTheRightWay =  NotTheRightWay[1].split("\"");
                    String signature = NotTheRightWay[0];
                    Property property = new Property("textures", texture, signature);
                    return property;
                } finally {
                    if (Collections.singletonList(sessionReader).get(0) != null) {
                        sessionReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(profileReader).get(0) != null) {
                    profileReader.close();
                }

            }
        }
    }
    public static UUID getPlayerUUID(String name) throws IOException
    {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        } else {
            InputStreamReader profileReader = null;
            Iterator var7;
            try {
                InputStreamReader sessionReader = null;

                try {
                    URL url_1 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + System.currentTimeMillis());
                    InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                    BufferedReader in = new BufferedReader(reader_1);
                    String inputLine;
                    String allInput = "";
                    while ((inputLine = in.readLine()) != null)
                        allInput = allInput + inputLine;
                    in.close();
                    if (allInput.length() < 5) return null;
                    String[] NotTheRightWay = allInput.split("id\":\"");
                    String digits =  NotTheRightWay[1].replace("\"}", "");
                    String uuid = digits.replaceAll(
                            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                            "$1-$2-$3-$4-$5");
                    return UUID.fromString(uuid);
                } finally {
                    if (Collections.singletonList(sessionReader).get(0) != null) {
                        sessionReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(profileReader).get(0) != null) {
                    profileReader.close();
                }

            }
        }
    }
    private EntityPlayer entityPlayer;
    private long joinTime;
    private String textFormat = null;

    public void setTextFormat() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());
                AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, getCraftPlayer(), "<message>", playerSet);
                Bukkit.getPluginManager().callEvent(asyncPlayerChatEvent);
                String formatted = String.format(asyncPlayerChatEvent.getFormat(), "<fakename>", asyncPlayerChatEvent.getMessage());
                formatted = formatted.replace(getCraftPlayer().getDisplayName(), "<fakename>");
                formatted = formatted.replace(getCraftPlayer().getName(), "<fakename>");
                formatted = formatted.replace('§', '&');
                textFormat = formatted;
            }
        }.runTaskLater(TitanBoxRFP.instants, 1);

    }

    public FakePlayerInfo(String name) {
        setupPlayer(name, UUID.randomUUID());
    }
    public FakePlayerInfo(String name, UUID uuid) {
        setupPlayer(name, uuid);
    }
    private void setupPlayer(String name, UUID uuid)
    {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        World world = Bukkit.getWorlds().get(0);
        WorldServer nmsWorld = ((CraftWorld)world).getHandle();
        if (name.length() > 16) name = name.substring(0, 16);
        GameProfile gameProfile = new GameProfile(uuid, name);
        this.entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile);
        Random random = new Random(System.currentTimeMillis());
        this.entityPlayer.e =  random.nextInt(10)+ 20;
        FakeNetworkManager networkmanager = new FakeNetworkManager(EnumProtocolDirection.a);
        this.entityPlayer.b = new PlayerConnection(nmsServer, networkmanager, this.entityPlayer);
        this.joinTime = System.currentTimeMillis();

    }

    public long getJoinTime() {
        return joinTime;
    }

    public GameProfile getGameProfile()
    {
        return this.entityPlayer.getBukkitEntity().getProfile();
    }
    public PlayerConnection getConnection()
    {
        return this.entityPlayer.b;
    }
    public FakeNetworkManager getFakeNetworkManager()
    {
        return (FakeNetworkManager) this.entityPlayer.b.a;
    }
    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }
    public CraftPlayer getCraftPlayer()
    {
        return entityPlayer.getBukkitEntity();
    }

    private void loadSkinToServer(UUID uuid)
    {
        String name = uuid.toString();
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        World world = Bukkit.getWorlds().get(0);
        WorldServer nmsWorld = ((CraftWorld)world).getHandle();
        if (name.length() > 16) name = name.substring(0, 16);
        GameProfile gameProfile = new GameProfile(uuid, name);
        EntityPlayer entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile);
        entityPlayer.b = new PlayerConnection(nmsServer, new FakeNetworkManager(EnumProtocolDirection.a), entityPlayer);
    }
    public void setSkin( UUID skin)
    {
        //\/ \/ \/ This loads the skin to the server
        loadSkinToServer(skin);
        ///\ /\ /\ This loads the skin to the server


        try {
            Property property = FakePlayerInfo.getPlayerTextureProperty(skin);
            update(property);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void update(Property property)
    {

        EntityPlayer npc = this.getEntityPlayer();
        GameProfile gameProfile = npc.getBukkitEntity().getProfile();
        PropertyMap properties = gameProfile.getProperties();
        properties.clear();
        properties.put("textures", property);
    }
    public void sendChatMessageOut(String message)
    {
        Set<Player> playerSet = new HashSet<Player>(Bukkit.getOnlinePlayers());

        String formattedMessage = textFormat;
        if (formattedMessage == null || formattedMessage.length() < 1) return;
        formattedMessage = formattedMessage.replace("<message>", message);
        formattedMessage = formattedMessage.replace("<fakename>", this.getName());
        for(Player player: playerSet)
        {
            if (TitanBoxRFP.configManager.isOpsFakeTag()) {
                if (TitanBoxRFP.hasAdminPermission(player) || player.hasPermission("titanbox.rfp.show")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage) + ChatColor.GRAY + " [" + "I'm Fake]");
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage));
                }
            }
            else
            {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage));
            }
        }

    }

    public String getName() {
        return entityPlayer.getBukkitEntity().getName();
    }

    public UUID getUniqueID() {
        return entityPlayer.getBukkitEntity().getUniqueId();
    }
}
