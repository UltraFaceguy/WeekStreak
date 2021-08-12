package land.face.streak;

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.shade.acf.PaperCommandManager;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.SmartYamlConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import land.face.streak.commands.DailyCommand;
import land.face.streak.data.PlayerData;
import land.face.streak.data.PlayerData.SlotId;
import land.face.streak.data.Reward;
import land.face.streak.listeners.LoginListener;
import land.face.streak.managers.RewardManager;
import land.face.streak.managers.RewardManager.Rarity;
import land.face.streak.managers.StreakManager;
import land.face.streak.menu.RewardMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class StreakPlugin extends JavaPlugin {

  private static StreakPlugin instance;
  public static final DecimalFormat INT_FORMAT = new DecimalFormat("#,###,###,###,###");
  public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

  private StreakManager streakManager;
  private RewardManager rewardManager;

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;

  private SmartYamlConfiguration playerData;
  private SmartYamlConfiguration rewardsYml;

  public static StreakPlugin getInstance() {
    return instance;
  }

  public StreakPlugin() {
    instance = this;
  }

  public void onEnable() {
    List<VersionedSmartYamlConfiguration> configurations = new ArrayList<>();
    configurations.add(configYAML = defaultSettingsLoad("config.yml"));

    for (VersionedSmartYamlConfiguration config : configurations) {
      if (config.update()) {
        getLogger().info("Updating " + config.getFileName());
      }
    }

    settings = MasterConfiguration.loadFromFiles(configYAML);

    streakManager = new StreakManager(this);
    rewardManager = new RewardManager();

    Bukkit.getPluginManager().registerEvents(new LoginListener(this), this);

    //Bukkit.getScheduler().runTaskTimer(this,
    //    () -> streakManager.expireOldListings(),
    //    60L * 5, // Start save after 60s
    //    1120 * 20L // Run every 2 minutes
    //);

    PaperCommandManager commandManager = new PaperCommandManager(this);
    commandManager.registerCommand(new DailyCommand(this));

    loadData();
    loadRewards();

    RewardMenu.setInstance(new RewardMenu(this));

    Bukkit.getServer().getLogger().info("WeekStreak Enabled!");
  }

  public void onDisable() {
    saveData(false);
    HandlerList.unregisterAll(this);
    Bukkit.getServer().getScheduler().cancelTasks(this);
    Bukkit.getServer().getLogger().info("WeekStreak Disabled!");
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public VersionedSmartYamlConfiguration getConfiguration() {
    return configYAML;
  }

  public StreakManager getStreakManager() {
    return streakManager;
  }

  public RewardManager getRewardManager() {
    return rewardManager;
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }

  private void loadRewards() {
    rewardsYml = new SmartYamlConfiguration(new File(getDataFolder(), "rewards.yml"));
    if (!rewardsYml.getFile().exists()) {
      try {
        rewardsYml.getFile().createNewFile();
        rewardsYml.createSection("rewards");
      } catch (IOException ignored) {

      }
    }
    rewardsYml.load();

    List<Reward> rewards = new ArrayList<>();
    ConfigurationSection rewardsData = rewardsYml.getConfigurationSection("rewards");
    for (String rewardId : rewardsData.getKeys(false)) {
      ConfigurationSection rewardSection = rewardsData.getConfigurationSection(rewardId);
      Material material = Material.valueOf(rewardSection.getString("material"));
      Rarity rarity = Rarity.valueOf(rewardSection.getString("rarity", "UNCOMMON"));
      String name = TextUtils.color(rewardSection.getString("name", ""));
      List<String> lore = TextUtils.color(rewardSection.getStringList("lore"));
      int customData = rewardSection.getInt("custom-data", 0);
      String command = rewardSection.getString("command", "");
      Reward reward = new Reward(rewardId, material, name, lore, customData);
      reward.setRarity(rarity);
      reward.setCommand(command);
      rewards.add(reward);
    }
    rewardManager.getRewardSlotMap().clear();
    for (Reward r : rewards) {
      rewardManager.getRewardSlotMap().put(r.getId(), r);
    }
  }

  private void loadData() {
    playerData = new SmartYamlConfiguration(new File(getDataFolder(), "data.yml"));
    if (!playerData.getFile().exists()) {
      try {
        playerData.getFile().createNewFile();
        playerData.createSection("current-rewards");
      } catch (IOException ignored) {

      }
    }
    playerData.load();

    List<PlayerData> loadedData = new ArrayList<>();
    ConfigurationSection dataSection = playerData.getConfigurationSection("data");
    if (dataSection == null) {
      dataSection = playerData.createSection("data");
    }
    for (String playerId : dataSection.getKeys(false)) {

      ConfigurationSection playerSection = dataSection.getConfigurationSection(playerId);

      PlayerData data = new PlayerData(UUID.fromString(playerId));
      for (String day : playerSection.getStringList("login-days")) {
        data.getLoginDays().add(DayOfWeek.valueOf(day));
      }
      data.setPoints(playerSection.getInt("points", 0));

      ConfigurationSection rewardSection = playerSection.getConfigurationSection("picked-rewards");
      for (String pickedSlotId : rewardSection.getKeys(false)) {
        SlotId pickedSlot = SlotId.valueOf(pickedSlotId);
        SlotId rewardSlot = SlotId.valueOf(rewardSection.getString(pickedSlotId));
        data.getPickedSlots().put(pickedSlot, rewardSlot);
      }
      loadedData.add(data);
    }

    ConfigurationSection currentRewards = playerData.getConfigurationSection("current-rewards");
    if (currentRewards == null) {
      currentRewards = playerData.createSection("current-rewards");
    }
    for (SlotId slot : StreakManager.SLOTS) {
      rewardManager.getCurrentRewards().put(slot, currentRewards.getString(slot.toString(), ""));
    }
    streakManager.importData(loadedData);
  }

  public void saveData(boolean async) {
    final ConfigurationSection dataSection = playerData.getConfigurationSection("data");
    for (String listingId : dataSection.getKeys(false)) {
      dataSection.set(listingId, null);
    }
    for (PlayerData pd : streakManager.getPlayerData()) {
      ConfigurationSection listingsSection = dataSection.createSection(pd.getUuid().toString());
      List<String> loginDays = new ArrayList<>();
      for (DayOfWeek s : pd.getLoginDays()) {
        loginDays.add(s.toString());
      }
      listingsSection.set("login-days", loginDays);
      listingsSection.set("points", pd.getPoints());
      Map<String, String> rewardMap = new HashMap<>();
      for (SlotId slotId : pd.getPickedSlots().keySet()) {
        rewardMap.put(slotId.toString(), pd.getPickedSlots().get(slotId).toString());
      }
      listingsSection.set("picked-rewards", rewardMap);
    }
    final ConfigurationSection rewardsSection = playerData.getConfigurationSection("");
    for (SlotId slot : StreakManager.SLOTS) {
      rewardsSection.set("current-rewards." + slot.toString(), rewardManager.getCurrentRewards().get(slot));
    }
    if (async) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(this, this::saveYml, 20L);
    } else {
      saveYml();
    }
  }

  private synchronized void saveYml() {
    long time = System.currentTimeMillis();
    System.out.println("Saving streak data...");
    playerData.save();
    System.out.println("Saved async in " + (System.currentTimeMillis() - time) + "ms");
  }
}