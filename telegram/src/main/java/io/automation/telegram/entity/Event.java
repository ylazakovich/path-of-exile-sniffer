package io.automation.telegram.entity;

import io.automation.telegram.model.EventFreq;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "user_events")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_id", columnDefinition = "serial")
  public int eventId;

  @Column(name = "time")
  @NotNull(message = "Need date!")
  public Date date;

  @Column(name = "description")
  @Size(min = 4, max = 200, message = "Description must be between 0 and 200 chars!")
  public String description;

  @Column(name = "event_freq", columnDefinition = "TIME")
  @Enumerated(EnumType.STRING)
  public EventFreq freq;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  public User user;

  public Event() {
  }

  public Event(int eventId,
               @NotNull(message = "Need date!") Date date,
               @Size(min = 4, max = 200, message = "Description must be between 0 and 200 chars!")
               String description,
               EventFreq freq, User user) {
    this.eventId = eventId;
    this.date = date;
    this.description = description;
    this.freq = freq;
    this.user = user;
  }
}