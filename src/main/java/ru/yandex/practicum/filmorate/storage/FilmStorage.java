package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    void deleteLike(Long filmId, Long userId) throws NotFoundException;

    Collection<Film> findAll();

    Film create(Film film) throws ValidationException, NotFoundException;

    Film update(Film film) throws ValidationException, NotFoundException;

    Optional<Film> findFilmById(Long id) throws NotFoundException;

    void addLike(Long filmId, Long userId) throws DuplicatedDataException, NotFoundException;

    Collection<Film> findPopularFilms(Integer count) throws ValidationException;

    boolean existFilmById (Long newFilm) throws NotFoundException;
}
