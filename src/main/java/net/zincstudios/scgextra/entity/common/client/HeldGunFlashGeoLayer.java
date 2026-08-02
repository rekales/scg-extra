package net.zincstudios.scgextra.entity.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;
import top.ribs.scguns.ScorchedGuns;
import top.ribs.scguns.client.util.PropertyHelper;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.util.GunModifierHelper;

public class HeldGunFlashGeoLayer<T extends Mob & GeoAnimatable> extends GeoRenderLayer<T> {

    private final float gunTilt;

    public HeldGunFlashGeoLayer(GeoRenderer<T> renderer) {
        this(renderer, 0);
    }

    public HeldGunFlashGeoLayer(GeoRenderer<T> renderer, float gunTilt) {
        super(renderer);
        this.gunTilt = gunTilt;
    }

    @Override
    public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.isLeftHanded() && !bone.getName().equals("right_hand")) return;
        if (animatable.isLeftHanded() && !bone.getName().equals("left_hand")) return;

        if (!GunFlashHandler.hasFlashToRender(animatable.getId())) return;

        ItemStack stack = animatable.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem gunItem)) return;
        Gun gun = gunItem.getModifiedGun(stack);
        Gun.Display.Flash flash = gun.getDisplay().getFlash();
        if (flash == null) return;

        poseStack.pushPose();

        RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);

        poseStack.mulPose(Axis.XP.rotationDegrees(-90 + this.gunTilt));

        // These set of translations seems to make it work properly, most of the time
        // TODO: review base to check how they figure out how they position the flash
        Vec3 weaponOrigin = PropertyHelper.getModelOrigin(stack, PropertyHelper.GUN_DEFAULT_ORIGIN);
        Vec3 flashPosition = PropertyHelper.getMuzzleFlashPosition(stack, gun).subtract(weaponOrigin);

        RandomSource rand = RandomSource.create(animatable.level().getGameTime());
        flashPosition = flashPosition.add((rand.nextFloat()-0.5)*0.07, (rand.nextFloat()-0.5)*0.07, 0);

        poseStack.translate(weaponOrigin.x * 0.0625, weaponOrigin.y * 0.0625, weaponOrigin.z * 0.0625);
        poseStack.translate(flashPosition.x * 0.0625, flashPosition.y * 0.0625, flashPosition.z * 0.0625);
        poseStack.translate(flashPosition.x * 0.0625, flashPosition.y * 0.0625, flashPosition.z * 0.0625 * 1.25);
        float scale = (float)(PropertyHelper.getMuzzleFlashScale(stack, gun).x * GunModifierHelper.getMuzzleFlashScale(stack, 1.0));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        ResourceLocation flashTexture = ResourceLocation.fromNamespaceAndPath(ScorchedGuns.MODID,
                "textures/effect/" + flash.getTextureLocation() + ".png");
        MobRenderUtils.renderMuzzleFlash(poseStack, renderType, bufferSource,
                flashTexture, stack.isEnchanted(), rand, scale);

        poseStack.popPose();
    }
}
