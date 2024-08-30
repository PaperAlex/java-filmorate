package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film findFilmById(Long id) {
        return filmStorage.findById(id).get();
    }

    public void addLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);
        if (film.isEmpty()) {
            throw new NotFoundException("Не удалось поставить Like, Фильм Id:" + filmId + "не найден.");
        } else if (user.isEmpty()) {
            throw new NotFoundException("Пользователь Id:" + filmId + "не найден.");
        } else if (findFilmById(filmId).getLikes().contains(userId)) {
            throw new DuplicatedDataException("Данный пользователь уже поставил Like этому фильму");
        } else {
            film.get().getLikes().add(userId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
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

        return filmStorage.findAll()
                .stream()
                .sorted((film0, film1) -> Integer.compare(film1.getLikes().size(), film0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) throws ValidationException {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) throws ValidationException {
        return filmStorage.update(newFilm);
    }
}
