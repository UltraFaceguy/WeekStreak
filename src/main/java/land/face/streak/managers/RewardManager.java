package land.face.streak.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import land.face.streak.StreakPlugin;
import land.face.streak.data.PlayerData.SlotId;
import land.face.streak.data.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardManager {

  private StreakPlugin plugin;

  private Map<String, Reward> rewardSlotMap = new HashMap<>();
  private Map<SlotId, String> currentRewards = new HashMap<>();

  public RewardManager(StreakPlugin plugin) {
    this.plugin = plugin;
  }

  public void generateNewRewards() {
    List<Reward> uncommon = new ArrayList<>();
    List<Reward> rare = new ArrayList<>();
    List<Reward> epic = new ArrayList<>();
    for (Reward reward : rewardSlotMap.values()) {
      if (reward.getRarity() == Rarity.EPIC) {
        epic.add(reward);
      } else if (reward.getRarity() == Rarity.RARE) {
        rare.add(reward);
      } else {
        uncommon.add(reward);
      }
    }

    Collections.shuffle(uncommon);
    Collections.shuffle(rare);
    Collections.shuffle(epic);

    List<Reward> rewards = new ArrayList<>();

    rewards.add(uncommon.get(0));
    rewards.add(uncommon.get(1));
    rewards.add(uncommon.get(2));
    rewards.add(uncommon.get(3));
    rewards.add(uncommon.get(4));
    rewards.add(rare.get(0));
    rewards.add(rare.get(1));
    rewards.add(rare.get(2));
    rewards.add(epic.get(0));

    Collections.shuffle(rewards);

    for (SlotId slotId : StreakManager.SLOTS) {
      currentRewards.put(slotId, rewards.get(slotId.ordinal()).getId());
    }
  }

  public Reward getCurrentReward(SlotId slotId) {
    String id = currentRewards.get(slotId);
    return rewardSlotMap.get(id);
  }

  public ItemStack getRewardIcon(String rewardId) {
    return rewardSlotMap.get(rewardId).getStack().clone();
  }

  public ItemStack getRewardIcon(SlotId slotId) {
    return getRewardIcon(currentRewards.get(slotId));
  }

  public void giveReward(Player player, SlotId rewardId) {
    String id = currentRewards.get(rewardId);
    Reward reward = rewardSlotMap.get(id);
    executeRewardCommands(player, reward);
  }

  private void executeRewardCommands(Player player, Reward reward) {
    String command = reward.getCommand().replace("@p", player.getName());
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
  }

  public Map<String, Reward> getRewardSlotMap() {
    return rewardSlotMap;
  }

  public Map<SlotId, String> getCurrentRewards() {
    return currentRewards;
  }

  public enum Rarity {
    UNCOMMON,
    RARE,
    EPIC
  }
}
