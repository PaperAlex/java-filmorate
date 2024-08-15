package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Вызываем список фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создаем новый фильм");
        // проверяем выполнение необходимых условий
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.trace("Создали новый фильм {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновляем фильм");
        if (newFilm.getId() == null) {
            log.warn("Не указан id фильма");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Указан неверный id");
            throw new ValidationException("Такого id фильма нет в списке");
        }

        log.info("Обновляем информацию о фильме");
        updateFilmValidation(newFilm);

        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            log.trace("Обновили фильм: {}", newFilm);
        }
        return newFilm;
    }

    private long getNextId() {
        log.info("Создаем id фильма");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void nameValidation(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private void releaseDateValidation(LocalDate releaseDate) {
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private void validateFilm(Film film) {
        nameValidation(film.getName());
        releaseDateValidation(film.getReleaseDate());
    }

    private void updateFilmValidation(Film newFilm) {
        newNameValidation(newFilm);
        newDescriptionValidation(newFilm);
        newReleaseDateValidation(newFilm);
        newDurationValidation(newFilm);
    }

    private void newNameValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        String name = newFilm.getName();
        if (name == null || name.isBlank() || name.isEmpty()) {
            newFilm.setName(oldFilm.getName());
            log.debug("Обновили имя фильма {}", newFilm.getId());
        }
    }

    private void newDescriptionValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        String description = newFilm.getDescription();
        if (description == null || description.isBlank() || description.isEmpty()) {
            newFilm.setDescription(oldFilm.getDescription());
            log.debug("Обновили описание {}", newFilm.getId());
        }
    }

    private void newReleaseDateValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        LocalDate releaseDate = newFilm.getReleaseDate();
        if (releaseDate == null) {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
            log.debug("Обновили дату выпуска {}", newFilm.getId());
        }
    }

    private void newDurationValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        Integer duration = newFilm.getDuration();
        if (duration == null) {
            newFilm.setDuration(oldFilm.getDuration());
            log.debug("Обновили продолжительность {}", newFilm.getId());
        }
    }
}
