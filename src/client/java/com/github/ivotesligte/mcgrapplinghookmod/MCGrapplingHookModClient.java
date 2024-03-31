package com.github.ivotesligte.mcgrapplinghookmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MCGrapplingHookModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // This entrypoint is suitable for setting up client-specific logic, such as
    // rendering.
    EntityRendererRegistry.register(MCGrapplingHookMod.HOOK,
        (context) -> new HookEntityRenderer(context));
    ModModelPredicateProvider.registerModModels();

    MCGrapplingHookMod.LOGGER.info("Initialized client.");
  }
}
