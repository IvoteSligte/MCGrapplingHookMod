package com.github.ivotesligte.mcgrapplinghookmod;

import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

import net.minecraft.client.render.entity.EntityRendererFactory;

public class HookEntityRenderer extends ProjectileEntityRenderer<HookEntity> {
  public static Identifier TEXTURE = new Identifier(MCGrapplingHookMod.MODID, "textures/entity/projectiles/hook.png");

  public HookEntityRenderer(EntityRendererFactory.Context context) {
    super(context);
  }

  public Identifier getTexture(HookEntity entity) {
    return TEXTURE;
  }
}
