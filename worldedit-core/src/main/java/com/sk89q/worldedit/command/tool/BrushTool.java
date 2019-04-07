package com.sk89q.worldedit.command.tool;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.config.BBC;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.object.RunnableVal;
import com.boydti.fawe.object.brush.BrushSettings;
import com.boydti.fawe.object.brush.MovableTool;
import com.boydti.fawe.object.brush.ResettableTool;
import com.boydti.fawe.object.brush.TargetMode;
import com.boydti.fawe.object.brush.scroll.ScrollAction;
import com.boydti.fawe.object.brush.scroll.ScrollTool;
import com.boydti.fawe.object.brush.visualization.VisualChunk;
import com.boydti.fawe.object.brush.visualization.VisualExtent;
import com.boydti.fawe.object.brush.visualization.VisualMode;
import com.boydti.fawe.object.extent.ResettableExtent;
import com.boydti.fawe.object.mask.MaskedTargetBlock;
import com.boydti.fawe.object.pattern.PatternTraverser;
import com.boydti.fawe.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.*;

import com.sk89q.worldedit.internal.expression.Expression;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.function.mask.SolidBlockMask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockType;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;


import static com.google.common.base.Preconditions.checkNotNull;

public class BrushTool implements TraceTool {

    protected static int MAX_RANGE = 500;
    protected int range = -1;
    private Mask mask = null;
    private Brush brush = new SphereBrush();
    @Nullable
    private Pattern material;
    private double size = 1;
    private String permission;

    /**
     * Construct the tool.
     * 
     * @param permission the permission to check before use is allowed
     */
    public BrushTool(String permission) {
        checkNotNull(permission);
        this.permission = permission;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission(permission);
    }

    /**
     * Get the filter.
     * 
     * @return the filter
     */
    public Mask getMask() {
        return mask;
    }

    /**
     * Set the block filter used for identifying blocks to replace.
     * 
     * @param filter the filter to set
     */
    public void setMask(Mask filter) {
        this.mask = filter;
    }

    /**
     * Set the brush.
     * 
     * @param brush tbe brush
     * @param permission the permission
     */
    public void setBrush(Brush brush, String permission) {
        this.brush = brush;
        this.permission = permission;
    }

    /**
     * Get the current brush.
     * 
     * @return the current brush
     */
    public Brush getBrush() {
        return brush;
    }

    /**
     * Set the material.
     * 
     * @param material the material
     */
    public void setFill(@Nullable Pattern material) {
        this.material = material;
    }

    /**
     * Get the material.
     *
     * @return the material
     */
    @Nullable public Pattern getMaterial() {
        return material;
    }

    /**
     * Get the set brush size.
     * 
     * @return a radius
     */
    public double getSize() {
        return size;
    }

    /**
     * Set the set brush size.
     * 
     * @param radius a radius
     */
    public void setSize(double radius) {
        this.size = radius;
    }

    /**
     * Get the set brush range.
     * 
     * @return the range of the brush in blocks
     */
    public int getRange() {
        return (range < 0) ? MAX_RANGE : Math.min(range, MAX_RANGE);
    }

    /**
     * Set the set brush range.
     * 
     * @param range the range of the brush in blocks
     */
    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        Location target = null;
        target = player.getBlockTrace(getRange(), true);

        if (target == null) {
            player.printError("No block in sight!");
            return true;
        }

        BlockBag bag = session.getBlockBag(player);

        try (EditSession editSession = session.createEditSession(player)) {
            if (mask != null) {
                Mask existingMask = editSession.getMask();

                if (existingMask == null) {
                    editSession.setMask(mask);
                } else if (existingMask instanceof MaskIntersection) {
                    ((MaskIntersection) existingMask).add(mask);
                } else {
                    MaskIntersection newMask = new MaskIntersection(existingMask);
                    newMask.add(mask);
                    editSession.setMask(newMask);
                }
            }

            try {
                brush.build(editSession, target.toVector().toBlockPoint(), material, size);
            } catch (MaxChangedBlocksException e) {
                player.printError("Max blocks change limit reached.");
            } finally {
                session.remember(editSession);
            }
        } finally {
            if (bag != null) {
                bag.flushChanges();
            }
        }

        return true;
    }

}
