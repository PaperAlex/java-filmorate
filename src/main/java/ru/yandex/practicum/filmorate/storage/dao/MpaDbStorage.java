package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("MpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private static final String FIND_ALL_MPA = "SELECT * FROM rating ORDER BY rating_id";


    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Mpa> findAllMpa() {
        return jdbcTemplate.query(FIND_ALL_MPA, (resultSet, rowNum) -> mpaMapRow(resultSet));
    }

    @Override
    public Optional<Mpa> findMpaById(Integer id) {
        String mpaQuery = "SELECT * FROM rating WHERE rating_id =?";
        return jdbcTemplate.query(mpaQuery, (resultSet, rowNum) -> mpaMapRow(resultSet), id).stream().findFirst();
    }

    private Mpa mpaMapRow(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("rating_id");
        String name = resultSet.getString("rating_name");
        return new Mpa(id, name);
    }

    private static final String FIND_MPA_COUNT = "SELECT COUNT(rating_id) AS cnt FROM rating;";

    @Override
    public Integer findMpaCount() {
        return jdbcTemplate.queryForObject(FIND_MPA_COUNT, Integer.class);
    }
}
