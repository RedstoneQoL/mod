package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.mixin.accessors.MinecraftClientAccessor;
import tools.redstone.redstonetools.utils.BlockInfo;
import tools.redstone.redstonetools.utils.BlockStateNbtUtil;


import static tools.redstone.redstonetools.utils.ItemUtils.addExtraNBTText;


@AutoService(AbstractFeature.class)
@Feature(name = "Copy State", description = "Gives you a copy of the block you're looking at with its BlockState.", command = "copystate")
public class CopyStateFeature extends PickBlockFeature {
    @Override
    protected Either<ItemStack, Feedback> getItemStack(ServerCommandSource source, BlockInfo blockInfo) {
        MinecraftClient client = MinecraftClient.getInstance();

        ItemStack itemStack = blockInfo.block.getPickStack(client.world, blockInfo.pos, blockInfo.state);

        if (blockInfo.state.hasBlockEntity()) {
            ((MinecraftClientAccessor) client).invokeAddBlockEntityNbt(itemStack, blockInfo.entity,
                    client.world.getRegistryManager());
        }

        int i = addBlockStateNbt(itemStack, blockInfo.state);
        if (i == -1) {
            return Either.right(Feedback.invalidUsage("This block doesn't have any BlockState!"));
        }

        return Either.left(itemStack);
    }

    private int addBlockStateNbt(ItemStack itemStack, BlockState blockState) {
        addExtraNBTText(itemStack, "BlockState");
        BlockStateNbtUtil.putPlacement(itemStack, blockState);
        return 1;
    }


}
