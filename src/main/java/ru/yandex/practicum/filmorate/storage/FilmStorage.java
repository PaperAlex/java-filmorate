package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film) throws ValidationException;

    Film update(Film film) throws ValidationException, NotFoundException;

    Optional<Film> findById(Long id);

    void likeDuplicatedValidation(Long filmId, Long userId) throws DuplicatedDataException;

    Collection<Film> findPopularFilms(Integer count);

    void updateFilmValidation(Film newFilm) throws NotFoundException;

}
