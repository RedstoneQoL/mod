package tools.redstone.redstonetools.features.commands.update;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.commands.CommandFeature;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.WorldEditUtils;

@AutoService(AbstractFeature.class)
@Feature(name = "Update", description = "Forces block updates in the selected area.", command = "/update", worldedit = true)
public class UpdateFeature extends CommandFeature {
    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var selectionOrFeedback = WorldEditUtils.getSelection(source.getPlayer());
        if (selectionOrFeedback.right().isPresent()) {
            return selectionOrFeedback.right().get();
        }

        assert selectionOrFeedback.left().isPresent();
        var selection = selectionOrFeedback.left().get();


        return RegionUpdater.updateRegion(source.getWorld(), selection.getMinimumPoint(), selection.getMaximumPoint());
    }
}
