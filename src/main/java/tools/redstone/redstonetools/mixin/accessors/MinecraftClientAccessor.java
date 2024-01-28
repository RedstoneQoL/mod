package tools.redstone.redstonetools.mixin.accessors;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {

    @Invoker("addBlockEntityNbt")
    void invokeAddBlockEntityNbt(ItemStack stack, BlockEntity blockEntity);

}
