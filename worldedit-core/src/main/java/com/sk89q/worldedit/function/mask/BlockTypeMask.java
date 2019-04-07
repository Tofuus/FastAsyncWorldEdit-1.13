package com.sk89q.worldedit.function.mask;

import static com.google.common.base.Preconditions.checkNotNull;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class BlockTypeMask extends AbstractExtentMask {

    private final Set<BlockType> blocks = new HashSet<>();

    /**
     * Create a new block mask.
     *
     * @param extent the extent
     * @param blocks a list of blocks to match
     */
    public BlockTypeMask(Extent extent, Collection<BlockType> blocks) {
        super(extent);
        checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    /**
     * Create a new block mask.
     *
     * @param extent the extent
     * @param block an array of blocks to match
     */
    public BlockTypeMask(Extent extent, BlockType... block) {
        this(extent, Arrays.asList(checkNotNull(block)));
    }

    /**
     * Add the given blocks to the list of criteria.
     *
     * @param blocks a list of blocks
     */
    public void add(Collection<BlockType> blocks) {
        checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    /**
     * Add the given blocks to the list of criteria.
     *
     * @param block an array of blocks
     */
    public void add(BlockType... block) {
        add(Arrays.asList(checkNotNull(block)));
    }

    /**
     * Get the list of blocks that are tested with.
     *
     * @return a list of blocks
     */
    public Collection<BlockType> getBlocks() {
        return blocks;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return blocks.contains(getExtent().getBlock(vector).getBlockType());
    }

    @Nullable
    @Override
    public Mask2D toMask2D() {
        return null;
    }
}
