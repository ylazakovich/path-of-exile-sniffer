package io.automation.model.handler;

import io.automation.service.MenuService;
import io.automation.cash.BotStateCash;
import io.automation.model.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandler {

  private final BotStateCash botStateCash;
  private final MenuService menuService;

  @Autowired
  public CallbackQueryHandler(BotStateCash botStateCash,
                              MenuService menuService) {
    this.botStateCash = botStateCash;
    this.menuService = menuService;
  }

  public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
    final long chatId = buttonQuery.getMessage().getChatId();
    final long userId = buttonQuery.getFrom().getId();
    BotApiMethod<?> callBackAnswer = null;
    String data = buttonQuery.getData();
    switch (data) {
      case "Skills":
        callBackAnswer = new SendMessage(String.valueOf(chatId), "TODO...");
        botStateCash.saveState(userId, State.SKILLS_WAIT_EVENT);
        break;
      case "TODO":
        break;
    }
    return callBackAnswer;
  }
}