package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

// Copied from Vex.VexMoveControl
public class SoulRipperMoveControl extends MoveControl {

    public SoulRipperMoveControl(SoulRipperEntity mob) {
        super(mob);
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 vec3 = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
            double d0 = vec3.length();
            if (d0 < this.mob.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
            } else {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
                if (this.mob.getTarget() == null) {
                    Vec3 vec31 = this.mob.getDeltaMovement();
                    this.mob.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
                    this.mob.yBodyRot = this.mob.getYRot();
                } else {
                    double d2 = this.mob.getTarget().getX() - this.mob.getX();
                    double d1 = this.mob.getTarget().getZ() - this.mob.getZ();
                    this.mob.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                    this.mob.yBodyRot = this.mob.getYRot();
                }
            }

        }
    }
}