package net.zincstudios.scgextra.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//copy from Sonic Boom
@OnlyIn(Dist.CLIENT)
public class LargeFlameParticle extends HugeExplosionParticle {
   protected LargeFlameParticle(ClientLevel level, double x, double y, double z, double quadSizeMultiplier, SpriteSet sprites) {
      super(level, x, y, z, quadSizeMultiplier, sprites);
      this.lifetime = 16;
      this.quadSize = 0.5F;
      this.setSpriteFromAge(sprites);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new LargeFlameParticle(level, x, y, z, xSpeed, this.sprites);
      }
   }
}