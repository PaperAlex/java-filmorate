package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public void addLike(Long filmId, Long userId) throws NotFoundException, DuplicatedDataException {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);
        if (film.isEmpty()) {
            throw new NotFoundException("Не удалось поставить Like, Фильм Id:" + filmId + "не найден.");
        } else if (user.isEmpty()) {
            throw new NotFoundException("Пользователь Id:" + filmId + "не найден.");
        }
        filmStorage.likeDuplicatedValidation(filmId, userId);
        film.get().getLikes().add(userId);
    }


    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);
        if (film.isEmpty()) {
            throw new NotFoundException("Не удалось удалить Like, Фильм Id:" + filmId + "не найден.");
        } else if (user.isEmpty()) {
            throw new NotFoundException("Пользователь Id:" + filmId + "не найден.");
        } else film.get().getLikes().remove(userId);
    }

    public Collection<Film> popularFilms(Integer count) throws ValidationException {
        if (count <= 0) {
            log.warn("Ошибка валидации, count null недопустимо");
            throw new ValidationException("Количество фильмов должно быть больше 0");
        }
        return filmStorage.findPopularFilms(count);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) throws ValidationException {
        log.debug("Создание нового фильма id: {}", film.getId());
        // проверяем выполнение необходимых условий
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) throws ValidationException, NotFoundException {
        if (newFilm.getId() == null) {
            log.warn("Ошибка валидации, id null недопустимо");
            throw new ValidationException("Id должен быть указан");
        }
        filmStorage.updateFilmValidation(newFilm);
        log.debug("Обновление фильма id: {}", newFilm.getId());
        return filmStorage.update(newFilm);
    }

    private void validateFilm(Film film) throws ValidationException {
        nameValidation(film.getName());
        releaseDateValidation(film.getReleaseDate());
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
}
