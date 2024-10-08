package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) "
            + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, false)";
    private static final String DELETE_USER_QUERY = "DELETE FROM \"USER\" WHERE USER_ID = ?;";
    private static final String MUTUAL_F_QUERY = "SELECT * FROM user AS u WHERE u.user_id IN (SELECT f.friend_id " +
            "FROM friends AS f WHERE f.user_id = ? " +
            "INTERSECT SELECT f.friend_id FROM friends AS f WHERE f.user_id = ?);";


    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, (resultSet, rowNum) -> mapRow(resultSet));
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"user_id"});
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User newUser) throws ValidationException, NotFoundException, DuplicatedDataException {
        jdbcTemplate.update(
                UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public Optional<User> findUserById(Long userId) throws NotFoundException {
        return jdbcTemplate.query(FIND_BY_ID_QUERY, (resultSet, rowNun) -> mapRow(resultSet), userId).stream().findFirst();
    }

    public User mapRow(ResultSet resultSet) throws SQLException {

        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void acceptFriend(Long userId, Long friendId) throws NotFoundException {
        String acceptFriendQuery = "UPDATE friends SET status = true WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(acceptFriendQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {
        String removeFriendQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int count = jdbcTemplate.update(removeFriendQuery, userId, friendId);
    }

    @Override
    public List<User> findAllFriends(Long userId) throws NotFoundException {
        String findAllFriendsQuery = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM friends AS f INNER JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? ORDER BY u.user_id";
        return jdbcTemplate.query(findAllFriendsQuery, (resultSet, rowNun) -> mapRow(resultSet), userId);
    }

    @Override
    public List<User> findMutualFriends(Long userId, Long friendId) {
        return jdbcTemplate.query(MUTUAL_F_QUERY, (resultSet, rowNun) -> mapRow(resultSet), userId, friendId);
    }

    @Override
    public void deleteUser(Long userId) {
        jdbcTemplate.update(DELETE_USER_QUERY, userId);
    }

    @Override
    public boolean existById(Long userId, Long friendId) throws NotFoundException {
        if (findUserById(userId).isEmpty() || findUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        if (userId < 0 || friendId < 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return true;
    }
}

