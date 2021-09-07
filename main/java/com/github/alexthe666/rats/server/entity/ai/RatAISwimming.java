package com.github.alexthe666.rats.server.entity.ai;

import com.github.alexthe666.rats.server.entity.EntityRat;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import net.minecraft.entity.ai.goal.SwimGoal;

public class RatAISwimming extends SwimGoal {

    private EntityRat entityRat;

    public RatAISwimming(EntityRat entityRat) {
        super(entityRat);
        this.entityRat = entityRat;
    }

    public boolean shouldExecute() {
        return super.shouldExecute() && !entityRat.hasUpgrade(RatsItemRegistry.RAT_UPGRADE_AQUATIC);
    }

}
