package tools.redstone.redstonetools.utils;

import tools.redstone.redstonetools.features.feedback.Feedback;
import com.mojang.datafixers.util.Either;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Consumer;

public class WorldEditUtils {
    /**
     * Execute a function for each block in
     * the provided region.
     *
     * Iterates the bounding box of the region
     * and checks if the position is contained
     * for each block.
     *
     * TODO: maybe make an async version of this somehow
     *
     * @param region   The region.
     * @param consumer The function to run.
     */
    public static void forEachBlockInRegion(Region region,
            Consumer<BlockVector3> consumer) {
        if (!DependencyLookup.WORLDEDIT_PRESENT) {
            throw new IllegalStateException("WorldEdit is not loaded.");
        }

        CuboidRegion bb = region.getBoundingBox();
        BlockVector3 min = bb.getMinimumPoint();
        BlockVector3 max = bb.getMaximumPoint();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    BlockVector3 vec = BlockVector3.at(x, y, z);
                    if (!region.contains(vec))
                        continue;
                    consumer.accept(vec);
                }
            }
        }
    }

    public static Either<Region, Feedback> getSelection(ServerPlayerEntity player) {
        if (!DependencyLookup.WORLDEDIT_PRESENT) {
            throw new IllegalStateException("WorldEdit is not loaded.");
        }

        var actor = FabricAdapter.adaptPlayer(player);

        var localSession = WorldEdit.getInstance()
                .getSessionManager()
                .get(actor);

        var selectionWorld = localSession.getSelectionWorld();

        try {
            return Either.left(localSession.getSelection(selectionWorld));
        } catch (IncompleteRegionException ex) {
            return Either.right(Feedback.invalidUsage("Please make a selection with WorldEdit first"));
        }
    }
}
