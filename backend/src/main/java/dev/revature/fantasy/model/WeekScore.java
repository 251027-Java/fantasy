package dev.revature.fantasy.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "week_score")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeekScore {

    @EmbeddedId
    private WeekScoreId id;

    private Double score;
}
