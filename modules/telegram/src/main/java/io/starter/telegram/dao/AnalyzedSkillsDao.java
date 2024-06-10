package io.starter.telegram.dao;

import java.util.ArrayList;
import java.util.List;

import io.starter.telegram.model.aggregator.Skill;
import io.starter.telegram.entity.AnalyzedSkillEntity;
import io.starter.telegram.repo.AnalyzedSkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AnalyzedSkillsDao {

  private final AnalyzedSkillsRepository analyzedSkillsRepository;

  @Autowired
  public AnalyzedSkillsDao(AnalyzedSkillsRepository analyzedSkillsRepository) {
    this.analyzedSkillsRepository = analyzedSkillsRepository;
  }

  public List<Skill> findAll() {
    List<AnalyzedSkillEntity> all = analyzedSkillsRepository.findAll(Sort.by(Sort.Direction.DESC, "profit"));
    return all.stream().map(entity -> {
      Skill skill = new Skill();
      skill.setName(entity.getName());
      skill.setCraftCost(entity.getCraftCost());
      skill.setProfit(entity.getProfit());
      return skill;
    }).toList();
  }

  public void add(List<Skill> skills) {
    if (analyzedSkillsRepository.findAll().isEmpty()) {
      final List<AnalyzedSkillEntity> entities = skills.stream()
          .map(skill -> {
            AnalyzedSkillEntity analyzedSkillEntity = new AnalyzedSkillEntity();
            analyzedSkillEntity.setName(skill.getName());
            analyzedSkillEntity.setCraftCost(skill.getCraftCost());
            analyzedSkillEntity.setProfit(skill.getProfit());
            return analyzedSkillEntity;
          }).toList();
      analyzedSkillsRepository.saveAll(entities);
    }
  }

  public void update(List<Skill> skills) {
    List<AnalyzedSkillEntity> entitiesOnUpdate = analyzedSkillsRepository.findAll();
    skills.forEach(skill ->
        entitiesOnUpdate.stream()
            .filter(entity -> entity.getName().equals(skill.getName()))
            .findFirst()
            .ifPresent(matchedEntity -> {
                  matchedEntity.setCraftCost(skill.getCraftCost());
                  matchedEntity.setProfit(skill.getProfit());
                }
            )
    );
    analyzedSkillsRepository.saveAll(entitiesOnUpdate);
  }

  public void addNew(List<Skill> skills) {
    List<AnalyzedSkillEntity> allEntities = analyzedSkillsRepository.findAll();
    List<AnalyzedSkillEntity> entitiesOnAdding = new ArrayList<>();
    skills.stream()
        .filter(skill -> allEntities.stream().noneMatch(entity -> entity.getName().equals(skill.getName())))
        .forEach(skill -> {
          AnalyzedSkillEntity analyzedSkillEntity = new AnalyzedSkillEntity();
          analyzedSkillEntity.setName(skill.getName());
          analyzedSkillEntity.setCraftCost(skill.getCraftCost());
          analyzedSkillEntity.setProfit(skill.getProfit());
          entitiesOnAdding.add(analyzedSkillEntity);
        });
    analyzedSkillsRepository.saveAll(entitiesOnAdding);
  }
}
