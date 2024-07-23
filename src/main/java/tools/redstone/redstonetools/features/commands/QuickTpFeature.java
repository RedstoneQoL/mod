package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.PositionUtils;
import tools.redstone.redstonetools.utils.RaycastUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import static tools.redstone.redstonetools.features.arguments.serializers.BoolSerializer.bool;
import static tools.redstone.redstonetools.features.arguments.serializers.FloatSerializer.floatArg;


@AutoService(AbstractFeature.class)
@Feature(name = "Quick TP", description = "Teleports you in the direction you are looking.", command = "quicktp")
public class QuickTpFeature extends CommandFeature {
    public static final Argument<Float> distance = Argument
            .ofType(floatArg(1.0f))
            .withDefault(50.0f);
    public static final Argument<Boolean> includeFluids = Argument
            .ofType(bool())
            .withDefault(false);

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var player = source.getPlayerOrThrow();

        var targetPosition = getTargetPosition(player);

        player.teleport(targetPosition.x, targetPosition.y, targetPosition.z, false);

        return Feedback.none();
    }

    private Vec3d getTargetPosition(PlayerEntity player) {
        // 8 chunks default, 16 blocks per chunk
        var renderDistanceBlocks = PlayerEntity.getRenderDistanceMultiplier() * 8 * 16;
        var hit = player.raycast(Math.min(distance.getValue(), renderDistanceBlocks), 0, includeFluids.getValue());

        return clampHitPosition(hit).subtract(0, 1.12, 0);
    }

    private Vec3d clampHitPosition(HitResult hit) {
        if (hit.getType() != HitResult.Type.BLOCK) {
            return hit.getPos().subtract(0, 0.5, 0);
        }

        var blockHit = (BlockHitResult) hit;

        var neighbor = RaycastUtils.getBlockHitNeighbor(blockHit);
        var neighborPos = neighbor.getBlockPos();

        return PositionUtils.getBottomPositionOfBlock(neighborPos);
    }
}
