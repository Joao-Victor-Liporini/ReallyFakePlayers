package com.firesoftitan.play.titanbox.rfp.managers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SaveManager {

    /**
     * Decodes an {@link ItemStack} from a Base64 String
     * @param string Base64 encoded String to decode
     * @return Decoded {@link ItemStack}
     */
    public static ItemStack decodeItemStack(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
//            e.printStackTrace();
            //return null;
        }
        ItemStack outItem = null;
        if (config.contains("i")) {
            outItem = config.getItemStack("i", null);
            if (outItem == null) {
                config = new YamlConfiguration();
                try {
                    config.loadFromString(config.saveToString());
                } catch (IllegalArgumentException | InvalidConfigurationException e) {
                    //              e.printStackTrace();
                    return null;
                }
                outItem = config.getItemStack("i", null);
            }
        }
        if (config.contains("x"))
        {
            try {
                String item = config.getString("x");
                NBTTagCompound item2 = MojangsonParser.a(item);
                outItem = CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(item2));
            } catch (CommandSyntaxException e) {
//            e.printStackTrace();
            }
        }

        return outItem;
    }
    public static List<EntityType> decodeEntityTypeList(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<EntityType> tmp = new ArrayList<EntityType>();
        for (String key: config.getKeys(false))
        {
            if (key.equals("i"))
            {
                return tmp;
            }
            EntityType itsub = EntityType.valueOf(config.getString(key));
            tmp.add(itsub);
        }

        return tmp;
    }
    public static List<UUID> decodeUUIDList(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<UUID> tmp = new ArrayList<UUID>();
        for (String key: config.getKeys(false))
        {
            if (key.equals("i"))
            {
                return tmp;
            }
            UUID itsub = UUID.fromString(config.getString(key));
            tmp.add(itsub);
        }

        return tmp;
    }
    public static List<Location> decodeLocationList(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<Location> tmp = new ArrayList<Location>();
        for (String key: config.getKeys(false))
        {
            if (key.equals("i"))
            {
                return tmp;
            }
            Location itsub = config.getLocation(key);
            tmp.add(itsub);
        }

        return tmp;
    }
    /**
     * Decodes an {@link ItemStack} from a Base64 String
     * @param string Base64 encoded String to decode
     * @return Decoded {@link ItemStack}
     */
    public static List<ItemStack> decodeItemList(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<ItemStack> tmp = new ArrayList<ItemStack>();
        for (String key: config.getKeys(false))
        {
            if (key.equals("i"))
            {
                return tmp;
            }
            ItemStack itsub = config.getItemStack(key);
            tmp.add(itsub);
        }

        return tmp;
    }

    /**
     * Decodes an {@link ItemStack} from a Base64 String
     * @param string Base64 encoded String to decode
     * @return Decoded {@link ItemStack}
     */
    public static List<String> decodeStringList(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<String> tmp = (List<String>) config.getList("i", new ArrayList<String>());
        return tmp;
    }
    /**
     * Decodes an {@link ItemStack} from a Base64 String
     * @param string Base64 encoded String to decode
     * @return Decoded {@link ItemStack}
     */
    public static List<Integer> decodeIntList(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        List<Integer> tmp = (List<Integer>) config.getList("i", new ArrayList<Integer>());
        return tmp;
    }

    /**
     * Decodes an {@link Location} from a Base64 String
     * @param string Base64 encoded String to decode
     * @return Decoded {@link Location}
     */
    public static Location decodeLocation(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        double x = config.getDouble("i.x");
        double y = config.getDouble("i.y");
        double z = config.getDouble("i.z");
        float pitch =  Float.valueOf(config.getString("i.pitch"));
        float yaw = Float.valueOf(config.getString("i.yaw"));
        String worldname = config.getString("i.world");
        World world = Bukkit.getWorld(worldname);
        Location location = new Location(world, x, y, z, yaw, pitch);
        return  location.clone();
    }
    /**
     * Encodes an {@link String} in a Base64 String
     * @param string {@link String} to encode
     * @return Base64 encoded String
     */
    public static String encode(String string) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("s", string);
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Encodes an {@link ItemStack} in a Base64 String
     * @param itemStack {@link ItemStack} to encode
     * @return Base64 encoded String
     */
    public static String encode(ItemStack itemStack) {
        String configString = "";
        YamlConfiguration config = null;
        // b = save
        NBTTagCompound item = CraftItemStack.asNMSCopy(itemStack).b(new NBTTagCompound());
        config = new YamlConfiguration();
//      config.set("x", item.asString());
        config.set("x", item.toString());
        configString = config.saveToString();
        byte[] configBytes = configString.getBytes(StandardCharsets.UTF_8);
        String out = Base64.getEncoder().encodeToString(configBytes);

        /* "i" is old format that is buggy
        try { //as of 1.16 this sometimes throw an error, so we have to save it a different way
            config = new YamlConfiguration();
            config.set("i", itemStack);
            configString = config.saveToString();
        } catch (Exception e) {
            NBTTagCompound item = CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound());
            config = new YamlConfiguration();
            config.set("x", item.asString());
            configString = config.saveToString();
        }
            byte[] configBytes = configString.getBytes(StandardCharsets.UTF_8);
            String out = Base64.getEncoder().encodeToString(configBytes);
            if (decodeItemStack(out) == null)
            {
                NBTTagCompound item = CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound());
                config = new YamlConfiguration();
                config.set("x", item.asString());
                configString = config.saveToString();
                configBytes = configString.getBytes(StandardCharsets.UTF_8);
                out = Base64.getEncoder().encodeToString(configBytes);
            }*/
            return out;



    }
    /**
     * Encodes an {@link Location} in a Base64 String
     * @param location {@link Location} to encode
     * @return Base64 encoded String
     */
    public static String encode(Location location) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i.x", location.getX());
        config.set("i.y", location.getY());
        config.set("i.z", location.getZ());
        config.set("i.pitch", location.getPitch() + "");
        config.set("i.yaw", location.getYaw() + "");
        if (location.getWorld() == null)
        {
            config.set("i.world", "worldmain");
        }
        else {
            config.set("i.world", location.getWorld().getName());
        }
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Encodes an {@link List<Integer>} in a Base64 String
     * Encodes an {@link List<String>} in a Base64 String
     * Encodes an {@link List<ItemStack>} in a Base64 String
     * @param list {@link List} to encode
     * @return Base64 encoded String
     */
    public static String encode(List list) {
        YamlConfiguration config = new YamlConfiguration();
        if (list.size() > 0)
        {
            if (list.get(0) instanceof  ItemStack)
            {
                int i = 0;
                for (ItemStack is: (List<ItemStack>)list)
                {
                    config.set("i" + i, is);
                    i++;
                }
                return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
            }
            if (list.get(0) instanceof  Location)
            {
                int i = 0;
                for (Location is: (List<Location>)list)
                {
                    config.set("i" + i, is.clone());
                    i++;
                }
                return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
            }
            if (list.get(0) instanceof  UUID)
            {
                int i = 0;
                for (UUID is: (List<UUID>)list)
                {
                    config.set("i" + i, is.toString());
                    i++;
                }
                return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
            }
            if (list.get(0) instanceof  EntityType)
            {
                int i = 0;
                for (EntityType is: (List<EntityType>)list)
                {
                    config.set("i" + i, is.name());
                    i++;
                }
                return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
            }


        }
        config.set("i", list);
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }
    private static String encode(YamlConfiguration config)
    {
        if (config == null) return null;
        return Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    private static YamlConfiguration decodeYaml(String data)
    {
        if (data == null || data.length() <1) return null;
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

//---------------------------------------------------------------------------------------------------------------------
    private YamlConfiguration config;
    private File file;
    public SaveManager(String pluginname, String folder, String fileName) {
        try {
            config = new YamlConfiguration();
            File dataStorageDIR = new File("plugins");
            if (!dataStorageDIR.exists())
            {
                dataStorageDIR.mkdir();
            }
            File TitanBoxDIR = new File("plugins" + File.separator + pluginname );
            if (!TitanBoxDIR.exists())
            {
                TitanBoxDIR.mkdir();
            }
            File TitanBoxDIRF = new File("plugins" + File.separator + pluginname + File.separator + folder );
            if (!TitanBoxDIRF.exists())
            {
                TitanBoxDIRF.mkdir();
            }

            this.file = new File("plugins" + File.separator + pluginname + File.separator + folder + File.separator  + fileName + ".yml");
            if (!this.file.exists())
            {
                this.file.createNewFile();
            }
            config.load(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();

        }
    }
    public SaveManager(String pluginname, String fileName) {
        try {
            config = new YamlConfiguration();
            File dataStorageDIR = new File("plugins");
            if (!dataStorageDIR.exists())
            {
                dataStorageDIR.mkdir();
            }
            File TitanBoxDIR = new File("plugins" + File.separator + pluginname );
            if (!TitanBoxDIR.exists())
            {
                TitanBoxDIR.mkdir();
            }
            this.file = new File("plugins" + File.separator + pluginname + File.separator  + fileName + ".yml");
            if (!this.file.exists())
            {
                this.file.createNewFile();
            }
            config.load(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public SaveManager(File file) {
        try {
            this.file = file;
            config = new YamlConfiguration();
            config.load(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public SaveManager(String fileName) {
        try {
            config = new YamlConfiguration();
            File dataStorageDIR = new File("data-storage");
            if (!dataStorageDIR.exists())
            {
                dataStorageDIR.mkdir();
            }
            File TitanBoxDIR = new File("data-storage" + File.separator + "TitanBox" );
            if (!TitanBoxDIR.exists())
            {
                TitanBoxDIR.mkdir();
            }
            this.file = new File("data-storage" + File.separator + "TitanBox" + File.separator  + fileName + "_save.yml");
            if (!this.file.exists())
            {
                this.file.createNewFile();
            }
            config.load(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void convertToFile(File file)
    {
        try {
            this.file = file;
            if (!this.file.exists())
            {
                this.file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void convertToFile(String fileName)
    {
        try {
            File dataStorageDIR = new File("data-storage");
            if (!dataStorageDIR.exists())
            {
                dataStorageDIR.mkdir();
            }
            File TitanBoxDIR = new File("data-storage" + File.separator + "TitanBox" );
            if (!TitanBoxDIR.exists())
            {
                TitanBoxDIR.mkdir();
            }
            this.file = new File("data-storage" + File.separator + "TitanBox" + File.separator  + fileName + "_save.yml");
            if (!this.file.exists())
            {
                this.file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public SaveManager() {
        config = new YamlConfiguration();
        this.file = null;
    }
    private SaveManager(YamlConfiguration config) {
        if (config == null) return;
        this.config = config;
    }

    public void set(String key, Object object)
    {
        if (object == null) return;
        config.set(key, object);
    }
    public void set(String key, UUID uuid)
    {
        if (uuid == null) return;
        config.set(key, uuid.toString());
    }
    public void set(String key, List list)
    {
        set(key, list, false);
    }
    public void set(String key, List list, boolean asText)
    {
        if (list == null) return;
        if (asText)
        {
            config.set(key, list);
        }
        else {
            String encode = SaveManager.encode(list);
            config.set(key, encode);
        }

    }
    public void set(String key, Location location)
    {
        if (location == null) return;
        //String encode = SaveManager.encode(location);
        config.set(key, location);
    }
    public void set(String key, ItemStack itemStack)
    {
        if (itemStack == null) return;
        String encode = SaveManager.encode(itemStack);
        config.set(key, encode);
    }
    public void set(String key, SaveManager saveManager)
    {
        if (saveManager == null) return;
        String encode = SaveManager.encode(saveManager.getConfig());
        config.set(key, encode);
    }
    public void delete(String key)
    {
        config.set(key, null);
    }
    public boolean contains(String key)
    {
        return config.contains(key);
    }
    public Set<String> getKeys()
    {
        try {
            Set<String> keys = this.config.getKeys(false);
            return keys;
        } catch (Exception e) {
            Set<String> hash_Set = new HashSet<String>();
            return hash_Set;
        }
    }
    public Set<String> getKeys(String key)
    {
        try {
            Set<String> keys = this.config.getConfigurationSection(key).getKeys(false);
            return keys;
        } catch (Exception e) {
            Set<String> hash_Set = new HashSet<String>();
            return hash_Set;
        }
    }
    private YamlConfiguration getConfig() {
        return config;
    }
    public SaveManager getSaveManager(String key) {
        if (!config.contains(key)) return new SaveManager();
        YamlConfiguration configuration = SaveManager.decodeYaml(config.getString(key));
        if (configuration == null) return new SaveManager();
        SaveManager saveManager = new SaveManager(configuration);
        return saveManager;
    }


    public void save()
    {
        try {
            this.file.delete();
            this.file.createNewFile();
            config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ItemStack getItem(String path) {
        try {
            if (config.contains(path)){
                    if (config.getString(path) != null) {
                        return SaveManager.decodeItemStack(config.getString(path));
                    }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
    public List<Location> getLocationList(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return SaveManager.decodeLocationList(config.getString(path));
                }
            }
            return new ArrayList<Location>();
        } catch (Exception e) {
            return new ArrayList<Location>();
        }

    }
    public List<EntityType> getEntityTypeList(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return SaveManager.decodeEntityTypeList(config.getString(path));
                }
            }
            return new ArrayList<EntityType>();
        } catch (Exception e) {
            return new ArrayList<EntityType>();
        }

    }
    public List<UUID> getUUIDList(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return SaveManager.decodeUUIDList(config.getString(path));
                }
            }
            return new ArrayList<UUID>();
        } catch (Exception e) {
            return new ArrayList<UUID>();
        }

    }
    public List<String> getStringList(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return SaveManager.decodeStringList(config.getString(path));
                }
            }
            return new ArrayList<String>();
        } catch (Exception e) {
            return new ArrayList<String>();
        }

    }
    public List<String> getStringListFromText(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return (ArrayList<String>)config.getList(path);
                }
            }
            return new ArrayList<String>();
        } catch (Exception e) {
            return new ArrayList<String>();
        }

    }
    public List<ItemStack> getItemList(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return SaveManager.decodeItemList(config.getString(path));
                }
            }
            return new ArrayList<ItemStack>();
        } catch (Exception e) {
            return new ArrayList<ItemStack>();
        }

    }

    public List<Integer> getIntList(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return SaveManager.decodeIntList(config.getString(path));
                }
            }
            return new ArrayList<Integer>();
        } catch (Exception e) {
            return new ArrayList<Integer>();
        }


    }

    public Long getLong(String path) {
        return config.getLong(path);
    }

    public UUID getUUID(String path) {
        try {
            if (config.contains(path)){
                if (config.getString(path) != null) {
                    return UUID.fromString(config.getString(path));
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Double getDouble(String path) {
        return config.getDouble(path);
    }

    public Location getLocation(String path) {
        return config.getLocation(path);
    }


}
