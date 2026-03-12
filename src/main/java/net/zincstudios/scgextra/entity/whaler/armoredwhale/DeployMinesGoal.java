package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import top.ribs.scguns.entity.throwable.ThrowableGrenadeEntity;
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
                ThrowableGrenadeEntity en = new ThrowableGrenadeEntity(top.ribs.scguns.init.ModEntities.THROWABLE_GRENADE.get(), this.mob.level());
                en.setPos(this.mob.getEyePosition());
                en.shoot(0, 1, 0, this.throwVelocity, 30);
                this.mob.level().addFreshEntity(en);
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