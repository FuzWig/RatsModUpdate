package com.github.alexthe666.rats.server.entity.ai.navigation;

import com.github.alexthe666.rats.server.entity.EntityRat;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class RatAquaticMoveHelper extends MovementController {
        EntityRat rat;

    public RatAquaticMoveHelper(EntityRat rat) {
        super(rat);
        this.rat = rat;
    }

    public void tick() {
        if (this.action == MovementController.Action.MOVE_TO && this.rat.isInWater()) {
            if (rat.collidedHorizontally && !rat.func_233570_aj_()) {
                rat.rotationYaw += 180.0F;
                int dist = 3;
                if (!rat.isInCage()) {
                    this.speed = 0.1F;
                    dist = 8;
                }
                BlockPos target = EntityRat.getPositionRelativetoWater(rat, rat.world, rat.getPosX() + rat.getRNG().nextInt(dist * 2) - dist, rat.getPosZ() + rat.getRNG().nextInt(dist * 2) - dist, rat.getRNG());
                this.setMoveTo(target.getX(), target.getY(), target.getZ(), this.speed);
            }
            Vector3d vec3d = new Vector3d(this.getX() - rat.getPosX(), this.getY() - rat.getPosY(), this.getZ() - rat.getPosZ());
            double d0 = vec3d.length();
            double edgeLength = rat.getBoundingBox().getAverageEdgeLength();
            if (d0 < edgeLength) {
                this.action = MovementController.Action.WAIT;
                rat.setMotion(rat.getMotion().scale(0.5D));
            } else {
                rat.setMotion(rat.getMotion().add(vec3d.scale(this.speed * 0.1D / d0)));
                if (rat.getAttackTarget() == null) {
                    Vector3d vec3d1 = rat.getMotion();
                    rat.rotationYaw = -((float)MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float)Math.PI);
                    rat.renderYawOffset = rat.rotationYaw;
                } else {
                    double d4 = rat.getAttackTarget().getPosX() - rat.getPosX();
                    double d5 = rat.getAttackTarget().getPosZ() - rat.getPosZ();
                    rat.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                    rat.renderYawOffset = rat.rotationYaw;
                }

            }
        }
    }
}
