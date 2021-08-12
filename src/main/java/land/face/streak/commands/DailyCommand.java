package land.face.streak.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandCompletion;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandPermission;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Default;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import com.tealcube.minecraft.bukkit.shade.acf.bukkit.contexts.OnlinePlayer;
import land.face.streak.StreakPlugin;
import land.face.streak.menu.RewardMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("daily")
public class DailyCommand extends BaseCommand {

  private final StreakPlugin plugin;

  public DailyCommand(StreakPlugin plugin) {
    this.plugin = plugin;
  }

  @Default
  public void baseCommand(OnlinePlayer sender) {
    plugin.getStreakManager().doLogin((Player) sender);
    RewardMenu.getInstance().open((Player) sender);
  }

  @Subcommand("reload")
  @CommandPermission("streak.start")
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
    MessageUtils.sendMessage(sender, "&aWeekStreak reloaded");
  }

  @Subcommand("save")
  @CommandPermission("streak.save")
  public void saveCommand(CommandSender sender) {
    plugin.saveData(false);
    MessageUtils.sendMessage(sender, "&aSaved streak data");
  }

  @Subcommand("setKeys")
  @CommandCompletion("@players")
  @CommandPermission("streak.keys")
  public void saveCommand(CommandSender sender, OnlinePlayer player, int amount) {
    plugin.getStreakManager().getPlayerData(player.getPlayer()).setPoints(amount);
    MessageUtils.sendMessage(sender, "&aSet target's keys to " + amount);
  }

  @Subcommand("reset|reroll")
  @CommandPermission("streak.reset")
  public void resetCommand(CommandSender sender) {
    plugin.getStreakManager().startNewWeek();
    RewardMenu.setInstance(new RewardMenu(plugin));
    for (Player p : Bukkit.getOnlinePlayers()) {
      plugin.getStreakManager().doLogin(p);
    }
    MessageUtils.sendMessage(sender, "ReRoll'd WeekStreak!");
  }
}
