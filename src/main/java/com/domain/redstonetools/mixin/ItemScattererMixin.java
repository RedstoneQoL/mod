package com.domain.redstonetools.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.domain.redstonetools.RedstoneToolsGameRules.DO_CONTAINER_DROPS;

@Mixin(ItemScatterer.class)
public class ItemScattererMixin {
    @Inject(method = "spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void spawn(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (!world.getGameRules().getBoolean(DO_CONTAINER_DROPS)) ci.cancel();
    }
}
