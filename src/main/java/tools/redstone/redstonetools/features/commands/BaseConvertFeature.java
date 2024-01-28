package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import net.minecraft.server.command.ServerCommandSource;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.NumberArg;


import static tools.redstone.redstonetools.features.arguments.serializers.NumberBaseArgumentType.numberBase;
import static tools.redstone.redstonetools.features.arguments.serializers.NumberSerializer.numberArg;

@AutoService(AbstractFeature.class)
@Feature(name = "Base Convert", description = "Converts a number from one base to another.", command = "base")
public class BaseConvertFeature extends CommandFeature {

    public static final Argument<NumberArg> inputNum = Argument
            .ofType(numberArg());
    public static final Argument<Integer> toBase = Argument
            .ofType(numberBase())
            .withDefault(10);

    @Override
    protected Feedback execute(ServerCommandSource source) {
        var input = inputNum.getValue().toPrefixedString();
        var output = inputNum.getValue().toPrefixedString(toBase.getValue());

        return Feedback.success("{} = {}", input, output);
    }
}
