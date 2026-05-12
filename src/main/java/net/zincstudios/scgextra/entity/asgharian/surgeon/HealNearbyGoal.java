package net.zincstudios.scgextra.entity.asgharian.surgeon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class HealNearbyGoal<T extends LivingEntity> extends Goal {

    protected final PathfinderMob mob;
    protected final float radius;
    protected final Class<T> targetType;
    protected final Predicate<LivingEntity> targetPredicate;

    public HealNearbyGoal(PathfinderMob mob, float radius, Class<T> targetType, Predicate<LivingEntity> targetPredicate) {
        this.mob = mob;
        this.radius = radius;
        this.targetType = targetType;
        this.targetPredicate = targetPredicate;
    }

    @Override
    public boolean canUse() {
        return this.mob.isAlive();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    // TODO: add visual
    @Override
    public void tick() {
        if (this.mob.tickCount % 10 == 0) {
            AABB aabb = this.mob.getBoundingBox().inflate(this.radius, this.radius/2, this.radius);
            List<LivingEntity> nearbyEntities = this.mob.level().getEntitiesOfClass(
                    LivingEntity.class,
                    aabb,
                    this.targetPredicate.and(entity ->
                            entity instanceof LivingEntity && entity.distanceTo(this.mob) <= this.radius)
            );

            for (LivingEntity entity : nearbyEntities) {
                entity.heal(1);
            }
        }

    }
}
