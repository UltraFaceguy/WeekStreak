/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package land.face.streak.menu.icons;

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import land.face.streak.StreakPlugin;
import land.face.streak.data.PlayerData;
import land.face.streak.data.PlayerData.SlotId;
import land.face.streak.data.Reward;
import land.face.streak.managers.RewardManager.Rarity;
import land.face.streak.menu.RewardMenu;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardIcon extends MenuItem {

  private StreakPlugin plugin;
  private SlotId slotId;
  private Map<UUID, ItemStack> tempStack = new HashMap<>();

  public RewardIcon(StreakPlugin plugin, SlotId slotId) {
    super("", new ItemStack(Material.CHEST_MINECART));
    this.plugin = plugin;
    this.slotId = slotId;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    if (tempStack.containsKey(player.getUniqueId())) {
      ItemStack icon = tempStack.get(player.getUniqueId());
      tempStack.remove(player.getUniqueId());
      return icon;
    }
    PlayerData data = plugin.getStreakManager().getPlayerData(player);
    if (data.getPickedSlots().containsKey(slotId)) {
      Reward reward = plugin.getRewardManager().getCurrentReward(data.getPickedSlots().get(slotId));
      ItemStack icon = RewardMenu.getClaimedStack().clone();
      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.WHITE + "Reward: " +
          ItemStackExtensionsKt.getDisplayName(reward.getStack()));
      lore.add(ChatColor.WHITE + "Reward Rarity: " +
          colorFromRarity(reward.getRarity()) + reward.getRarity());
      lore.add("");
      lore.addAll(ItemStackExtensionsKt.getLore(icon));
      ItemStackExtensionsKt.setLore(icon, lore);
      return icon;
    } else if (data.getPoints() > 0) {
      return RewardMenu.getClaimStack();
    } else {
      return RewardMenu.getNoKeyStack();
    }
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
    PlayerData data = plugin.getStreakManager().getPlayerData(event.getPlayer());
    if (data.getPickedSlots().containsKey(slotId)) {
      return;
    }
    boolean success = plugin.getStreakManager().pickSlot(event.getPlayer(), slotId);
    if (success) {
      event.getPlayer().playSound(event.getPlayer().getLocation(),
          Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
      SlotId rewardId = data.getPickedSlots().get(slotId);
      Reward reward = plugin.getRewardManager().getCurrentReward(rewardId);

      ItemStack rewardIcon = reward.getStack().clone();

      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(ChatColor.WHITE + "Reward Rarity: " +
          colorFromRarity(reward.getRarity()) + reward.getRarity());
      lore.add("");
      lore.addAll(ItemStackExtensionsKt.getLore(rewardIcon));
      ItemStackExtensionsKt.setLore(rewardIcon, lore);

      tempStack.put(event.getPlayer().getUniqueId(), rewardIcon);

      if (reward.getRarity() == Rarity.RARE) {
        event.getPlayer().playSound(event.getPlayer().getLocation(),
            Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
      } else if (reward.getRarity() == Rarity.EPIC) {
        event.getPlayer().playSound(event.getPlayer().getLocation(),
            Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
      }

      event.setWillUpdate(true);
    }
  }

  private static ChatColor colorFromRarity(Rarity rarity) {
    switch (rarity) {
      case RARE:
        return ChatColor.DARK_PURPLE;
      case UNCOMMON:
        return ChatColor.BLUE;
      case EPIC:
        return ChatColor.RED;
    }
    return ChatColor.WHITE;
  }

}
