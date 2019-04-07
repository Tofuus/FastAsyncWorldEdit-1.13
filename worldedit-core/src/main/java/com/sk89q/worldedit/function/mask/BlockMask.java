package com.sk89q.worldedit.function.mask;

import com.boydti.fawe.object.collection.FastBitSet;
import com.boydti.fawe.util.MainUtil;
import com.boydti.fawe.util.StringMan;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.registry.state.AbstractProperty;
import com.sk89q.worldedit.registry.state.Property;
import static com.google.common.base.Preconditions.checkNotNull;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A mask that checks whether blocks at the given positions are matched by
 * a block in a list.
 *
 * <p>This mask checks for both an exact block type and state value match,
 * respecting fuzzy status of the BlockState.</p>
 */
public class BlockMask extends AbstractExtentMask {

    private final Set<BaseBlock> blocks = new HashSet<>();

    /**
     * Create a new block mask.
     *
     * @param extent the extent
     * @param blocks a list of blocks to match
     */
    public BlockMask(Extent extent, Collection<BaseBlock> blocks) {
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
    public BlockMask(Extent extent, BaseBlock... block) {
        this(extent, Arrays.asList(checkNotNull(block)));
    }

    /**
     * Add the given blocks to the list of criteria.
     *
     * @param blocks a list of blocks
     */
    public void add(Collection<BaseBlock> blocks) {
        checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    /**
     * Add the given blocks to the list of criteria.
     *
     * @param block an array of blocks
     */
    public void add(BaseBlock... block) {
        add(Arrays.asList(checkNotNull(block)));
    }

    /**
     * Get the list of blocks that are tested with.
     *
     * @return a list of blocks
     */
    public Collection<BaseBlock> getBlocks() {
        return blocks;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        BlockState block = getExtent().getBlock(vector);
        for (BaseBlock testBlock : blocks) {
            if (testBlock.equalsFuzzy(block)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public Mask2D toMask2D() {
        return null;
    }

}
