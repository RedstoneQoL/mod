package tools.redstone.redstonetools.features.commands;


import com.google.auto.service.AutoService;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.WorldEditUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@AutoService(AbstractFeature.class)
@Feature(command = "/minsel", description = "Removes all air-only layers from a selection", name = "Minimize Selection", worldedit = true)
public class MinSelectionFeature extends CommandFeature {

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var selectionOrFeedback = WorldEditUtils.getSelection(source.getPlayerOrThrow());
        if (selectionOrFeedback.right().isPresent()) {
            return selectionOrFeedback.right().get();
        }

        assert selectionOrFeedback.left().isPresent();
        var selection = selectionOrFeedback.left().get();
        var selectionWorld = selection.getWorld();

        var actor = FabricAdapter.adaptPlayer(source.getPlayerOrThrow());

        var localSession = WorldEdit.getInstance()
                .getSessionManager()
                .get(actor);

        var selector = localSession.getRegionSelector(selectionWorld);

        boolean isEmpty = true;
        for (BlockVector3 point : selection) {
            if (!selectionWorld.getBlock(point).equals(BlockTypes.AIR.getDefaultState()))
                isEmpty = false;
        }

        if (isEmpty) {
            return Feedback.invalidUsage("Cannot minimize empty selections.");
        }

        minimiseSelection(selectionWorld, selection);

        selector.learnChanges();
        selector.explainRegionAdjust(actor, localSession);

        return Feedback.success("Minimized selection.");
    }

    private void minimiseSelection(World selectionWorld, Region selection)
            throws CommandSyntaxException {
        List<BlockVector3> changes = new ArrayList<>();
        var faces = getFaces(selection);
        var finished = true;

        for (CuboidRegion face : faces) {
            var isOnlyAir = true;

            for (BlockVector3 point : face) {
                if (selectionWorld.getBlock(point).getBlockType().getDefaultState() != BlockTypes.AIR
                        .getDefaultState()) {
                    isOnlyAir = false;
                    break;
                }
            }

            if (!isOnlyAir)
                continue;

            var difference = selection.getCenter().subtract(face.getCenter());
            difference = difference.normalize();

            changes.add(difference.toBlockPoint());

            finished = false;

        }

        try {
            selection.contract(changes.toArray(new BlockVector3[changes.size()]));
        } catch (RegionOperationException e) {
            throw new SimpleCommandExceptionType(Text.of("There was an error modifying the region.")).create();
        }

        if (!finished)
            minimiseSelection(selectionWorld, selection);

    }

    private List<CuboidRegion> getFaces(Region selection) {
        var faces = new ArrayList<CuboidRegion>();

        var pos1 = selection.getBoundingBox().getPos1();
        var pos2 = selection.getBoundingBox().getPos2();

        var min = selection.getMinimumPoint();
        var max = selection.getMaximumPoint();

        faces.add(new CuboidRegion(pos1.withX(min.getX()), pos2.withX(min.getX())));
        faces.add(new CuboidRegion(pos1.withX(max.getX()), pos2.withX(max.getX())));

        faces.add(new CuboidRegion(pos1.withZ(min.getZ()), pos2.withZ(min.getZ())));
        faces.add(new CuboidRegion(pos1.withZ(max.getZ()), pos2.withZ(max.getZ())));

        faces.add(new CuboidRegion(pos1.withY(min.getY()), pos2.withY(min.getY())));
        faces.add(new CuboidRegion(pos1.withY(max.getY()), pos2.withY(max.getY())));

        return faces;
    }

}
