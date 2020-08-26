package land.face.streak.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import land.face.streak.StreakPlugin;
import land.face.streak.data.PlayerData;
import land.face.streak.menu.RewardMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class BaseCommand {

  private StreakPlugin plugin;

  public BaseCommand(StreakPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(identifier = "daily reload", permissions = "streak.reload", onlyPlayers = false)
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
  }

  @Command(identifier = "daily", permissions = "streak.open", onlyPlayers = false)
  public void baseCommand(CommandSender sender) {
    if (sender instanceof Player) {
      plugin.getStreakManager().doLogin((Player) sender);
      RewardMenu.getInstance().open((Player) sender);
      return;
    }
    MessageUtils.sendMessage(sender, "Sorry, base command is players only");
  }

  @Command(identifier = "daily save", permissions = "streak.save", onlyPlayers = false)
  public void saveCommand(CommandSender sender) {
    plugin.saveData(false);
  }

  @Command(identifier = "daily setkeys", permissions = "streak.save", onlyPlayers = false)
  public void saveCommand(CommandSender sender, @Arg(name = "player") Player player, @Arg(name = "amount") int amount) {
    plugin.getStreakManager().getPlayerData(player).setPoints(amount);
  }

  @Command(identifier = "daily reset", permissions = "streak.reset", onlyPlayers = false)
  public void resetCommand(CommandSender sender) {
    plugin.getStreakManager().startNewWeek();
    RewardMenu.setInstance(new RewardMenu(plugin));
    for (Player p : Bukkit.getOnlinePlayers()) {
      plugin.getStreakManager().doLogin(p);
    }
  }
}
