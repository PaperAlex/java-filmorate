package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAllGenres() {
        List<Genre> genres = genreStorage.findAllGenres();
        log.debug("findAllGenres: {}", genres);
        return genres;
    }

    public Optional<Genre> findGenreById(int id) throws NotFoundException {
        Genre genre = genreStorage.findGenreById(id).orElseThrow(() -> new NotFoundException("Genre not found"));
        log.debug("findGenreById: {}", genre);
        return Optional.ofNullable(genre);
    }
}
