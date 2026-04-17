package net.zincstudios.scgextra.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

public class ModParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<SimpleParticleType> COPPER_FIRE_BALL = PARTICLE_TYPES
            .register("copper_fire_ball", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> COPPER_FLAME = PARTICLE_TYPES
            .register("copper_flame", () -> new SimpleParticleType(true));


    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }
}
