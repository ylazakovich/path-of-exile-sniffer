package io.automation.controller;

import java.util.List;
import java.util.Objects;

import io.automation.entity.SkillEntity;
import io.automation.service.DatabaseService;
import io.automation.service.PoeNinjaService;
import io.automation.service.SkillsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/database")
public class DatabaseController {

  private final DatabaseService databaseService;
  private final SkillsService skillsService;
  private final PoeNinjaService poeNinjaService;

  public DatabaseController(DatabaseService databaseService,
                            SkillsService skillsService,
                            PoeNinjaService poeNinjaService) {
    this.databaseService = databaseService;
    this.skillsService = skillsService;
    this.poeNinjaService = poeNinjaService;
  }

  @GetMapping("/load/gems")
  public void loadGems() {
    databaseService.loadGems();
  }

  @Scheduled(cron = "* */30 * * * *")
  @GetMapping("/update/gems/prices")
  public void updatePricesGems() {
    // TODO: need to move it to Service;
    List<SkillEntity> pastState = skillsService.findAllSkills();
    poeNinjaService.getSkills().subscribe(data -> {
      pastState.forEach(pastPrice -> Objects.requireNonNull(data.getBody()).getLines().stream()
          .filter(currentPrice -> currentPrice.getName().equals(pastPrice.getName()) &&
              currentPrice.getVariant().equals(pastPrice.getVariant()))
          .findFirst().ifPresent(matchedEntity -> pastPrice.setChaosValue(matchedEntity.getChaosValue())));
      skillsService.saveAll(pastState);
    });
  }
}
