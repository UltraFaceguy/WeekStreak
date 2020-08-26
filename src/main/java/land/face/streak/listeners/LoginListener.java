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
package land.face.streak.listeners;

import static com.sk89q.worldedit.math.BlockVector3.at;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import java.util.List;
import land.face.streak.StreakPlugin;
import land.face.streak.data.PlayerData;
import land.face.streak.menu.RewardMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public final class LoginListener implements Listener {

  private StreakPlugin plugin;
  private List<String> safeRegions;

  private RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
  private StringMatcher stringMatcher = WorldGuard.getInstance().getPlatform().getMatcher();


  public LoginListener(StreakPlugin plugin) {
    this.plugin = plugin;
    safeRegions = plugin.getSettings().getStringList("config.safe-regions");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLogin(PlayerLoginEvent event) {
    if (event.getResult() == Result.ALLOWED) {
      PlayerData data = plugin.getStreakManager().doLogin(event.getPlayer());
      if (data.getPoints() > 0) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
            sendLoginMenu(event.getPlayer(), data), 5L);
      }
      Bukkit.getScheduler().runTaskLater(plugin, () ->
          sendSpam(event.getPlayer(), data), 300L);
    }
  }

  private void sendSpam(Player player, PlayerData data) {
    if (!player.isOnline()) {
      return;
    }
    if (data.getPoints() < 1) {
      return;
    }
    MessageUtils.sendMessage(player, "&b&lYou have &f&l" + data.getPoints()
        + " &b&lunclaimed login reward(s)! Use &f&l/daily &b&lfor your free stuff!");
    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 0.8f);
  }

  private void sendLoginMenu(Player player, PlayerData data) {
    if (!isInSafeRegion(player.getLocation()) || data.getPoints() < 1) {
      return;
    }
    RewardMenu.getInstance().open(player);
  }

  private boolean isInSafeRegion(Location loc) {
    BlockVector3 vectorLoc = at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    World world = stringMatcher.getWorldByName(loc.getWorld().getName());
    if (world == null) {
      return false;
    }
    RegionManager manager = regionContainer.get(world);
    if (manager == null) {
      return false;
    }
    ApplicableRegionSet regions = manager.getApplicableRegions(vectorLoc);
    for (ProtectedRegion region : regions.getRegions()) {
      if (safeRegions.contains(region.getId())) {
        return true;
      }
    }
    return false;
  }
}
