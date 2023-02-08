package com.domain.redstonetools.features.commands.quicktp;

import com.domain.redstonetools.features.AbstractFeature;
import com.domain.redstonetools.features.commands.PrimaryCommand;
import com.domain.redstonetools.features.commands.TestFeatureComponent;
import com.domain.redstonetools.utils.PositionUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.domain.redstonetools.utils.RaycastUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class QuickTpFeature extends AbstractFeature
        implements PrimaryCommand, TestFeatureComponent {

    public QuickTpFeature() {
        super("quicktp", "Quick TP");
    }

    @Override
    public void configurePrimaryCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.executes(context -> {
            var player = ((ServerCommandSource) context.getSource()).getPlayer();

            var targetPosition = getTargetPosition(player);

            player.teleport(targetPosition.x, targetPosition.y, targetPosition.z);

            return Command.SINGLE_SUCCESS;
        });
    }

    // TODO: Test this
    private Vec3d getTargetPosition(PlayerEntity player) {
        var hit = player.raycast(64, 0, false);

        return clampHitPosition(hit);
    }

    // TODO: Test this
    private Vec3d clampHitPosition(HitResult hit) {
        if (!(hit instanceof BlockHitResult blockHit)) {
            return hit.getPos();
        }

        return PositionUtils.getFloorOfBlock(RaycastUtils.getBlockHitNeighbor(blockHit).getBlockPos());
    }

    @Override
    public String getPrimaryCommandName() {
        return "quicktp";
    }

}
