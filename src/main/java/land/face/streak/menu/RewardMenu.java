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
package land.face.streak.menu;

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.streak.StreakPlugin;
import land.face.streak.data.PlayerData.SlotId;
import land.face.streak.menu.icons.KeysIcon;
import land.face.streak.menu.icons.RewardIcon;
import land.face.streak.menu.icons.StatusIcon;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class RewardMenu extends ItemMenu {

  private static RewardMenu instance;
  private static ItemStack noKeyStack;
  private static ItemStack claimStack;
  private static ItemStack claimedStack;

  public RewardMenu(StreakPlugin plugin) {
    super(ChatColor.BOLD + "Daily Reward!", Size.fit(45), plugin);

    setItem(12, new RewardIcon(plugin, SlotId.SLOT_ONE));
    setItem(13, new RewardIcon(plugin, SlotId.SLOT_TWO));
    setItem(14, new RewardIcon(plugin, SlotId.SLOT_THREE));
    setItem(21, new RewardIcon(plugin, SlotId.SLOT_FOUR));
    setItem(22, new RewardIcon(plugin, SlotId.SLOT_FIVE));
    setItem(23, new RewardIcon(plugin, SlotId.SLOT_SIX));
    setItem(30, new RewardIcon(plugin, SlotId.SLOT_SEVEN));
    setItem(31, new RewardIcon(plugin, SlotId.SLOT_EIGHT));
    setItem(32, new RewardIcon(plugin, SlotId.SLOT_NINE));

    setItem(19, new StatusIcon(plugin));
    setItem(25, new KeysIcon(plugin));

    fillEmptySlots(new BlankIcon());

    claimStack = new ItemStack(Material.CHEST_MINECART);
    ItemStackExtensionsKt.setDisplayName(claimStack, TextUtils.color("&a&lClick To Open!"));
    List<String> lore = new ArrayList<>();
    lore.add("&7What could it be? Click to");
    lore.add("&7get your daily reward!");
    ItemStackExtensionsKt.setCustomModelData(claimStack, 50);
    claimStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    TextUtils.setLore(claimStack, lore, true);
    claimStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);

    noKeyStack = new ItemStack(Material.CHEST_MINECART);
    ItemStackExtensionsKt.setDisplayName(noKeyStack, TextUtils.color("&e&lNo Rewards Left!"));
    List<String> lore3 = new ArrayList<>();
    lore3.add("&7Alas, you'll have to come");
    lore3.add("&7back tomorrow to get your");
    lore3.add("&7next reward.");
    ItemStackExtensionsKt.setCustomModelData(noKeyStack, 50);
    TextUtils.setLore(noKeyStack, lore3, true);

    claimedStack = new ItemStack(Material.MINECART);
    ItemStackExtensionsKt.setDisplayName(claimedStack, TextUtils.color("&e&lAlready Opened!"));
    List<String> lore2 = new ArrayList<>();
    lore2.add("&7You claimed this reward");
    lore2.add("&7already! Nice!");
    ItemStackExtensionsKt.setCustomModelData(claimedStack, 50);
    TextUtils.setLore(claimedStack, lore2, true);
  }

  public static RewardMenu getInstance() {
    return instance;
  }

  public static void setInstance(RewardMenu menu) {
    instance = menu;
  }

  public static ItemStack getClaimStack() {
    return claimStack;
  }

  public static ItemStack getNoKeyStack() {
    return noKeyStack;
  }

  public static ItemStack getClaimedStack() {
    return claimedStack;
  }

}

/*
00 01 02 03 04 05 06 07 08
09 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
*/
