/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class BlockNetherWartRecollectable extends PlantRecollectable {

    public BlockNetherWartRecollectable() {
        super("blocknetherwart");
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, BlockState blockState) {
        return blockState.getBlock() instanceof NetherWartBlock && blockState.get(NetherWartBlock.AGE) >= 3;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, BlockState blockState) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(BlockUtils.getBlockDrops(world, pos));
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        return stacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, BlockState blockState) {
        return true;
    }

    @Override
    public List<String> getRecollectablesNames() {
        return Arrays.asList("text.industrialforegoing.plant.nether_wart");
    }
}
