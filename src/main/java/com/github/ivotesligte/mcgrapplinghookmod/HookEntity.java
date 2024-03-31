package com.github.ivotesligte.mcgrapplinghookmod;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HookEntity extends PersistentProjectileEntity {
  public static final Vec3d SWING_SPEEDUP = new Vec3d(1.08, 1.02, 1.08);
  public static final double NOFALL_THRESHOLD = -0.4;
  public static final double GRAVITY = -0.5 / 20.0;
  public static final double MAX_SPEED = 2.0;

  public HookEntity(EntityType<? extends HookEntity> entityType, World world) {
    super(entityType, world, ItemStack.EMPTY);
    this.pickupType = PickupPermission.DISALLOWED; // override pickup permission
    this.setDamage(0.5);
    this.setNoGravity(true);
  }

  public HookEntity(EntityType<? extends HookEntity> entityType, World world, double x, double y, double z) {
    this(entityType, world);
    this.setPosition(x, y, z);
  }

  public HookEntity(EntityType<? extends HookEntity> type, LivingEntity owner, World world) {
    this(type, world, owner.getX(), owner.getEyeY() - 0.10000000149011612, owner.getZ());
    this.setOwner(owner);
  }

  @Override
  public void setOwner(@Nullable Entity entity) {
    super.setOwner(entity);
    this.pickupType = PickupPermission.DISALLOWED; // setOwner for some reason changes pickupType
  }

  public void tick() {
    super.tick();

    if (this.getOwner() instanceof PlayerEntity player) {
      if (!(player.getActiveItem().getItem() instanceof GrapplingHookItem)) {
        this.discard();
        return;
      }

      if (this.inGround) {
        Vec3d playerPos = player.getPos();
        Vec3d playerVel = player.getVelocity();
        Vec3d anchorPos = this.getPos();
        Vec3d relativePos = playerPos.subtract(anchorPos);

        Vec3d nRelativePos = relativePos.normalize();
        Vec3d nPlayerVel = playerVel.normalize();

        Vec3d nNewVel = nPlayerVel.subtract(nRelativePos.multiply(nPlayerVel.dotProduct(nRelativePos))).normalize();
        Vec3d newVel = nNewVel.multiply(playerVel.length());

        // if new velocity moves player closer to hook
        if (newVel.dotProduct(nRelativePos) < playerVel.dotProduct(nRelativePos)) {
          // if player is moving slower than max velocity, give it a speedup
          player.setVelocity(player.speed < MAX_SPEED ? newVel.multiply(SWING_SPEEDUP) : newVel);
        }
        if (player.getVelocity().y > NOFALL_THRESHOLD) {
          player.fallDistance = 0.0F;
        }
      } else {
        // gravity
        this.addVelocity(0.0, GRAVITY, 0.0);
      }
    }
  }
}
