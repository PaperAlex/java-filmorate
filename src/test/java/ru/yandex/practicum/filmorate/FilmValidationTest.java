package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    private Validator validator;
    private Film film;
    private FilmController controller;

    @BeforeEach
    void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new FilmController();
    }

    @Test
    void filmNameValidation() {
        film = Film.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2020, 2, 20))
                .duration(60)
                .build();

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        assertEquals(1, violationSet.size());
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void filmMaxDescriptionValidation() {
        film = Film.builder()
                .name("Name")
                .description("Ma quande lingues coalesce, li grammatica del resultant lingue es plu simplic e regulari quam ti del coalescent lingues. Li nov lingua franca va esser plu simplic e regulari quam li existent Europan li")
                .releaseDate(LocalDate.of(2020, 2, 20))
                .duration(60)
                .build();

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        assertEquals(1, violationSet.size());
        assertEquals("максимальная длина описания — 200 символов", violationSet.iterator().next().getMessage());
    }

    @Test
    void releaseDateValidation() {
        film = Film.builder()
                .name("Name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 12))
                .duration(60)
                .build();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void filmDurationValidation() {
        film = Film.builder()
                .name("Name")
                .description("description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(-60)
                .build();

        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        assertEquals(1, violationSet.size());
    }

    @Test
    void filmUpdateTest() {
        film = Film.builder()
                .name("5 name")
                .description("description")
                .releaseDate(LocalDate.of(1999, 1, 1))
                .duration(123)
                .build();

        controller.create(film);

        film = Film.builder()
                .id(1L)
                .name("")
                .description("Super description")
                .duration(123)
                .build();

        controller.update(film);

        assertEquals("5 name", film.getName());
        assertEquals("Super description", film.getDescription());
        assertEquals(1L, film.getId());

    }

    @Test
    void unknownFilmUpdateTest() {
        film = Film.builder()
                .name("5 name")
                .description("description")
                .releaseDate(LocalDate.of(1999, 1, 1))
                .duration(123)
                .build();

        controller.create(film);

        film = Film.builder()
                .id(2L)
                .name("")
                .description("Super description")
                .duration(123)
                .build();

        assertThrows(ValidationException.class, () -> controller.update(film));
    }
}
