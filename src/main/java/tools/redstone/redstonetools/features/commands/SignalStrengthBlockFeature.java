package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.arguments.serializers.SignalBlockSerializer;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.SignalBlock;

import java.util.Random;

import static tools.redstone.redstonetools.features.arguments.serializers.IntegerSerializer.integer;

@AutoService(AbstractFeature.class)
@Feature(name = "Signal Strength Block", description = "Creates a block with the specified signal strength.", command = "ssb")
public class SignalStrengthBlockFeature extends CommandFeature {

    public static final Argument<Integer> signalStrength = Argument
            .ofType(integer(0));

    public static final Argument<SignalBlock> block = Argument
            .ofType(SignalBlockSerializer.signalBlock())
            .withDefault(SignalBlock.AUTO);

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        try {
            ItemStack itemStack = block.getValue().getItemStack(signalStrength.getValue());
            source.getPlayerOrThrow().giveItemStack(itemStack);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Feedback.error(e.getMessage());
        }

        //funny
        if(signalStrength.getValue() == 0) {
            String[] funny = {
                    "Why would you want this??", "Wtf are you going to use this for?", "What for?",
                    "... Ok, if you're sure.", "I'm 99% sure you could just use any other block.",
                    "This seems unnecessary.", "Is that a typo?", "Do you just like the glint?",
                    "Wow, what a fancy but otherwise useless barrel.", "For decoration?"};
            return Feedback.success(funny[new Random().nextInt(funny.length)]);
        }

        return Feedback.none();
    }

}
