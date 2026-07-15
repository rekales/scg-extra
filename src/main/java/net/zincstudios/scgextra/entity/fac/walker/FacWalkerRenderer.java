package net.zincstudios.scgextra.entity.fac.walker;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class FacWalkerRenderer extends BaseEntityRenderer<FacWalkerEntity> {

    private static final String[] SMOKE_BONE_NAMES = {"left_smoke", "right_smoke"};

    private long lastTime = 0;

    public FacWalkerRenderer(EntityRendererProvider.Context context, GeoModel<FacWalkerEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, Map.of(
                0, "left_flash",
                1, "right_flash"
        )));
    }

    @Override
    public void render(FacWalkerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        this.addExhaustSmoke(entity);
    }

    private void addExhaustSmoke(FacWalkerEntity entity) {
        Level level = entity.level();
        if (this.lastTime == level.getGameTime()) return;
        this.lastTime = level.getGameTime();
        if (this.lastTime%4!=0 || entity.isSprinting() && this.lastTime%3!=0) return;

        for (String boneName : SMOKE_BONE_NAMES) {
            Optional<GeoBone> opt = this.getGeoModel().getBone(boneName);
            if (opt.isEmpty()) continue;

            Vector3d bonePos = opt.get().getWorldPosition();
            Matrix4f worldMatrix = opt.get().getModelRotationMatrix();
            Vector4f up = new Vector4f(0, 1, 0, 0);
            worldMatrix.transform(up);

            Vec3 pos = new Vec3(bonePos.x, bonePos.y, bonePos.z);
            Vec3 dir = new Vec3(up.x, up.y, up.z).normalize().scale(0.14);

            double posRand = 0.15;
            double dirRand = 0.1;

            level.addParticle(
                    ParticleTypes.SMOKE,
                    pos.x + (level.getRandom().nextDouble()-0.5) * posRand,
                    pos.y + (level.getRandom().nextDouble()-0.5) * posRand,
                    pos.z + (level.getRandom().nextDouble()-0.5) * posRand,
                    dir.x + (level.getRandom().nextDouble()-0.5) * dirRand,
                    dir.y + (level.getRandom().nextDouble()-0.5) * dirRand,
                    dir.z + (level.getRandom().nextDouble()-0.5) * dirRand
            );
        }
    }
}
