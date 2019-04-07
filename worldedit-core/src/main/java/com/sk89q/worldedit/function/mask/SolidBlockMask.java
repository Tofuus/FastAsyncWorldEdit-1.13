package com.sk89q.worldedit.function.mask;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.block.BlockTypes;

import javax.annotation.Nullable;

public class SolidBlockMask extends AbstractExtentMask {

    public SolidBlockMask(Extent extent) {
        super(extent);
    }

    @Override
    public boolean test(BlockVector3 vector) {
        Extent extent = getExtent();
        BlockState block = extent.getBlock(vector);
        return block.getBlockType().getMaterial().isMovementBlocker();
    }

    @Nullable
    @Override
    public Mask2D toMask2D() {
        return null;
    }

}
