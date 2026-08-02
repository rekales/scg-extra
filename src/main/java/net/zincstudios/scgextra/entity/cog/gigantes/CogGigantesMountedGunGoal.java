package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.entity.cog.FixedMountedGunGoal;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.network.GunFlashMessage;
import net.zincstudios.scgextra.network.SCGEPacketHandler;
import top.ribs.scguns.ScorchedGuns;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogGigantesMountedGunGoal extends FixedMountedGunGoal<CogGigantesEntity> {

    private int firingTimeOut = 0;

    public CogGigantesMountedGunGoal(CogGigantesEntity mob, GunItem gunItem) {
        super(mob, gunItem);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.mob.isStunned();
    }

    @Override
    public void start() {
        super.start();
        this.firingTimeOut = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setFiring(false);
    }

    @Override
    public void tick() {
        if (this.firingTimeOut > 0) {
            this.mob.setFiring(true);
            this.firingTimeOut--;
        } else {
            this.mob.setFiring(false);
        }
        super.tick();
    }

    @Override
    protected void fireGun(LivingEntity target) {
        super.fireGun(target);
        this.firingTimeOut = this.burstInterval*2;

        // TODO: temp, replace later with reimplementation of mounted gun goal
        Gun.Display.Flash flash = ModItems.VALORA.get().getGun().getDisplay().getFlash();
        if (flash == null) return;
        ResourceLocation flashTexture = ResourceLocation.fromNamespaceAndPath(ScorchedGuns.MODID,
                "textures/effect/" + flash.getTextureLocation() + ".png");
        SCGEPacketHandler.sendToNearbyPlayers(() -> MobUtil.levelLocationFromEntity(this.mob),
                new GunFlashMessage(this.mob.getId(), 0, flashTexture, false, 1F));
    }
}
