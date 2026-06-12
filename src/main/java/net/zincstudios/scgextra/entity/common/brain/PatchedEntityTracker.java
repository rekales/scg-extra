package net.zincstudios.scgextra.entity.common.brain;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.EntityTracker;

import javax.annotation.ParametersAreNonnullByDefault;

// By better I mean it uses the follow range attribute instead of
// NEAREST_VISIBLE_LIVING_ENTITIES memory module to maintain lock even if far and behind cover.
@ParametersAreNonnullByDefault
public class PatchedEntityTracker extends EntityTracker {

    public PatchedEntityTracker(Entity entity, boolean trackEyeHeight) {
        super(entity, trackEyeHeight);
    }

    @Override
    public boolean isVisibleBy(LivingEntity entity) {
        Entity target = this.getEntity();
        if (target instanceof LivingEntity livingentity) {
            if (!livingentity.isAlive()) {
                return false;
            } else {
                double range = Math.max(entity.getAttributeValue(Attributes.FOLLOW_RANGE), 2.0D);
                return entity.closerThan(livingentity, range);
            }
        } else {
            return true;
        }
    }
}
