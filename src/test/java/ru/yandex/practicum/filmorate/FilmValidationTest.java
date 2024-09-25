package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    private Validator validator;
    private Film film;

    @BeforeEach
    void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void filmNameValidation() {
        film = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2020, 2, 20))
                .duration(60)
                .build();

        assertValidation(film);
    }

    @Test
    void filmMaxDescriptionValidation() {
        film = Film.builder()
                .name("Name")
                .description("Ma quande lingues coalesce, li grammatica del resultant lingue es plu simplic e regulari quam ti del coalescent lingues. Li nov lingua franca va esser plu simplic e regulari quam li existent Europan li")
                .releaseDate(LocalDate.of(2020, 2, 20))
                .duration(60)
                .build();

        assertValidation(film);
    }

    @Test
    void filmDurationValidation() {
        film = Film.builder()
                .id(1L)
                .name("Name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 12))
                .duration(-60)
                .mpa(new Mpa(1, "G"))
                .build();

        assertValidation(film);
    }

    private void assertValidation(Film film) {
        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        assertEquals(1, violationSet.size());
    }
}
