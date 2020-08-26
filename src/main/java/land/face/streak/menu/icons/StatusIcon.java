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

import com.tealcube.minecraft.bukkit.TextUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.streak.StreakPlugin;
import land.face.streak.data.Reward;
import land.face.streak.managers.RewardManager.Rarity;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatusIcon extends MenuItem {

  private ItemStack icon;

  public StatusIcon(StreakPlugin plugin) {
    super("", new ItemStack(Material.PAPER));

    List<Reward> rewards = new ArrayList<>();

    for (String id : plugin.getRewardManager().getCurrentRewards().values()) {
      rewards.add(plugin.getRewardManager().getRewardSlotMap().get(id));
    }

    icon = new ItemStack(Material.PAPER);
    ItemStackExtensionsKt.setDisplayName(icon, TextUtils.color("&6&l== &e&lThis Week's Rewards! &6&l=="));
    List<String> lore = new ArrayList<>();
    lore.add("");
    lore.add("&c&lEpic Rewards:");
    for (Reward reward : rewards) {
      if (reward.getRarity() == Rarity.EPIC) {
        lore.add(" &7- " + ChatColor
            .stripColor(ItemStackExtensionsKt.getDisplayName(reward.getStack())));
      }
    }
    lore.add("&5&lRare Rewards:");
    for (Reward reward : rewards) {
      if (reward.getRarity() == Rarity.RARE) {
        lore.add(" &7- " + ChatColor
            .stripColor(ItemStackExtensionsKt.getDisplayName(reward.getStack())));
      }
    }
    lore.add("&9&lUncommon Rewards:");
    for (Reward reward : rewards) {
      if (reward.getRarity() == Rarity.UNCOMMON) {
        lore.add(" &7- " + ChatColor
            .stripColor(ItemStackExtensionsKt.getDisplayName(reward.getStack())));
      }
    }
    lore.add("");
    lore.add("&7Rewards refresh every week");
    lore.add("&bFriday &7at &b12:00AM EST&7!");
    lore.add("");
    ItemStackExtensionsKt.setLore(icon, TextUtils.color(lore));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    return icon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
  }
}
