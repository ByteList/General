package de.gamechest.database.bug;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteList on 25.04.2017.
 */
public enum BugReason {

    ANTI_CHEAT("AntiCheat"),
    LOBBY("Lobby"),
    CLICKATTACK("ClickAttack"),
    SHULKERDEFENCE("ShulkerDefence"),
    JUMPDUELL("JumpDuell"),
    DEATHRUN("DeathRun"),
    SURVIVAL("Survival"),
    TS_VERIFY("TsVerify"),
    MAP_BUG("MapBug"),
    REPLAY("Replay");

    @Getter
    private String betterReason;

    BugReason(String betterReason) {
        this.betterReason = betterReason;
    }

    public static List<String> getBetterBugReasonsAsString() {
        java.util.List<String> units = new ArrayList<>();
        for (BugReason unit : values()) {
            units.add(unit.getBetterReason());
        }
        return units;
    }

    public static BugReason getBugReason(String betterReason) {
        for (BugReason units : values()) {
            String br = units.getBetterReason();
            if (br.toUpperCase().equals(betterReason.toUpperCase())) {
                return units;
            }
        }
        throw new IllegalArgumentException(betterReason + " is not a better bug reason!");
    }
}
