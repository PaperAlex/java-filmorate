package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("GenreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAllGenres() {
        String queryForAllGenres = "SELECT * FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(queryForAllGenres, (resultSet, rowNum) -> genreMapRow(resultSet));
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        String queryForGenre = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.query(queryForGenre, (resultSet, rowNum) -> genreMapRow(resultSet), id).stream().findFirst();
    }

    private Genre genreMapRow(ResultSet resultSet) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name"));
    }
}
