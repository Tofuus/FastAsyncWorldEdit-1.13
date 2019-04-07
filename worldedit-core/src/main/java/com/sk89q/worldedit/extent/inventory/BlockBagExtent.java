package com.sk89q.worldedit.extent.inventory;

import com.boydti.fawe.object.exception.FaweException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Applies a {@link BlockBag} to operations.
 */
public class BlockBagExtent extends AbstractDelegateExtent {

    private Map<BlockType, Integer> missingBlocks = new HashMap<>();
    private BlockBag blockBag;

    /**
     * Create a new instance.
     *
     * @param extent the extent
     * @param blockBag the block bag
     */
    public BlockBagExtent(Extent extent, @Nullable BlockBag blockBag) {
        super(extent);
        this.blockBag = blockBag;
    }

    /**
     * Get the block bag.
     *
     * @return a block bag, which may be null if none is used
     */
    public @Nullable BlockBag getBlockBag() {
        return blockBag;
    }

    /**
     * Set the block bag.
     *
     * @param blockBag a block bag, which may be null if none is used
     */
    public void setBlockBag(@Nullable BlockBag blockBag) {
        this.blockBag = blockBag;
    }

    /**
     * Gets the list of missing blocks and clears the list for the next
     * operation.
     *
     * @return a map of missing blocks
     */
    public Map<BlockType, Integer> popMissing() {
        Map<BlockType, Integer> missingBlocks = this.missingBlocks;
        this.missingBlocks = new HashMap<>();
        return missingBlocks;
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws WorldEditException {
        if (blockBag != null) {
            BlockState existing = getExtent().getBlock(position);

            if (!block.getBlockType().equals(existing.getBlockType())) {
                if (!block.getBlockType().getMaterial().isAir()) {
                    try {
                        blockBag.fetchPlacedBlock(block.toImmutableState());
                    } catch (UnplaceableBlockException e) {
                        return false;
                    } catch (BlockBagException e) {
                        if (!missingBlocks.containsKey(block.getBlockType())) {
                            missingBlocks.put(block.getBlockType(), 1);
                        } else {
                            missingBlocks.put(block.getBlockType(), missingBlocks.get(block.getBlockType()) + 1);
                        }
                        return false;
                    }
                }

                if (!existing.getBlockType().getMaterial().isAir()) {
                    try {
                        blockBag.storeDroppedBlock(existing);
                    } catch (BlockBagException ignored) {
                    }
                }
            }
        }

        return super.setBlock(position, block);
    }
}
