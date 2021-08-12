package land.face.streak.managers;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import land.face.streak.StreakPlugin;
import land.face.streak.data.PlayerData;
import land.face.streak.data.PlayerData.SlotId;
import land.face.streak.data.RarityComparator;
import land.face.streak.data.Reward;
import land.face.streak.events.ClaimEvent;
import land.face.streak.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StreakManager {

  private StreakPlugin plugin;
  private TextChannel streakChannel;
  private RarityComparator rarityComparator;

  private Map<UUID, PlayerData> dataMap = new HashMap<>();

  public static final SlotId[] SLOTS = SlotId.values();

  public StreakManager(StreakPlugin plugin) {
    this.plugin = plugin;
    rarityComparator = new RarityComparator();
    String channelId = plugin.getSettings().getString("config.rewards-channel-id");
    if (StringUtils.isNotBlank(channelId)) {
      setDiscordChannel(channelId);
    } else {
      Bukkit.getLogger().info("Config option 'rewards-channel-id' not set - No discord");
    }
  }

  private void setDiscordChannel(String channelId) {
    streakChannel = DiscordUtil.getTextChannelById(channelId);
    if (streakChannel == null) {
      Bukkit.getScheduler().runTaskLater(plugin, () -> setDiscordChannel(channelId), 600L);
      Bukkit.getLogger().warning("Streak channel is null! Rotation messages won't be sent.");
    } else {
      Bukkit.getLogger().info("Successfully set discord channel for rotation messages!");
    }
  }

  public Collection<PlayerData> getPlayerData() {
    return dataMap.values();
  }

  public void importData(List<PlayerData> data) {
    for (PlayerData d : data) {
      dataMap.put(d.getUuid(), d);
    }
  }

  public PlayerData getPlayerData(Player player) {
    return dataMap.get(player.getUniqueId());
  }

  public PlayerData doLogin(Player player) {
    dataMap.putIfAbsent(player.getUniqueId(), new PlayerData(player.getUniqueId()));
    PlayerData data = dataMap.get(player.getUniqueId());
    DayOfWeek dow = LocalDate.now().getDayOfWeek();
    if (!data.getLoginDays().contains(dow)) {
      data.getLoginDays().add(dow);
      data.setPoints(data.getPoints() + 1);
    }
    return data;
  }

  public void startNewWeek() {
    dataMap.clear();
    plugin.getRewardManager().generateNewRewards();
    List<Reward> rewards = new ArrayList<>();
    for (String s : plugin.getRewardManager().getCurrentRewards().values()) {
      rewards.add(plugin.getRewardManager().getRewardSlotMap().get(s));
    }
    rewards.sort(rarityComparator);

    String preamble = ":cowboy: Howdy gamers! The weekly reward rotation is here! If you log in every day this week, you can collect 7 of these snazzy rewards, one per day! Good luck!";
    String epicMsg =
        "***EPIC REWARD***\n"
            + ItemStackExtensionsKt.getDisplayName(rewards.get(0).getStack());
    String rareMsg =
        "***RARE REWARDS***\n"
            + ItemStackExtensionsKt.getDisplayName(rewards.get(1).getStack())
            + "\n" + ItemStackExtensionsKt.getDisplayName(rewards.get(2).getStack())
            + "\n" + ItemStackExtensionsKt.getDisplayName(rewards.get(3).getStack());
    String uncommonMsg =
        "***UNCOMMON REWARDS***\n"
            + ItemStackExtensionsKt.getDisplayName(rewards.get(4).getStack())
            + "\n" + ItemStackExtensionsKt.getDisplayName(rewards.get(5).getStack())
            + "\n" + ItemStackExtensionsKt.getDisplayName(rewards.get(6).getStack())
            + "\n" + ItemStackExtensionsKt.getDisplayName(rewards.get(7).getStack())
            + "\n" + ItemStackExtensionsKt.getDisplayName(rewards.get(8).getStack());

    if (streakChannel != null) {
      DiscordUtil.sendMessage(streakChannel, preamble);
      DiscordUtil.sendMessage(streakChannel, epicMsg);
      DiscordUtil.sendMessage(streakChannel, rareMsg);
      DiscordUtil.sendMessage(streakChannel, uncommonMsg);
    } else {
      Bukkit.getLogger().warning("Streak channel is null! No weekly rewards message sent!");
    }
  }

  public boolean pickSlot(Player player, SlotId slotId) {
    if (InventoryUtil.isInventoryFull(player)) {
      MessageUtils.sendMessage(player, "&eYou don't have enough inventory space to do this!");
      return false;
    }
    PlayerData data = dataMap.get(player.getUniqueId());
    if (data == null || data.getPoints() == 0) {
      return false;
    }
    if (data.getPickedSlots().containsKey(slotId)) {
      return false;
    }
    SlotId rewardId = pickRewardSlot(data);

    ClaimEvent claimEvent = new ClaimEvent(player, rewardId);
    plugin.getServer().getPluginManager().callEvent(claimEvent);

    if (claimEvent.isCancelled()) {
      return false;
    }

    data.getPickedSlots().put(slotId, rewardId);
    data.setPoints(data.getPoints() - 1);

    plugin.getRewardManager().giveReward(player, rewardId);
    return true;
  }

  private SlotId pickRewardSlot(PlayerData data) {
    List<SlotId> outcomes = new ArrayList<>(Arrays.asList(SLOTS));
    outcomes.removeAll(data.getPickedSlots().values());
    if (outcomes.size() == 0) {
      Bukkit.getLogger().severe("Bad reward data for: " + data.getUuid());
      throw new IllegalStateException("No reward outcomes exist!");
    }
    Collections.shuffle(outcomes);
    return outcomes.get(0);
  }
}
