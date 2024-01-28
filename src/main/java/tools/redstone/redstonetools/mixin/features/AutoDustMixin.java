package tools.redstone.redstonetools.mixin.features;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tools.redstone.redstonetools.RedstoneToolsClient;
import tools.redstone.redstonetools.features.toggleable.AutoDustFeature;
import tools.redstone.redstonetools.utils.ColoredBlock;

@Mixin(Block.class)
public abstract class AutoDustMixin {
    private AutoDustFeature autoDustFeature;

    private AutoDustFeature getAutoDustFeature() {
        if (autoDustFeature == null) {
            autoDustFeature = RedstoneToolsClient.INJECTOR.getInstance(AutoDustFeature.class);
        }

        return autoDustFeature;
    }

    @Inject(method = "onPlaced", at = @At("TAIL"))
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack,
            CallbackInfo ci) {
        if (!getAutoDustFeature().isEnabled()) {
            return;
        }

        var dustPos = pos.up();
        var block = world.getBlockState(pos).getBlock();
        var blockAbove = world.getBlockState(dustPos).getBlock();

        if (!blockAbove.equals(Blocks.AIR) || ColoredBlock.fromBlock(block) == null) {
            return;
        }

        ItemPlacementContext context = new ItemPlacementContext((PlayerEntity) placer, Hand.MAIN_HAND,
                new ItemStack(Items.REDSTONE), new BlockHitResult(
                        new Vec3d(dustPos.getX(), dustPos.getY(), dustPos.getZ()), Direction.UP, dustPos, false));
        placer.getWorld().setBlockState(dustPos, Blocks.REDSTONE_WIRE.getPlacementState(context));
    }
}
