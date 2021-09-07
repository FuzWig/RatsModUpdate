package com.github.alexthe666.rats.server.world;

public class RatlantisDimension {/* extends Dimension {
    public static final BlockPos SPAWN = new BlockPos(0, 110, 0);

    public RatlantisDimension(Supplier<DimensionType> p_i231900_1_, ChunkGenerator p_i231900_2_) {
        super(p_i231900_1_, p_i231900_2_);
    }

    public ChunkGenerator<?> createChunkGenerator() {
        OverworldGenSettings settings = ChunkGeneratorType.SURFACE.createSettings();
        settings.setDefaultBlock(RatsBlockRegistry.MARBLED_CHEESE.getDefaultState());
        SingleBiomeProviderSettings providerSettings = new SingleBiomeProviderSettings(null);
        providerSettings.setBiome(RatsWorldRegistry.RATLANTIS_BIOME);
        return new ChunkProviderRatlantis(this.world, new SingleBiomeProvider(providerSettings), settings);
     }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        double d0 = MathHelper.frac((double)worldTime / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float)(d0 * 2.0D + d1) / 3.0F;
    }

    @Override @Nullable
    @OnlyIn(Dist.CLIENT)
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return super.calcSunriseSunsetColors(celestialAngle, partialTicks);
    }

    @Override @OnlyIn(Dist.CLIENT)
    public Vector3d getFogColor(final float p_76562_1_, final float p_76562_2_) {
        float f = MathHelper.clamp(MathHelper.cos(p_76562_1_ * ((float) Math.PI * 2F)) * 2.0F + 0.5F, 0, 1F);
        float bright = 1.15F * f;
        float f1 = 1F;
        float f2 = 0.98F;
        float f3 = 0.79F;
        return new Vector3d((double) f1 * bright, (double) f2 * bright, (double) f3 * bright);
    }


    @Override
    public boolean hasSkyLight() {
        return true;
    }

    @Override @OnlyIn(Dist.CLIENT)
    public boolean isSkyColored() {
        return true;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return true;
    }

    @Override @OnlyIn(Dist.CLIENT)
    public float getCloudHeight() {
        return super.getCloudHeight() + 20;
    }

    @Override @Nullable
    public BlockPos findSpawn(final ChunkPos p_206920_1_, final boolean p_206920_2_) {
        final Random random = new Random(this.world.getSeed());
        final BlockPos blockpos = new BlockPos(p_206920_1_.getXStart() + random.nextInt(15), 0,
                p_206920_1_.getZEnd() + random.nextInt(15));
        return this.world.getGroundAboveSeaLevel(blockpos).getMaterial().blocksMovement() ? blockpos : null;
    }

    @Override
    public BlockPos getSpawnCoordinate() {
        return SPAWN;
    }

    @Override @Nullable
    public BlockPos findSpawn(final int p_206921_1_, final int p_206921_2_, final boolean p_206921_3_) {
        return this.findSpawn(new ChunkPos(p_206921_1_ >> 4, p_206921_2_ >> 4), p_206921_3_);
    }

    @Override @OnlyIn(Dist.CLIENT)
    public boolean doesXZShowFog(final int p_76568_1_, final int p_76568_2_) {
        return false;
    }*/
}