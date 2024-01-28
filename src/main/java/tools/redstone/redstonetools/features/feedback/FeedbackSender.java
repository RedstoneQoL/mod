package tools.redstone.redstonetools.features.feedback;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import javax.inject.Singleton;
import java.util.function.Supplier;

@Singleton
public class FeedbackSender extends AbstractFeedbackSender {
    @Override
    public void sendFeedback(ServerCommandSource source, Feedback feedback) {
        if (feedback.getType() == FeedbackType.NONE) {
            return;
        }
        Supplier<Text> feedbackSupplier = () -> Text.literal(feedback.getMessage())
                .formatted(feedback.getFormatting());

        source.sendFeedback(feedbackSupplier, false);
    }
}
