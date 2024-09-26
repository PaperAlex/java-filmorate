package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("UserDbStorage") UserStorage userStorage,
                         @Qualifier("GenreDbStorage") GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
    }

    private static final String FIND_ALL_FILMS = "SELECT * FROM films AS f" +
            " LEFT JOIN rating AS r ON f.rating_id = r.rating_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films AS f JOIN rating AS r" +
            " ON f.rating_id = r.rating_id WHERE film_id = ?;";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, releaseDate, duration, rating_id)" +
            "VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, releaseDate = ?, " +
            "duration = ?, rating_id = ? WHERE film_id = ?;";
    private static final String INSERT_INTO_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
    private static final String FILM_GENRE_DELETE_QUERY = "DELETE FROM film_genre WHERE film_id = ?;";
    private static final String INSERT_INTO_LIKES = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";


    @Override
    public Film create(Film film) throws ValidationException, NotFoundException {
        log.debug("create film({})", film);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                },
                keyHolder);
        film.setId(keyHolder.getKey().longValue());

        for (Genre genre1 : film.getGenres()) {
            Genre g = genreStorage.findGenreById(genre1.getId())
                    .orElseThrow(() -> new ValidationException("Genre not found"));
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(g -> jdbcTemplate.update(INSERT_INTO_FILM_GENRE, film.getId(), g.getId()));
        }
        return film;
    }


    @Override
    public Film update(Film film) throws NotFoundException, ValidationException {
        Long id = film.getId();
        jdbcTemplate.update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(INSERT_INTO_FILM_GENRE, film.getId());
            }
        }

        findFilmById(film.getId());
        return film;
    }


    @Override
    public Optional<Film> findFilmById(Long filmId) throws NotFoundException {
        return jdbcTemplate.query(FIND_BY_ID_QUERY, (resultSet, rowNum) -> mapRowFilm(resultSet), filmId).stream().findFirst();
    }

    public Film mapRowFilm(ResultSet resultSet) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(resultSet.getInt("rating_id"), resultSet.getString("rating_name")))
                .build();
        List<Genre> filmsGenre = findFilmGenres(film.getId());
        List<Integer> likes = getFilmLikes(film.getId());
        for (Genre genre : filmsGenre) {
            film.getGenres().add(genre);
        }
        for (Integer like : likes) {
            film.getLikes().add(Long.valueOf(like));
        }
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name"));
    }

    private Integer mapRowToLike(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }

    private List<Genre> findFilmGenres(long filmId) {
        String findFilmGenresQuery = "SELECT fg.film_id, fg.genre_id, g.genre_name FROM film_genre fg" +
                " JOIN genre g ON g.genre_id = fg.genre_id WHERE film_id = ? ORDER BY genre_id;";
        return jdbcTemplate.query(findFilmGenresQuery, this::mapRowToGenre, filmId);
    }

    private List<Integer> getFilmLikes(long filmId) {
        String getFilmLikesQuery = "SELECT user_id FROM likes WHERE film_id = ?;";
        return jdbcTemplate.query(getFilmLikesQuery, this::mapRowToLike, filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) throws DuplicatedDataException, NotFoundException {
        Optional<Film> film = findFilmById(filmId);
        Optional<User> user = userStorage.findUserById(userId);
        jdbcTemplate.update(INSERT_INTO_LIKES, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) throws NotFoundException {
        Optional<Film> film = findFilmById(filmId);
        Optional<User> user = userStorage.findUserById(userId);
        jdbcTemplate.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(FIND_ALL_FILMS, (resultSet, rowNum) -> mapRowFilm(resultSet));
    }

    @Override
    public Collection<Film> findPopularFilms(Integer count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                "mpa.rating_id, mpa.rating_name AS mpa_name " +
                "FROM films AS f " +
                "INNER JOIN rating AS mpa ON f.rating_id = mpa.rating_id " +
                "LEFT JOIN likes ON f.film_id = likes.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(likes.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> mapRowFilm(resultSet), count);
    }

    @Override
    public boolean existFilmById(Long newFilm) throws NotFoundException {
        if (findFilmById(newFilm).isEmpty()) {
            log.warn("Ошибка валидации, id null недопустимо");
            throw new NotFoundException("Должен быть указан существующий id");
        }
        log.debug("Обновление фильма id: {}", newFilm);
        return true;
    }
}

