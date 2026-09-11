package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateChallengeEndDateRequest(
        @NotNull @FutureOrPresent
        LocalDate endDate
) {
}
