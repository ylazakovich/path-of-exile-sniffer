package io.automation.client;

import io.automation.dto.GemDTO;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class PoeNinjaClient {

  private final WebClient webClient;

  public PoeNinjaClient() {
    this.webClient = WebClient.builder()
        .baseUrl("https://poe.ninja")
        .exchangeStrategies(ExchangeStrategies
            .builder()
            .codecs(
                codecs -> codecs
                    .defaultCodecs()
                    .maxInMemorySize(8 * 100 * 1024 * 1024))
            .build())
        .build();
  }

  public Mono<GemDTO> getDataWithGems() {
    return webClient.get()
        .uri("/api/data/itemoverview?league=Ancestor&type=SkillGem")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(GemDTO.class);
  }
}
