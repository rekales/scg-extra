package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

// TODO: decouple code from SkyCarrierEntity cuz it's ass, MoveControl isn't supposed to handle ai logic
public class CogGigantesMoveControl extends MoveControl {

    private int floatDuration;

    public CogGigantesMoveControl(CogGigantesEntity mob) {
        super(mob);
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            if (this.floatDuration-- <= 0) {
                this.floatDuration += this.mob.getRandom().nextInt(5) + 2;
                Vec3 vec3 = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double d0 = vec3.length();
                vec3 = vec3.normalize();
                if (this.canReach(vec3, Mth.ceil(d0))) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vec3.scale(0.1D)));
                } else {
                    this.operation = MoveControl.Operation.WAIT;
                }
            }

        }
    }

    private boolean canReach(Vec3 pos, int length) {
        AABB aabb = this.mob.getBoundingBox();

        for(int i = 1; i < length; ++i) {
            aabb = aabb.move(pos);
            if (!this.mob.level().noCollision(this.mob, aabb)) {
                return false;
            }
        }

        return true;
    }
}