package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;
    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;
    @Size(message = "максимальная длина описания — 200 символов", max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private final Set<Long> likes = new HashSet<>();
}
