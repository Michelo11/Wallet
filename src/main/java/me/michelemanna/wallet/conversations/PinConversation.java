package me.michelemanna.wallet.conversations;

import me.michelemanna.wallet.WalletPlugin;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PinConversation extends StringPrompt {
    private final int pin;
    private final UUID cardOwner;

    public PinConversation(int pin, UUID cardOwner) {
        this.pin = pin;
        this.cardOwner = cardOwner;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return WalletPlugin.getInstance().getMessage("conversations.atm.pin.prompt");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (!String.valueOf(pin).equals(input)) {
            context.getForWhom().sendRawMessage(WalletPlugin.getInstance().getMessage("conversations.atm.pin.invalid"));
            return END_OF_CONVERSATION;
        }

        return new AmountConversation(cardOwner);
    }
}