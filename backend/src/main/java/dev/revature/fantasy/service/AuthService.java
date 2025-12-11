package dev.revature.fantasy.service;

import dev.revature.fantasy.dto.AuthRequestDto;
import dev.revature.fantasy.dto.AuthResponseDto;

import java.util.Optional;

public interface AuthService {

    public Optional<AuthResponseDto> auth(AuthRequestDto authRequestDto);
}
