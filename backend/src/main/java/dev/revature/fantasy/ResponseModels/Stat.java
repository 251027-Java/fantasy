package dev.revature.fantasy.ResponseModels;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stat {
    private String userName;
    private Score  score;
}
