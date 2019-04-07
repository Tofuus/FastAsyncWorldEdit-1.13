package com.sk89q.worldedit.function.pattern;

import com.boydti.fawe.object.collection.RandomCollection;
import com.boydti.fawe.object.random.SimpleRandom;
import com.boydti.fawe.object.random.TrueRandom;
import com.sk89q.worldedit.WorldEditException;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.extent.Extent;
import static com.google.common.base.Preconditions.checkNotNull;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Uses a random pattern of a weighted list of patterns.
 */
public class RandomPattern extends AbstractPattern {

    private final Random random = new Random();
    private List<Chance> patterns = new ArrayList<>();
    private double max = 0;

    /**
     * Add a pattern to the weight list of patterns.
     *
     * <p>The probability for the pattern added is chance / max where max is
     * the sum of the probabilities of all added patterns.</p>
     *
     * @param pattern the pattern
     * @param chance the chance, which can be any positive number
     */
    public void add(Pattern pattern, double chance) {
        checkNotNull(pattern);
        patterns.add(new Chance(pattern, chance));
        max += chance;
    }

    @Override
    public BaseBlock apply(BlockVector3 position) {
        double r = random.nextDouble();
        double offset = 0;

        for (Chance chance : patterns) {
            if (r <= (offset + chance.getChance()) / max) {
                return chance.getPattern().apply(position);
            }
            offset += chance.getChance();
        }

        throw new RuntimeException("ProportionalFillPattern");
    }

    private static class Chance {
        private Pattern pattern;
        private double chance;

        private Chance(Pattern pattern, double chance) {
            this.pattern = pattern;
            this.chance = chance;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public double getChance() {
            return chance;
        }
    }

}
