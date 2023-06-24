package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.WorldEditUtils;

import java.math.BigInteger;
import java.util.Collections;

import static tools.redstone.redstonetools.features.arguments.serializers.BigIntegerSerializer.bigInteger;
import static tools.redstone.redstonetools.features.arguments.serializers.BlockStateArgumentSerializer.blockState;
import static tools.redstone.redstonetools.features.arguments.serializers.IntegerSerializer.integer;

@AutoService(AbstractFeature.class)
@Feature(name = "Binary Block Write", description = "Interprets your WorldEdit selection as a binary number.", command = "/write")
public class BinaryBlockWriteFeature extends CommandFeature {
    private static final BlockStateArgument DEFAULT_ON_ARG = new BlockStateArgument(
            Blocks.REDSTONE_BLOCK.getDefaultState(),
            Collections.emptySet(),
            null
    );

    private static final BlockStateArgument DEFAULT_OFF_ARG = new BlockStateArgument(
            Blocks.AIR.getDefaultState(),
            Collections.emptySet(),
            null
    );

    public static final Argument<Integer> bitCount = Argument
            .ofType(integer(1))
            .withDefault(8);

    public static final Argument<BlockStateArgument> onBlock = Argument
            .ofType(blockState())
            .withDefault(DEFAULT_ON_ARG);

    public static final Argument<BlockStateArgument> offBlock = Argument
            .ofType(blockState())
            .withDefault(DEFAULT_OFF_ARG);

    public static final Argument<BigInteger> number = Argument
            .ofType(bigInteger(BigInteger.valueOf(0)));

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var selectionOrFeedback = WorldEditUtils.getSelection(source.getPlayer());

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

        if (direction.getBlockX() + direction.getBlockY() + direction.getBlockZ() > 1) {
            return Feedback.invalidUsage("The selection must have 2 axis the same");
        }

        // if the selection ends on a bit and not the air, so it isn't a multiple of the bit count, this is true
        boolean edge = ((boundingBox.getVolume() - 1) / (bitCount.getValue() - 1) - 1 + boundingBox.getVolume()) % bitCount.getValue() == 0;

        if (boundingBox.getVolume() % bitCount.getValue() != 0 && !edge) {
            return Feedback.invalidUsage("The selection must be a multiple of the bit count or end on a bit");
        }

        if (boundingBox.getVolume() < bitCount.getValue()) {
            return Feedback.invalidUsage("The selection is too small to write the number");
        }

        var spacingVector = direction.multiply((int) (boundingBox.getVolume() / bitCount.getValue()));

        if (edge) {
            spacingVector = direction.multiply((int) ((boundingBox.getVolume() - 1) / (bitCount.getValue() - 1)));
        }

        var bits = number.getValue().toString(2);

        if (bits.length() > bitCount.getValue()) {
            return Feedback.invalidUsage("The number is too large to fit in the selection");
        }

        bits = "0".repeat(bitCount.getValue() - bits.length()) + bits;

        var stringIndex = 0;
        for (BlockVector3 point = pos1; boundingBox.contains(point); point = point.add(spacingVector)) {
            var pos = new BlockPos(point.getBlockX(), point.getBlockY(), point.getBlockZ());

            BlockStateArgument state = bits.charAt(stringIndex) == '1' ? onBlock.getValue() : offBlock.getValue();

            source.getPlayer().getWorld().setBlockState(pos, state.getBlockState());
            stringIndex++;
        }

        return Feedback.success("Wrote " + bits);
    }

}