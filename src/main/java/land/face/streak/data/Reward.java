package land.face.streak.data;

import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.List;
import land.face.streak.managers.RewardManager.Rarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Reward {

  private final String id;
  private String command;
  private Rarity rarity;
  private final ItemStack stack;

  public Reward(String id, Material material, String name, List<String> lore, int data) {
    this.id = id;
    stack = new ItemStack(material);
    ItemStackExtensionsKt.setDisplayName(stack, name);
    TextUtils.setLore(stack, lore);
    if (data != 0) {
      ItemStackExtensionsKt.setCustomModelData(stack, data);
    }
    stack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
  }

  public String getId() {
    return id;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Rarity getRarity() {
    return rarity;
  }

  public void setRarity(Rarity rarity) {
    this.rarity = rarity;
  }

  public ItemStack getStack() {
    return stack;
  }
}
