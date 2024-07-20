package io.starter.steps.telegram;

import com.codeborne.selenide.Selenide;
import io.starter.pages.telegram.SearchPage;
import io.starter.steps.models.PageSteps;
import io.starter.verifications.telegram.SearchPageVerification;

public class SearchPageSteps extends PageSteps<SearchPageSteps, SearchPageVerification, SearchPage> {

  public SearchPageSteps(SearchPage page) {
    super.verification = new SearchPageVerification(this, page);
    super.page = page;
  }

  public HomePageSteps selectChat(int index) {
    page.selectChat(index);
    return new HomePageSteps(Selenide.page());
  }
}
