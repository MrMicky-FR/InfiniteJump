package fr.mrmicky.infinitejump.anticheathooks;

import fr.mrmicky.infinitejump.InfiniteJump;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import org.bukkit.entity.Player;

/**
 * @author MrMicky
 */
public class NoCheatPlusHook implements NCPHook {

    private final InfiniteJump plugin;

    public NoCheatPlusHook(InfiniteJump plugin) {
        this.plugin = plugin;

        NCPHookManager.addHook(CheckType.MOVING_SURVIVALFLY, this);
        plugin.getLogger().info("NoCheatPlus hook enabled");
    }

    @Override
    public String getHookName() {
        return plugin.getName();
    }

    @Override
    public String getHookVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean onCheckFailure(CheckType check, Player p, IViolationInfo vi) {
        return plugin.getJumpManager().isActive(p);
    }
}
