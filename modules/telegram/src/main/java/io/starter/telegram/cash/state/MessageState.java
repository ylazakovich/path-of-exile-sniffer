package io.starter.telegram.cash.state;

import io.starter.telegram.config.Emoji;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageState {

  NO_CMD(""),
  WELCOME("/start"),
  START("%s Start".formatted(Emoji.YELLOW_RIBBON)),
  SETTINGS("%s Settings".formatted(Emoji.GEAR)),
  FEEDBACK("%s Feedback".formatted(Emoji.PERFORMING_ARTS));

  public final String value;

  public static MessageState byText(String text) {
    for (MessageState state : MessageState.values()) {
      if (state.value.equalsIgnoreCase(text)) {
        return state;
      }
    }
    return WELCOME;
  }
}
