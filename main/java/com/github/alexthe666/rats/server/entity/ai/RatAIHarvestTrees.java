package com.github.alexthe666.rats.server.entity.ai;

import com.github.alexthe666.rats.server.entity.EntityRat;
import com.github.alexthe666.rats.server.entity.RatCommand;
import com.github.alexthe666.rats.server.entity.RatUtils;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class RatAIHarvestTrees extends Goal {
    private final EntityRat entity;
    private final RatAIHarvestTrees.BlockSorter targetSorter;
    private BlockPos targetBlock = null;
    private int destroyedLeaves;
    private int breakingTime;
    private int previousBreakProgress;
    private BlockPos offsetToAirPos = null;

    public RatAIHarvestTrees(EntityRat entity) {
        super();
        this.entity = entity;
        this.targetSorter = new RatAIHarvestTrees.BlockSorter(entity);
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public static final boolean isBlockLog(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isIn(BlockTags.LOGS);
    }

    public static final boolean isBlockLeaf(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof LeavesBlock;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.canMove() || !this.entity.isTamed() || this.entity.getCommand() != RatCommand.HARVEST || this.entity.isInCage() || !this.entity.hasUpgrade(RatsItemRegistry.RAT_UPGRADE_LUMBERJACK)) {
            return false;
        }
        if (!this.entity.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            return false;
        }
        resetTarget();
        return targetBlock != null;
    }

    private void resetTarget() {
        World world = entity.world;
        List<BlockPos> allBlocks = new ArrayList<>();
        int RADIUS = entity.getSearchRadius();
        for (BlockPos pos : BlockPos.getAllInBox(this.entity.getSearchCenter().add(-RADIUS, -RADIUS, -RADIUS), this.entity.getSearchCenter().add(RADIUS, RADIUS, RADIUS)).map(BlockPos::toImmutable).collect(Collectors.toList())) {
            if (isBlockLog(world, pos) && world.getBlockState(pos.down()).isSolid() && !isBlockLog(world, pos.down())) {
                BlockPos topOfLog = new BlockPos(pos);
                while (!world.isAirBlock(topOfLog.up()) && topOfLog.getY() < world.getHeight()) {
                    topOfLog = topOfLog.up();
                }
                if (isBlockLeaf(world, topOfLog)) {
                    //definitely a tree, now find the base of the tree
                    BlockPos logPos = getStump(topOfLog);
                    allBlocks.add(logPos);
                }
            }
        }
        if (!allBlocks.isEmpty()) {
            allBlocks.sort(this.targetSorter);
            this.targetBlock = allBlocks.get(0);
        }
    }

    private BlockPos getStump(BlockPos log) {
        if (log.getY() > 0) {
            for (BlockPos pos : BlockPos.getAllInBox(log.add(-4, -4, -4), log.add(4, 0, 4)).map(BlockPos::toImmutable).collect(Collectors.toList())) {
                if (isBlockLog(entity.world, pos.down()) || isBlockLeaf(entity.world, pos.down())) {
                    return getStump(pos.down());
                }
            }
        }
        return log;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return targetBlock != null && this.entity.getHeldItem(Hand.MAIN_HAND).isEmpty();
    }

    public void resetTask() {
        this.entity.getNavigator().clearPath();
        destroyedLeaves = 0;
        entity.crafting = false;
        resetTarget();
    }

    public BlockPos getOffsetedToAirPos(){
        if(targetBlock != null){
            for(Direction direction : Direction.values()){
                BlockPos offsetPos = targetBlock.offset(direction);
                if(entity.world.isAirBlock(offsetPos) || !entity.world.getBlockState(offsetPos).isSolid()){
                    return offsetPos;
                }
            }
        }
        return targetBlock;
    }

    @Override
    public void tick() {
        if (this.targetBlock != null) {
            RayTraceResult rayTrace = RatUtils.rayTraceBlocksIgnoreRatholes(entity.world, entity.getPositionVec().add(0, entity.getEyeHeight(), 0), new Vector3d(this.targetBlock.getX() + 0.5D, this.targetBlock.getY() + 0.5D, this.targetBlock.getZ() + 0.5D), false, entity);
            if (rayTrace instanceof BlockRayTraceResult) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTrace;
                BlockPos rayPos = entity.rayTraceBlockPos(this.targetBlock);
                if (rayPos == null) {
                    rayPos = this.targetBlock;
                }
                if (isBlockLeaf(this.entity.world, rayPos)) {
                    this.entity.world.destroyBlock(rayPos, true);
                }

            }
            if (isBlockLeaf(this.entity.world, new BlockPos(this.entity.getPositionVec().subtract(0, -1 ,0)))) {
                this.entity.world.destroyBlock(new BlockPos(this.entity.getPositionVec().subtract(0, -1 ,0)), true);
            }
            offsetToAirPos = getOffsetedToAirPos();
            this.entity.getNavigator().tryMoveToXYZ(offsetToAirPos.getX() + 0.5D, offsetToAirPos.getY(), offsetToAirPos.getZ() + 0.5D, 1.25D);
            if (isBlockLog(this.entity.world, this.targetBlock)) {
                double distance = this.entity.getRatDistanceCenterSq(this.targetBlock.getX(), this.targetBlock.getY(), this.targetBlock.getZ());
                if (distance < 6F * entity.getRatDistanceModifier()) {
                    entity.world.setEntityState(entity, (byte) 85);
                    entity.crafting = true;
                    if (distance < 0.6F * entity.getRatDistanceModifier()) {
                        this.entity.setMotion(0, 0, 0);
                        entity.getNavigator().clearPath();
                        //entity.moveController.action = MovementController.Action.WAIT;
                    }
                    breakingTime++;
                    int i = (int) ((float) this.breakingTime / 160.0F * 10.0F);
                    if (breakingTime % 10 == 0) {
                        entity.playSound(SoundEvents.BLOCK_WOOD_HIT, 1, 1);
                        entity.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.5F);
                    }
                    if (i != this.previousBreakProgress) {
                        entity.world.sendBlockBreakProgress(entity.getEntityId(), targetBlock, i);
                        this.previousBreakProgress = i;
                    }
                    if (this.breakingTime >= 160) {
                        entity.world.setEntityState(entity, (byte) 86);
                        entity.playSound(SoundEvents.BLOCK_WOOD_BREAK, 1, 1);
                        this.breakingTime = 0;
                        this.previousBreakProgress = -1;
                        this.fellTree();
                        this.entity.fleePos = this.targetBlock;
                        this.targetBlock = null;
                        entity.crafting = false;
                        this.resetTask();
                    }
                }
            } else {
                this.entity.fleePos = this.targetBlock;
                this.targetBlock = null;
                this.resetTask();
            }

        }
    }

    private void fellTree() {
        World world = entity.world;
        BlockPos base = new BlockPos(this.targetBlock);
        Queue<BlockPos> queue = new LinkedList<BlockPos>();
        while (isBlockLog(world, base)) {
            if (!queue.contains(base)) {
                queue.add(base);
            }
            for (BlockPos pos : BlockPos.getAllInBox(base.add(-2, 0, -2), base.add(2, 2, 2)).map(BlockPos::toImmutable).collect(Collectors.toList())) {
                if (isBlockLog(world, pos) && !queue.contains(pos)) {
                    if (isBlockLog(world, pos.up()) && !isBlockLog(world, base.up())) {
                        base = pos;
                    }
                    queue.add(pos);
                }
            }
            base = base.up();
        }
        while (!queue.isEmpty()) {
            BlockPos pop = queue.remove();
            world.destroyBlock(pop, true);
        }
    }

    public class BlockSorter implements Comparator<BlockPos> {
        private final Entity entity;

        public BlockSorter(Entity entity) {
            this.entity = entity;
        }

        @Override
        public int compare(BlockPos pos1, BlockPos pos2) {
            double yDist1 = Math.abs(pos1.getY() + 0.5 - entity.getPosY());
            double yDist2 = Math.abs(pos2.getY() + 0.5 - entity.getPosY());
            if (yDist1 == yDist2) {
                double distance1 = this.getDistance(pos1);
                double distance2 = this.getDistance(pos2);
                return Double.compare(distance1, distance2);
            } else {
                return Double.compare(yDist1, yDist2);
            }
        }

        private double getDistance(BlockPos pos) {
            double deltaX = this.entity.getPosX() - (pos.getX() + 0.5);
            double deltaY = this.entity.getPosY() + this.entity.getEyeHeight() - (pos.getY() + 0.5);
            double deltaZ = this.entity.getPosZ() - (pos.getZ() + 0.5);
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        }
    }
}