package tools.redstone.redstonetools.features.commands;

import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.BlockInfo;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;

public abstract class BlockRaycastFeature extends CommandFeature {
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            throw new CommandSyntaxException(null, Text.of("This command is client-side only."));
        }

        if (client.crosshairTarget == null || client.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            if (requiresBlock()) {
                return Feedback.invalidUsage("You must be looking at a block to use this command.");
            } else {
                return execute(source, null);
            }
        }

        var blockPos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
        var blockState = client.world.getBlockState(blockPos);
        var blockEntity = client.world.getBlockEntity(blockPos);
        var block = blockState.getBlock();

        return execute(source, new BlockInfo(block, blockPos, blockState, blockEntity));
    }

    protected boolean requiresBlock() {
        return true;
    }

    protected abstract Feedback execute(ServerCommandSource source, @Nullable BlockInfo blockInfo) throws CommandSyntaxException;
}
