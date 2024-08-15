package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;
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
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Создание нового фильма id: {}", film.getId());
        // проверяем выполнение необходимых условий
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) throws ValidationException {
        if (newFilm.getId() == null) {
            log.warn("Ошибка валидации, id null недопустимо");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.error("Ошибка, id {} нет в списке", newFilm.getId());
            throw new ValidationException("Такого id фильма нет в списке");
        }

        log.debug("Обновление фильма id: {}", newFilm.getId());
        updateFilmValidation(newFilm);

        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            log.info("Обновлен фильм");
        }
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void nameValidation(String name) throws ValidationException {
        if (StringUtils.isBlank(name)) {
            log.warn("Ошибка валидации, название фильма null");
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private void releaseDateValidation(LocalDate releaseDate) throws ValidationException {
        if (releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации, указана дата до 28.12.1895");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private void validateFilm(Film film) throws ValidationException {
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
        if (StringUtils.isBlank(name)) {
            newFilm.setName(oldFilm.getName());
            log.info("Обновлено имя фильма");
        }
    }

    private void newDescriptionValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        String description = newFilm.getDescription();
        if (StringUtils.isBlank(description)) {
            newFilm.setDescription(oldFilm.getDescription());
            log.info("Обновлено описание фильма");
        }
    }

    private void newReleaseDateValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        LocalDate releaseDate = newFilm.getReleaseDate();
        if (releaseDate == null) {
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
            log.info("Обновлена дата выпуска фильма");
        }
    }

    private void newDurationValidation(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        Integer duration = newFilm.getDuration();
        if (duration == null) {
            newFilm.setDuration(oldFilm.getDuration());
            log.info("Обновлена продолжительность фильма");
        }
    }
}
