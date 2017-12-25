package de.gamechest.updater.updaters;

import de.gamechest.database.stats.clickattack.DatabaseClickAttack;
import de.gamechest.database.stats.clickattack.DatabaseClickAttackObject;
import de.gamechest.database.stats.deathrun.DatabaseDeathRun;
import de.gamechest.database.stats.deathrun.DatabaseDeathRunObject;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuell;
import de.gamechest.database.stats.jumpduell.DatabaseJumpDuellObject;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefence;
import de.gamechest.database.stats.shulkerdefence.DatabaseShulkerDefenceObject;
import de.gamechest.updater.Updater;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by ByteList on 25.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class StatsUpdater extends Thread {

    private final Updater updater = Updater.getInstance();
    private final Logger logger = updater.getLogger();

    public StatsUpdater() {
        super("Stats Updater Thread");
    }

    @Override
    public void run() {
        while (updater.isRunning) {
            clickAttack();
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shulkerDefence();
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deathRun();
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jumpDuell();
            try {
                logger.info("[Stats] Waiting 30 minutes for next update...");
                Thread.sleep(60000L*30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void clickAttack() {
        DatabaseClickAttack databaseClickAttack = updater.getDatabaseManager().getDatabaseClickAttack();
        ArrayList<UUID> players = new ArrayList<>(databaseClickAttack.getPlayers());

        players.sort((o1, o2) -> {
            Long point1 = databaseClickAttack.getDatabaseElement(o1, DatabaseClickAttackObject.POINTS).getAsLong(); // 500
            Long point2 = databaseClickAttack.getDatabaseElement(o2, DatabaseClickAttackObject.POINTS).getAsLong(); // 600

            return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
        });

        int rank = 0;

        for(UUID uuid : players) {
            rank++;
            databaseClickAttack.setDatabaseObject(uuid, DatabaseClickAttackObject.RANK, rank);
        }
        logger.info("[Stats] Updated ClickAttack stats!");
    }

    private void shulkerDefence() {
        DatabaseShulkerDefence databaseShulkerDefence = updater.getDatabaseManager().getDatabaseShulkerDefence();
        ArrayList<UUID> players = new ArrayList<>(databaseShulkerDefence.getPlayers());

        players.sort((o1, o2) -> {
            Long point1 = databaseShulkerDefence.getDatabaseElement(o1, DatabaseShulkerDefenceObject.POINTS).getAsLong(); // 500
            Long point2 = databaseShulkerDefence.getDatabaseElement(o2, DatabaseShulkerDefenceObject.POINTS).getAsLong(); // 600

            return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
        });

        int rank = 0;

        for(UUID uuid : players) {
            rank++;
            databaseShulkerDefence.setDatabaseObject(uuid, DatabaseShulkerDefenceObject.RANK, rank);
        }
        logger.info("[Stats] Updated ShulkerDefence stats!");
    }

    private void deathRun() {
        DatabaseDeathRun databaseDeathRun = updater.getDatabaseManager().getDatabaseDeathRun();
        ArrayList<UUID> players = new ArrayList<>(databaseDeathRun.getPlayers());

        players.sort((o1, o2) -> {
            Long point1 = databaseDeathRun.getDatabaseElement(o1, DatabaseDeathRunObject.POINTS).getAsLong(); // 500
            Long point2 = databaseDeathRun.getDatabaseElement(o2, DatabaseDeathRunObject.POINTS).getAsLong(); // 600

            return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
        });

        int rank = 0;

        for(UUID uuid : players) {
            rank++;
            databaseDeathRun.setDatabaseObject(uuid, DatabaseDeathRunObject.RANK, rank);
        }
        logger.info("[Stats] Updated DeathRun stats!");
    }

    private void jumpDuell() {
        DatabaseJumpDuell databaseJumpDuell = updater.getDatabaseManager().getDatabaseJumpDuell();
        ArrayList<UUID> players = new ArrayList<>(databaseJumpDuell.getPlayers());

        players.sort((o1, o2) -> {
            Long point1 = databaseJumpDuell.getDatabaseElement(o1, DatabaseJumpDuellObject.POINTS).getAsLong(); // 500
            Long point2 = databaseJumpDuell.getDatabaseElement(o2, DatabaseJumpDuellObject.POINTS).getAsLong(); // 600

            return (Objects.equals(point1, point2) ? 0 : point2.compareTo(point1));
        });

        int rank = 0;

        for(UUID uuid : players) {
            rank++;
            databaseJumpDuell.setDatabaseObject(uuid, DatabaseJumpDuellObject.RANK, rank);
        }
        logger.info("[Stats] Updated JumpDuell stats!");
    }



}
