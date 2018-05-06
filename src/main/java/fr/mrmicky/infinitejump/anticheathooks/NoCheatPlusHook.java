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

    private InfiniteJump m;

    public NoCheatPlusHook(InfiniteJump m) {
        this.m = m;
        NCPHookManager.addHook(CheckType.MOVING_SURVIVALFLY, this);
        m.getLogger().info("NoCheatPlus hook enabled");
    }

    @Override
    public String getHookName() {
        return "InfiniteJump";
    }

    @Override
    public String getHookVersion() {
        return m.getDescription().getVersion();
    }

    @Override
    public boolean onCheckFailure(CheckType check, Player p, IViolationInfo vi) {
        return m.getJumps().contains(p.getUniqueId());
    }
}
