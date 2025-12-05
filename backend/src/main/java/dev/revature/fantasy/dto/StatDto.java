package dev.revature.fantasy.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class StatDto {
    @NonNull
    private String userName;

    @NonNull
    private ScoreDto score;
}
