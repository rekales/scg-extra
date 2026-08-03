package net.zincstudios.scgextra.client.renderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.zincstudios.scgextra.block.wreckerturret.WreckerTurretBlockEntity;
import net.zincstudios.scgextra.client.model.WreckerTurretWeaponModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WreckerTurretBlockRenderer extends GeoBlockRenderer<WreckerTurretBlockEntity> {

    public WreckerTurretBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WreckerTurretWeaponModel());
    }

    @Override
    public boolean shouldRenderOffScreen(WreckerTurretBlockEntity blockEntity) {
        return true;
    }
}
