package dev.revature.fantasy.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;
import java.util.Optional;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WeekScoreId {
    @ManyToOne
    @JoinColumn(name = "roster_user_id", foreignKey = @ForeignKey(name = "fk_roster_user_id"))
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RosterUser rosterUser;

    @Column(name = "week_num")
    private Integer weekNum;

    @ToString.Include
    private Long rosterUserId() {
        return Optional.ofNullable(rosterUser).map(RosterUser::getId).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WeekScoreId that = (WeekScoreId) o;
        return Objects.equals(rosterUserId(), that.rosterUserId()) && Objects.equals(weekNum, that.weekNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rosterUserId(), weekNum);
    }
}
