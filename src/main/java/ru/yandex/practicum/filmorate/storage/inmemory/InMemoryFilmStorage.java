package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void deleteLike(Long filmId, Long userId) throws NotFoundException {

    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) throws ValidationException {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм");
        return film;
    }

    @Override
    public Film update(Film newFilm) throws ValidationException, NotFoundException {
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

    public void updateFilmValidation(Film newFilm) throws NotFoundException {
        idFilmValidation(newFilm);
        newNameValidation(newFilm);
        newDescriptionValidation(newFilm);
        newReleaseDateValidation(newFilm);
        newDurationValidation(newFilm);
    }

    private void idFilmValidation(Film newFilm) throws NotFoundException {
        if (!films.containsKey(newFilm.getId())) {
            log.error("Ошибка, id {} нет в списке", newFilm.getId());
            throw new NotFoundException("Такого id фильма нет в списке");
        }
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

    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    public Collection<Film> findPopularFilms(Integer count) {
        return findAll()
                .stream()
                .sorted((film0, film1) -> Integer.compare(film1.getLikes().size(), film0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existFilmById(Long newFilm) throws NotFoundException {
        return false;
    }
}