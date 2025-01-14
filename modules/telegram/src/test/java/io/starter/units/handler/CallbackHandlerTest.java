package io.starter.units.handler;

import java.util.List;
import java.util.stream.IntStream;

import io.starter.dataproviders.CallbackHandlerProvider;
import io.starter.telegram.cash.CallbackCash;
import io.starter.telegram.cash.MessageCash;
import io.starter.telegram.cash.state.CallbackState;
import io.starter.telegram.constants.Constants;
import io.starter.telegram.dao.SkillDao;
import io.starter.telegram.dao.UserDao;
import io.starter.telegram.entity.LeagueEntity;
import io.starter.telegram.handler.UpdateHandler;
import io.starter.telegram.model.aggregator.Skill;
import io.starter.telegram.model.telegram.TelegramFacade;
import io.starter.telegram.service.CallbackAnswerService;
import io.starter.telegram.service.MessageAnswerService;

import net.datafaker.Faker;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CallbackHandlerTest {

  private final SkillDao skillDao = mock(SkillDao.class);
  private final UserDao userDao = mock(UserDao.class);
  private final MaybeInaccessibleMessage message = mock(MaybeInaccessibleMessage.class);
  private final CallbackQuery callbackQuery = mock(CallbackQuery.class);
  private final Update update = mock(Update.class);
  private final User user = mock(User.class);

  private final CallbackAnswerService callbackAnswerService = spy(new CallbackAnswerService(skillDao, userDao));
  private final MessageAnswerService messageAnswerService = spy(MessageAnswerService.class);
  private final CallbackCash callbackCash = spy(CallbackCash.class);
  private final MessageCash messageCash = spy(MessageCash.class);

  private final Faker faker = new Faker();

  @BeforeClass
  void setUp() {
    when(update.hasCallbackQuery()).thenReturn(true);
    when(update.getCallbackQuery()).thenReturn(callbackQuery);
    when(callbackQuery.getFrom()).thenReturn(user);
    when(callbackQuery.getMessage()).thenReturn(message);
    doNothing().when(userDao).saveWhenNotExist(user);
    doNothing().when(userDao).saveLastMessageId(user, message);
  }

  @Test(description = "Bot should react on clicking button 'skills'")
  void testWhenUserClickBtnSkills() {
    UpdateHandler handler = spy(new UpdateHandler(messageAnswerService, callbackAnswerService, userDao));
    TelegramFacade bot = spy(new TelegramFacade(handler, callbackCash, messageCash));

    when(callbackQuery.getData()).thenReturn(CallbackState.SKILLS.value);
    BotApiMethod<?> botApiMethod = bot.handleOnUpdate(update);

    EditMessageText expected = messageAnswerService.onClickSkills(message);
    EditMessageText actual = (EditMessageText) botApiMethod;
    assertThat(botApiMethod.getMethod()).isEqualTo(EditMessageText.PATH);
    assertThat(actual.getText()).isEqualTo(Constants.Start.SKILLS_GUIDE);
    assertThat(actual).isEqualTo(expected);
  }

  @Test(description = "Bot should react on clicking button 'skills'",
      dataProviderClass = CallbackHandlerProvider.class,
      dataProvider = "whenUserInSettingsMenu")
  void testUserInteractionInSettingsMenu(CallbackState state) {
    UpdateHandler handler = spy(new UpdateHandler(messageAnswerService, callbackAnswerService, userDao));
    TelegramFacade bot = spy(new TelegramFacade(handler, callbackCash, messageCash));
    String callbackQueryId = String.valueOf(faker.number().positive());

    when(callbackQuery.getData()).thenReturn(state.value);
    when(callbackQuery.getId()).thenReturn(callbackQueryId);
    BotApiMethod<?> botApiMethod = bot.handleOnUpdate(update);

    AnswerCallbackQuery expected = callbackAnswerService.onClickSetting(callbackQuery);
    AnswerCallbackQuery actual = (AnswerCallbackQuery) botApiMethod;
    assertThat(botApiMethod.getMethod()).isEqualTo(AnswerCallbackQuery.PATH);
    assertThat(actual.getText()).isEqualTo(Constants.Settings.SETTINGS_UPDATED);
    assertThat(actual).isEqualTo(expected);
  }

  @Test(description = "Bot should react on clicking button 'skills'",
      dataProviderClass = CallbackHandlerProvider.class,
      dataProvider = "whenUserInSkillsMenu")
  void testUserInteractionInSkillsMenu(CallbackState state) {
    LeagueEntity leagueEntity = mock(LeagueEntity.class);
    UpdateHandler handler = spy(new UpdateHandler(messageAnswerService, callbackAnswerService, userDao));
    TelegramFacade bot = spy(new TelegramFacade(handler, callbackCash, messageCash));
    String callbackQueryId = String.valueOf(faker.number().positive());
    List<Skill> skills = IntStream.range(0, 20)
        .mapToObj(i -> Skill.builder()
            .name(faker.harryPotter().spell())
            .chaosEquivalentProfit(faker.number().numberBetween(5, 100))
            .build())
        .toList();
    int lastPage = (int) Math.ceil((double) skills.size() / 10);
    int page = faker.number().numberBetween(1, lastPage);

    when(callbackQuery.getData()).thenReturn(state.value);
    when(callbackQuery.getId()).thenReturn(callbackQueryId);
    when(userDao.readLeague(user)).thenReturn(leagueEntity);
    when(skillDao.readAll(leagueEntity)).thenReturn(skills);
    when(userDao.readSkillPage(user)).thenReturn(page);
    doNothing().when(userDao).saveSkillPage(user, page);
    BotApiMethod<?> botApiMethod = bot.handleOnUpdate(update);

    EditMessageText expected = callbackAnswerService.onClickSkills(callbackQuery);
    EditMessageText actual = (EditMessageText) botApiMethod;
    assertThat(botApiMethod.getMethod()).isEqualTo(EditMessageText.PATH);
    assertThat(actual).isEqualTo(expected);
  }

  @Test(description = "Bot should ignore not supporting interaction")
  void testWhenUserMadeUnknownInteraction() {
    UpdateHandler handler = spy(new UpdateHandler(messageAnswerService, callbackAnswerService, userDao));
    TelegramFacade bot = spy(new TelegramFacade(handler, callbackCash, messageCash));
    String text = faker.text().text();

    when(callbackQuery.getData()).thenReturn(text);
    BotApiMethod<?> botApiMethod = bot.handleOnUpdate(update);

    assertThat(botApiMethod).isNull();
  }
}
