package com.github.alexthe666.rats.server.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.ratlantis.IRatlantean;
import com.github.alexthe666.rats.server.misc.RatsSoundRegistry;
import com.google.common.base.Predicate;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityRatKing extends MonsterEntity implements IAnimatedEntity, IRatlantean, ISummonsRats {
    public static final int RAT_COUNT = 15;
    public static final float RAT_ANGLE = 360F / RAT_COUNT;
    private int animationTick;
    private Animation currentAnimation;
    private static final DataParameter<String> RAT_COLORS = EntityDataManager.createKey(EntityRatKing.class, DataSerializers.STRING);
    private static final DataParameter<Integer> SUMMONED_RATS = EntityDataManager.createKey(EntityRatKing.class, DataSerializers.VARINT);
    private static final Predicate<LivingEntity> NOT_RAT = new Predicate<LivingEntity>() {
        public boolean apply(@Nullable LivingEntity entity) {
            return entity.isAlive() && !(entity instanceof EntityRat) && !(entity instanceof EntityRatKing);
        }
    };

    public EntityRatKing(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        if (potioneffectIn.getPotion() == RatsMod.PLAGUE_POTION) {
            return false;
        }
        return super.isPotionApplicable(potioneffectIn);
    }

    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, NOT_RAT));

    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(RAT_COLORS, "000000000000000");
        this.dataManager.register(SUMMONED_RATS, Integer.valueOf(0));
    }

    public static AttributeModifierMap.MutableAttribute func_234290_eH_() {
        return MobEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233818_a_, 200.0D)            //HEALTH
                .func_233815_a_(Attributes.field_233821_d_, 0.15D)           //SPEED
                .func_233815_a_(Attributes.field_233823_f_, 2.0D)            //ATTACK
                .func_233815_a_(Attributes.field_233819_b_, 64.0D);         //FOLLOW RANGE
    }

    public void livingTick() {
        super.livingTick();
        LivingEntity target = this.getAttackTarget();
        if(!world.isRemote && target != null & this.ticksExisted % 15 == 0 && this.getRatsSummoned() < 10){
            EntityRatShot cannonball = new EntityRatShot(RatsEntityRegistry.RAT_SHOT, world, this);
            cannonball.setColorVariant(rand.nextInt(4));
            float radius = 1.6F;
            float angle = (0.01745329251F * (this.renderYawOffset));
            double extraX = getPosX();
            double extraZ = getPosZ();
            double extraY = 0.2 + getPosY();
            double d0 = target.getPosYEye() - (double)1.2F;
            double d1 = target.getPosX() - extraX;
            double d3 = target.getPosZ() - extraZ;
            double d2 = d0 - extraY;
            float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 1;
            float velocity = Math.min(MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.06F, 1.4F);
            cannonball.setPosition(extraX, extraY, extraZ);
            cannonball.shoot(d1, d2 + (double) f, d3, velocity, 0);
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 3.0F, 2.3F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            if (!world.isRemote) {
                this.world.addEntity(cannonball);
            }
        }
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            for(int i = 0; i < 3 + rand.nextInt(3); i++){
                this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    public int getTalkInterval() {
        return 20;
    }


    @Override
    protected void playHurtSound(DamageSource source) {
        SoundEvent soundevent = this.getHurtSound(source);
        if (soundevent != null) {
            for(int i = 0; i < 3 + rand.nextInt(3); i++){
                this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
            }
        }

    }

    protected SoundEvent getAmbientSound() {
        return RatsSoundRegistry.RAT_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return RatsSoundRegistry.RAT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return RatsSoundRegistry.RAT_DIE;
    }


    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        for (int i = 0; i < RAT_COUNT; i++) {
            int color = rand.nextInt(4);
            setRatColors(i, color);
        }
        return spawnDataIn;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("RatColors", getRatColorsString());
        compound.putInt("RatsSummoned", this.getRatsSummoned());
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setRatsSummoned(compound.getInt("RatsSummoned"));
        this.setRatColorsString(compound.getString("RatColors"));
    }

    private void setRatColorsString(String str) {
        this.dataManager.set(RAT_COLORS, str);
    }

    private String getRatColorsString() {
        return this.dataManager.get(RAT_COLORS);
    }

    public void setRatColors(int index, int color){
        String ratColors = getRatColorsString();
        if(ratColors.length() < RAT_COUNT - 1){
            ratColors = "000000000000000";
        }
        String before = ratColors.substring(0, index);
        String after = ratColors.substring(index, RAT_COUNT-1);
        String newStr = before + color + after;
        setRatColorsString(newStr);
    }

    public int getRatColors(int index){
        String ratColors = getRatColorsString();
        if(ratColors.length() < RAT_COUNT - 1){
            ratColors = "000000000000000";
        }
        char c = ratColors.charAt(index);
        return Integer.parseInt(String.valueOf(c));
    }

    @Override
    public boolean encirclesSummoner() {
        return true;
    }

    @Override
    public boolean readsorbRats() {
        return this.getRatsSummoned() > 5;
    }

    public int getRatsSummoned() {
        return Integer.valueOf(this.dataManager.get(SUMMONED_RATS).intValue());
    }

    public void setRatsSummoned(int count) {
        this.dataManager.set(SUMMONED_RATS, Integer.valueOf(count));
    }

    @Override
    public float getRadius() {
        return (float) 3F;
    }


    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{};
    }


    public BlockPos getLightPosition() {
        BlockPos pos = new BlockPos(this.getPositionVec());
        if (!world.getBlockState(pos).isSolid()) {
            return pos.up();
        }
        return pos;
    }
}
