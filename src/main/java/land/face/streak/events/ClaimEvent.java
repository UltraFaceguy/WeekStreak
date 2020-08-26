package land.face.streak.events;

import land.face.streak.data.PlayerData.SlotId;
import org.bukkit.entity.Player;

public class ClaimEvent extends StreakCancellableEvent {

  private final Player player;
  private final SlotId rewardId;

  public ClaimEvent(Player player, SlotId rewardId) {
    this.player = player;
    this.rewardId = rewardId;
  }

  public Player getPlayer() {
    return player;
  }

  public SlotId getRewardId() {
    return rewardId;
  }
}