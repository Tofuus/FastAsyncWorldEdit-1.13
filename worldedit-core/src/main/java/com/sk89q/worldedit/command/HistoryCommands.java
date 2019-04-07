/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.command;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.config.BBC;
import com.boydti.fawe.config.Settings;
import com.boydti.fawe.database.DBHandler;
import com.boydti.fawe.database.RollbackDatabase;
import com.boydti.fawe.logging.rollback.RollbackOptimizedHistory;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.RegionWrapper;
import com.boydti.fawe.object.RunnableVal;
import com.boydti.fawe.object.changeset.DiskStorageHistory;
import com.boydti.fawe.regions.FaweMaskManager;
import com.boydti.fawe.util.MainUtil;
import com.boydti.fawe.util.MathMan;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.command.binding.Range;
import com.sk89q.worldedit.util.command.binding.Switch;
import com.sk89q.worldedit.util.command.parametric.Optional;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Commands to undo, redo, and clear history.
 */
public class HistoryCommands {

    private final WorldEdit worldEdit;

    /**
     * Create a new instance.
     *
     * @param worldEdit reference to WorldEdit
     */
    public HistoryCommands(WorldEdit worldEdit) {
        checkNotNull(worldEdit);
        this.worldEdit = worldEdit;
    }

    @Command(
        aliases = { "/undo", "undo" },
        usage = "[times] [player]",
        desc = "Undoes the last action",
        min = 0,
        max = 2
    )
    @CommandPermissions("worldedit.history.undo")
    public void undo(Player player, LocalSession session, CommandContext args) throws WorldEditException {
        int times = Math.max(1, args.getInteger(0, 1));
        for (int i = 0; i < times; ++i) {
            EditSession undone;
            if (args.argsLength() < 2) {
                undone = session.undo(session.getBlockBag(player), player);
            } else {
                player.checkPermission("worldedit.history.undo.other");
                LocalSession sess = worldEdit.getSessionManager().findByName(args.getString(1));
                if (sess == null) {
                    player.printError("Unable to find session for " + args.getString(1));
                    break;
                }
                undone = sess.undo(session.getBlockBag(player), player);
            }
            if (undone != null) {
                player.print("Undo successful.");
                worldEdit.flushBlockBag(player, undone);
            } else {
                player.printError("Nothing left to undo.");
                break;
            }
        }
    }

    @Command(
        aliases = { "/redo", "redo" },
        usage = "[times] [player]",
        desc = "Redoes the last action (from history)",
        min = 0,
        max = 2
    )
    @CommandPermissions("worldedit.history.redo")
    public void redo(Player player, LocalSession session, CommandContext args) throws WorldEditException {
        
        int times = Math.max(1, args.getInteger(0, 1));

        for (int i = 0; i < times; ++i) {
            EditSession redone;
            if (args.argsLength() < 2) {
                redone = session.redo(session.getBlockBag(player), player);
            } else {
                player.checkPermission("worldedit.history.redo.other");
                LocalSession sess = worldEdit.getSessionManager().findByName(args.getString(1));
                if (sess == null) {
                    player.printError("Unable to find session for " + args.getString(1));
                    break;
                }
                redone = sess.redo(session.getBlockBag(player), player);
            }
            if (redone != null) {
                player.print("Redo successful.");
                worldEdit.flushBlockBag(player, redone);
            } else {
                player.printError("Nothing left to redo.");
            }
        }
    }

    @Command(
        aliases = { "/clearhistory", "clearhistory" },
        usage = "",
        desc = "Clear your history",
        min = 0,
        max = 0
    )
    @CommandPermissions("worldedit.history.clear")
    public void clearHistory(Player player, LocalSession session) throws WorldEditException {
        session.clearHistory();
        player.print("History cleared.");
    }

}
