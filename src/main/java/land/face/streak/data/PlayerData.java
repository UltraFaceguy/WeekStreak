package land.face.streak.data;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

  private final UUID uuid;
  private int points;
  private Set<DayOfWeek> loginDays;
  private Map<SlotId, SlotId> pickedSlots;

  public PlayerData(UUID uuid) {
    this.uuid = uuid;
    loginDays = new HashSet<>();
    points = 0;
    pickedSlots = new HashMap<>();
  }

  public UUID getUuid() {
    return uuid;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public Set<DayOfWeek> getLoginDays() {
    return loginDays;
  }

  public Map<SlotId, SlotId> getPickedSlots() {
    return pickedSlots;
  }

  public void setPickedSlots(Map<SlotId, SlotId> pickedSlots) {
    this.pickedSlots = pickedSlots;
  }

  public enum SlotId {
    SLOT_ONE,
    SLOT_TWO,
    SLOT_THREE,
    SLOT_FOUR,
    SLOT_FIVE,
    SLOT_SIX,
    SLOT_SEVEN,
    SLOT_EIGHT,
    SLOT_NINE
  }
}
