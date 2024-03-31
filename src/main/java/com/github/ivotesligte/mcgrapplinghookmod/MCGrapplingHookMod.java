package com.github.ivotesligte.mcgrapplinghookmod;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MCGrapplingHookMod implements ModInitializer {
  public static String MODID = "mcgrapplinghookmod";

  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("mcgrapplinghookmod");

  public static final GrapplingHookItem GRAPPLING_HOOK = Registry.register(Registries.ITEM,
      new Identifier(MODID, "grappling_hook"),
      new GrapplingHookItem(new FabricItemSettings().maxCount(1)));

  public static final EntityType<HookEntity> HOOK = Registry.register(Registries.ENTITY_TYPE,
      new Identifier(MODID, "hook"), FabricEntityTypeBuilder
          .<HookEntity>create(SpawnGroup.MISC, HookEntity::new)
          .dimensions(EntityDimensions.fixed(0.5F, 0.5F)).trackRangeChunks(4)
          .trackedUpdateRate(20).build());

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Mod initialized.");
  }
}
