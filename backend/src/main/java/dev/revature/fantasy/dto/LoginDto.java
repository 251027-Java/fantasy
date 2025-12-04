package dev.revature.fantasy.dto;

import lombok.Data;
import lombok.NonNull;


@Data
public class LoginDto {
    @NonNull
    private String userId;
    @NonNull
    private LeagueDto[] leagues;
}
