package dev.revature.fantasy.dto;

public record RosterUserDto(
        Integer rosterId,
        String userId,
        String leagueId,
        Integer wins,
        Integer ties,
        Integer losses,
        Integer fptsDecimal,
        Integer fptsAgainstDecimal,
        Integer fptsAgainst,
        Integer fpts) {}
