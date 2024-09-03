package io.starter.mapper;

import java.util.List;
import java.util.function.Function;

import io.starter.entity.LeagueEntity;
import io.starter.entity.SkillEntity;
import io.starter.model.ninja.Lines;
import io.starter.model.ninja.Skill;

import org.springframework.stereotype.Service;

@Service
public class SkillEntityMapper implements Function<Lines<Skill>, List<SkillEntity>> {

  @Override
  public List<SkillEntity> apply(Lines<Skill> data) {
    return data.getLines().stream()
        .map(skill -> new SkillEntity(
            new LeagueEntity(),
            skill.getName(),
            skill.getVariant(),
            skill.isCorrupted(),
            skill.getGemLevel(),
            skill.getGemQuality(),
            skill.getChaosEquivalent(),
            0.0))
        .toList();
  }
}
