package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.BlockColor;
import tools.redstone.redstonetools.utils.BlockInfo;
import tools.redstone.redstonetools.utils.ColoredBlock;
import com.mojang.datafixers.util.Either;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import tools.redstone.redstonetools.utils.ColoredBlockType;

import javax.annotation.Nullable;

import static tools.redstone.redstonetools.features.arguments.serializers.ColoredBlockTypeSerializer.coloredBlockType;

@AutoService(AbstractFeature.class)
@Feature(name = "Colored", description = "Gives the player specified variant of block being looked at, with the same color. Default is White.", command = "colored")
public class ColoredFeature extends PickBlockFeature {
    public static final Argument<ColoredBlockType> blockType = Argument.ofType(coloredBlockType());
    @Override
    protected boolean requiresBlock() {
        return false;
    }

    @Override
    protected Either<ItemStack, Feedback> getItemStack(ServerCommandSource source, @Nullable BlockInfo blockInfo) {
        var color = blockInfo == null
                ? BlockColor.WHITE
                : BlockColor.fromBlock(blockInfo.block);

        var coloredBlock = blockType.getValue().withColor(color);

        return Either.left(new ItemStack(coloredBlock.toBlock()));
    }
}
