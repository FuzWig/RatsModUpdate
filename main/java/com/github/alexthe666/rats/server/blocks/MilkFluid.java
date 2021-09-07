package com.github.alexthe666.rats.server.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class MilkFluid extends FlowingFluid {

    public MilkFluid(){
        this.setRegistryName("milk");
    }

    protected net.minecraftforge.fluids.FluidAttributes createAttributes()
    {
        return net.minecraftforge.fluids.FluidAttributes.Water.builder(
                new net.minecraft.util.ResourceLocation("rats:block/milk"),
                new net.minecraft.util.ResourceLocation("rats:block/milk_flowing"))
                .overlay(new net.minecraft.util.ResourceLocation("block/water_overlay"))
                .translationKey("block.rats.milk.name")
                .color(0xEEEEEE).build(this);
    }

    @Override
    public Item getFilledBucket() {
        return Items.MILK_BUCKET;
    }

    @Override
    protected boolean canDisplace(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
        return false;
    }

    @Override
    public int getTickRate(IWorldReader p_205569_1_) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    protected BlockState getBlockState(FluidState state) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return true;
    }

    @Override
    public int getLevel(FluidState p_207192_1_) {
        return 8;
    }

    @Override
    public Fluid getFlowingFluid() {
        return RatsFluidRegistry.MILK_FLUID;
    }

    @Override
    public Fluid getStillFluid() {
        return RatsFluidRegistry.MILK_FLUID;
    }

    @Override
    protected boolean canSourcesMultiply() {
        return false;
    }

    @Override
    protected void beforeReplacingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    protected int getSlopeFindDistance(IWorldReader iWorldReader) {
        return 0;
    }

    @Override
    protected int getLevelDecreasePerBlock(IWorldReader iWorldReader) {
        return 0;
    }
}
