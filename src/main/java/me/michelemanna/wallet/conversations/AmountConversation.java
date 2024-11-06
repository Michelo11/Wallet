package me.michelemanna.wallet.conversations;

import me.michelemanna.wallet.WalletPlugin;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AmountConversation extends NumericPrompt {
    private final UUID cardOwner;

    public AmountConversation(UUID cardOwner) {
        this.cardOwner = cardOwner;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return WalletPlugin.getInstance().getMessage("conversations.atm.amount.prompt");
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
        if (input.intValue() <= 0) {
            context.getForWhom().sendRawMessage(WalletPlugin.getInstance().getMessage("conversations.atm.amount.invalid"));
            return END_OF_CONVERSATION;
        }

        WalletPlugin.getInstance().withdraw((Player)context.getForWhom(), input.intValue(), cardOwner);

        return END_OF_CONVERSATION;
    }
}