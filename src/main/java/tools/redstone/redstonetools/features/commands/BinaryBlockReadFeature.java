package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.WorldEditUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;

import static tools.redstone.redstonetools.features.arguments.serializers.BlockStateArgumentType.blockState;
import static tools.redstone.redstonetools.features.arguments.serializers.BoolSerializer.bool;
import static tools.redstone.redstonetools.features.arguments.serializers.IntegerSerializer.integer;
import static tools.redstone.redstonetools.features.arguments.serializers.NumberBaseArgumentType.numberBase;


@AutoService(AbstractFeature.class)
@Feature(name = "Binary Block Read", description = "Interprets your WorldEdit selection as a binary number.", command = "/read", worldedit = true)
public class BinaryBlockReadFeature extends CommandFeature {
    private static final BlockStateArgument LIT_LAMP_ARG = new BlockStateArgument(
            Blocks.REDSTONE_LAMP.getDefaultState().with(RedstoneLampBlock.LIT, true),
            Collections.singleton(RedstoneLampBlock.LIT),
            null);

    public static final Argument<Integer> offset = Argument
            .ofType(integer(1))
            .withDefault(2);
    public static final Argument<BlockStateArgument> onBlock = Argument
            .ofType(blockState(REGISTRY_ACCESS))
            .withDefault(LIT_LAMP_ARG);
    public static final Argument<Integer> toBase = Argument
            .ofType(numberBase())
            .withDefault(10);
    public static final Argument<Boolean> reverseBits = Argument
            .ofType(bool())
            .withDefault(false);

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var selectionOrFeedback = WorldEditUtils.getSelection(source.getPlayerOrThrow());

        if (selectionOrFeedback.right().isPresent()) {
            return selectionOrFeedback.right().get();
        }

        assert selectionOrFeedback.left().isPresent();
        var selection = selectionOrFeedback.left().get();

        var boundingBox = selection.getBoundingBox();
        var pos1 = boundingBox.getPos1();
        var pos2 = boundingBox.getPos2();
        var direction = pos2.subtract(pos1).normalize();

        // prevent infinite loop
        if (direction.lengthSq() == 0) {
            direction = BlockVector3.at(0, 0, 1);
        }

        var spacingVector = direction.multiply(offset.getValue());

        if (direction.getBlockX() + direction.getBlockY() + direction.getBlockZ() > 1) {
            return Feedback.invalidUsage("The selection must have 2 axis the same.");
        }

        var bits = new StringBuilder();
        for (BlockVector3 point = pos1; boundingBox.contains(point); point = point.add(spacingVector)) {
            var pos = new BlockPos(point.getBlockX(), point.getBlockY(), point.getBlockZ());
            var actualState = source.getWorld().getBlockState(pos);

            var matches = actualState.getBlock() == onBlock.getValue().getBlockState().getBlock();
            if (matches) {
                for (var property : onBlock.getValue().getProperties()) {
                    var propertyValue = onBlock.getValue().getBlockState().get(property);

                    if (!actualState.get(property).equals(propertyValue)) {
                        matches = false;
                        break;
                    }
                }
            }

            bits.append(matches ? 1 : 0);
        }

        if (reverseBits.getValue()) {
            bits.reverse();
        }

        var output = Integer.toString(Integer.parseInt(bits.toString(), 2), toBase.getValue());
        return Feedback.success("{}.", output);
    }

}
