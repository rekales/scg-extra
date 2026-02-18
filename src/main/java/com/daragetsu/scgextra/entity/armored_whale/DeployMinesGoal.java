package com.daragetsu.scgextra.entity.armored_whale;

import com.daragetsu.scgextra.SCGExtra;
import com.daragetsu.scgextra.entity.ModEntities;
import com.daragetsu.scgextra.entity.projectile.DeployedMineEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class DeployMinesGoal extends Goal {

    protected final ArmoredWhaleEntity mob;
    private final int maxInterval;
    private final float throwVelocity;
    private final int minCount;
    private final int maxCount;
    private int cooldown = 0;
    private int throwTicks = 0;

    public DeployMinesGoal(ArmoredWhaleEntity mob, int maxInterval, float throwVelocity, int minCount, int maxCount) {
        this.mob = mob;
        this.maxInterval = maxInterval;
        this.throwVelocity = throwVelocity;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = maxInterval;
    }

    @Override
    public void stop() {
        super.stop();
        this.throwTicks = 0;
        this.cooldown = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.throwTicks > 0) {
            this.throwTicks--;
        }
        if (this.throwTicks == 7) {  // match with animation frames

            int amount = this.mob.getRandom().nextIntBetweenInclusive(this.minCount, this.maxCount);

            for (int i = 0; i < amount; i++) {
                DeployedMineEntity mine = new DeployedMineEntity(ModEntities.DEPLOYED_MINE.get(), this.mob.level());
                mine.setPos(this.mob.getEyePosition());
                mine.setOwner(this.mob);
                mine.shoot(0 , 1, 0, this.throwVelocity, 30);
                this.mob.level().addFreshEntity(mine);
            }
        }

        if (this.cooldown > 0) {
            this.cooldown--;
        } else {
            this.cooldown = maxInterval;
            this.throwTicks = 25;
            this.mob.triggerAnim("deploy", "deploy_mines");
        }
    }
}