package com.github.ivotesligte.mcgrapplinghookmod;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class GrapplingHookItem extends RangedWeaponItem implements Vanishable {
  public static final int TICKS_PER_SECOND = 20;
  public static final int RANGE = 20;
  public static final int USE_COOLDOWN = 10;
  public static final float PROJECTILE_VELOCITY = 6.0F;

  public GrapplingHookItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
    return false;
  }

  private void updateHookNbt(ItemStack stack, Integer hookId) {
    NbtCompound nbt = stack.getOrCreateNbt();
    nbt.putInt("hook", hookId == null ? 0 : hookId.intValue());
  }

  private void updateRemoveAirDragNbt(ItemStack stack, boolean removeAirDrag) {
    NbtCompound nbt = stack.getOrCreateNbt();
    nbt.putBoolean("removeAirDrag", removeAirDrag);
  }

  @Nullable
  private Entity getHook(ItemStack stack, World world) {
    int hookId = stack.getOrCreateNbt().getInt("hook");

    if (hookId == 0) {
      return null;
    }

    Entity hook = world.getEntityById(hookId);

    if (!(hook instanceof HookEntity)) {
      updateHookNbt(stack, null);
      return null;
    }
    return hook;
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (entity instanceof PlayerEntity player) {
      boolean removeAirDrag = stack.getOrCreateNbt().getBoolean("removeAirDrag");
      removeAirDrag &= !player.groundCollision;
      updateRemoveAirDragNbt(stack, removeAirDrag);

      if (player.getStackInHand(player.getActiveHand()) == stack && removeAirDrag) {
        player.setVelocity(player.getVelocity().multiply(1.0 / 0.98)); // counter air drag
      }
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (user instanceof PlayerEntity playerEntity) {
      Entity hook = getHook(stack, world);

      if (!world.isClient) {
        stack.damage(1, playerEntity, (p) -> {
          p.sendToolBreakStatus(playerEntity.getActiveHand());
        });
        if (hook != null) {
          hook.discard();
          updateHookNbt(stack, null);
        }
      }

      playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
      playerEntity.getItemCooldownManager().set(this, USE_COOLDOWN);
    }
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack itemStack = user.getStackInHand(hand);

    if (getHook(itemStack, world) != null || user.hasVehicle()) {
      MCGrapplingHookMod.LOGGER.info("hook was not null");

      return TypedActionResult.pass(itemStack);
    }

    user.setCurrentHand(hand);

    if (!world.isClient) {
      HookEntity hookEntity = new HookEntity(MCGrapplingHookMod.HOOK, user, world);
      hookEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F,
          PROJECTILE_VELOCITY, 1.0F);

      world.spawnEntity(hookEntity);
      updateHookNbt(itemStack, hookEntity.getId());// TODO: use UUID after figuring out how to get an entity by UUID
      updateRemoveAirDragNbt(itemStack, !user.groundCollision);
    }

    world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(),
        SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

    return TypedActionResult.success(itemStack, world.isClient());
  }

  public Predicate<ItemStack> getProjectiles() {
    return (stack) -> false;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 72000;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  public static float getPullProgress(int useTicks) {
    float f = (float) useTicks / 20.0F;
    f = f * f + f * 2.0F;
    if (f > 1.0F) {
      f = 1.0F;
    }

    return f;
  }

  public int getRange() {
    return RANGE;
  }
}
