package dev.revature.fantasy.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "week_score")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekScore {

    @EmbeddedId
    private WeekScoreId id;

    private Double score;
}
