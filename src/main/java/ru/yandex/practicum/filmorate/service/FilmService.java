package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@Validated
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage"/*"InMemoryFilmStorage"*/) FilmStorage filmStorage,
                       @Qualifier("UserDbStorage"/*"InMemoryUserStorage"*/) UserStorage userStorage,
                       @Qualifier("MpaDbStorage") MpaStorage mpaStorage,
                       @Qualifier("GenreDbStorage") GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void addLike(Long filmId, Long userId) throws NotFoundException, DuplicatedDataException {
        Film film = filmStorage.findFilmById(filmId).orElseThrow(() -> new NotFoundException
                ("Не удалось поставить Like, Фильм Id:" + filmId + "не найден."));
        User user = userStorage.findUserById(userId).orElseThrow(() -> new NotFoundException
                ("Пользователь Id:\" + filmId + \"не найден."));

        filmStorage.addLike(filmId, userId);
    }


    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        if (filmStorage.findFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Не удалось удалить Like, Фильм Id:" + filmId + "не найден.");
        } else if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь Id:" + filmId + "не найден.");
        } else filmStorage.deleteLike(filmId, userId);
    }

    public Optional<Film> findFilmById(Long filmId) throws NotFoundException {
        return filmStorage.findFilmById(filmId);
    }

    public Collection<Film> findPopularFilms(@Positive Integer count) throws ValidationException {
        return filmStorage.findPopularFilms(count);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) throws NotFoundException, ValidationException {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) throws ValidationException, NotFoundException {
        filmStorage.existFilmById(newFilm.getId());
        return filmStorage.update(newFilm);
    }

    private void validateFilm(Film film) throws ValidationException {
        nameValidation(film.getName());
        releaseDateValidation(film.getReleaseDate());
        mpaValidation((film.getMpa().getId()));
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

    private void mpaValidation(Integer mpaId) throws ValidationException {
        int mpaCount = mpaStorage.findMpaCount();
        if (mpaId > mpaCount) {
            throw new ValidationException("Mpa has only " + mpaCount + " ratings");
        }
    }
}
