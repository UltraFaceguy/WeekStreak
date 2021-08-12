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

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.streak.StreakPlugin;
import land.face.streak.menu.BlankIcon;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KeysIcon extends MenuItem {

  private final StreakPlugin plugin;
  private final ItemStack icon;

  public KeysIcon(StreakPlugin plugin) {
    super("", new ItemStack(Material.CHEST_MINECART));
    this.plugin = plugin;
    icon = new ItemStack(Material.TRIPWIRE_HOOK);
    List<String> lore = new ArrayList<>();
    lore.add("&7Each day you login during");
    lore.add("&7the week will get you a");
    lore.add("&areward&7! You can collect");
    lore.add("&7up to &f7 &7prizes a week!");
    ItemStackExtensionsKt.setCustomModelData(icon, 50);
    TextUtils.setLore(icon, lore, true);
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    int keys = plugin.getStreakManager().getPlayerData(player).getPoints();
    if (keys == 0) {
      return BlankIcon.getBlankStack();
    }
    ItemStack newIcon = icon.clone();
    ItemStackExtensionsKt.setDisplayName(newIcon, TextUtils.color("&b&l" + keys + " Rewards Remaining!"));
    newIcon.setAmount(keys);
    return newIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
  }
}
