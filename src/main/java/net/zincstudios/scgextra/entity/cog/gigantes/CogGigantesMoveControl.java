package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.entity.monster.SkyCarrierEntity;

import java.util.List;

// TODO: decouple code from SkyCarrierEntity cuz it's ass, MoveControl isn't supposed to handle ai logic

// Boiled down version of SkyCarrierMoveControl
public class CogGigantesMoveControl extends MoveControl {

    private final double minDistance;
    private final double maxDistance;
    private final double bufferZone;
    private final double maxSpeed;
    private final double backingSpeed;
    private Vec3 currentVelocity;
    private float currentYaw;
    private float targetYaw;

    public CogGigantesMoveControl(CogGigantesEntity mob, double minDistance, double maxDistance, double bufferZone, double maxSpeed, double backingSpeed) {
        super(mob);
        this.currentVelocity = Vec3.ZERO;
        this.currentYaw = 0.0F;
        this.targetYaw = 0.0F;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.bufferZone = bufferZone;
        this.maxSpeed = maxSpeed;
        this.backingSpeed = backingSpeed;
        this.currentYaw = this.mob.getYRot();
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        Vec3 desiredVelocity = Vec3.ZERO;
        AABB repulsionBox = this.mob.getBoundingBox().inflate((double)2.0F);
        List<SkyCarrierEntity> nearbyCarriers = this.mob.level().getEntitiesOfClass(SkyCarrierEntity.class, repulsionBox, (e) -> e != this.mob);
        Vec3 repulsionVector = Vec3.ZERO;

        for(SkyCarrierEntity other : nearbyCarriers) {
            Vec3 toOther = this.mob.position().subtract(other.position());
            double distance = toOther.length();
            if (distance < (double)2.0F && distance > (double)0.0F) {
                repulsionVector = repulsionVector.add(toOther.normalize().scale((double)0.5F / distance));
            }
        }

        if (target != null) {
            Vec3 targetPos = target.position().add(0,1,0);
            Vec3 ourPos = this.mob.position();
            Vec3 directionToTarget = targetPos.subtract(ourPos);
            double distance = directionToTarget.length();
            if (distance < this.minDistance) {
                desiredVelocity = directionToTarget.normalize().reverse().scale(this.backingSpeed);
            } else if (distance > this.maxDistance + this.bufferZone) {
                desiredVelocity = directionToTarget.normalize().scale(this.maxSpeed);
            } else if (distance < this.minDistance - this.bufferZone) {
                desiredVelocity = directionToTarget.normalize().reverse().scale(this.backingSpeed);
            }

            desiredVelocity = desiredVelocity.add(repulsionVector.scale(0.3));
            double var22 = 0.15;
            double deceleration = 0.88;
            this.currentVelocity = this.currentVelocity.scale(deceleration);
            Vec3 accelerationVec = desiredVelocity.subtract(this.currentVelocity).scale(var22);
            this.currentVelocity = this.currentVelocity.add(accelerationVec);
            double currentSpeed = this.currentVelocity.length();
            if (currentSpeed > this.maxSpeed) {
                this.currentVelocity = this.currentVelocity.normalize().scale(this.maxSpeed);
            }

            this.mob.setDeltaMovement(this.currentVelocity);
            double dx = target.getX() - this.mob.getX();
            double dz = target.getZ() - this.mob.getZ();
            this.targetYaw = (float)(Math.atan2(dz, dx) * (180D / Math.PI) - (double)90.0F);
            float yawDifference = Mth.wrapDegrees(this.targetYaw - this.currentYaw);
            float maxTurnSpeed = 9.0F;
            float turnAmount = Mth.clamp(yawDifference, -maxTurnSpeed, maxTurnSpeed);
            this.currentYaw = Mth.wrapDegrees(this.currentYaw + turnAmount);
            this.mob.setYRot(this.currentYaw);
            this.mob.yBodyRot = this.currentYaw;
            this.mob.yHeadRot = this.currentYaw;
        } else {
            this.handleIdleMovement();
        }
    }

    private void handleIdleMovement() {
        if (this.operation == Operation.MOVE_TO) {
            Vec3 direction = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
            double distance = direction.length();
            if (distance < (double)1.0F) {
                this.operation = Operation.WAIT;
                this.currentVelocity = this.currentVelocity.scale(0.85);
                this.mob.setDeltaMovement(this.currentVelocity);
            } else {
                Vec3 desiredVelocity = direction.normalize().scale(Math.min(this.maxSpeed * 0.7, distance * 0.2));
                this.currentVelocity = this.currentVelocity.scale(0.88).add(desiredVelocity.subtract(this.currentVelocity).scale(0.15));
                this.mob.setDeltaMovement(this.currentVelocity);
                if (this.currentVelocity.lengthSqr() > 0.001) {
                    this.targetYaw = (float)(Math.atan2(this.currentVelocity.z, this.currentVelocity.x) * (180D / Math.PI) - (double)90.0F);
                    float yawDifference = Mth.wrapDegrees(this.targetYaw - this.currentYaw);
                    this.currentYaw = Mth.wrapDegrees(this.currentYaw + Mth.clamp(yawDifference, -9.0F, 9.0F));
                    this.mob.setYRot(this.currentYaw);
                    this.mob.yBodyRot = this.currentYaw;
                    this.mob.yHeadRot = this.currentYaw;
                }
            }
        }

    }
}