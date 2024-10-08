package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user) throws ValidationException, DuplicatedDataException;

    User update(User newUser) throws ValidationException, NotFoundException, DuplicatedDataException;

    Optional<User> findUserById(Long userId) throws NotFoundException;

    void addFriend(Long userId, Long friendId) throws NotFoundException;

    void acceptFriend(Long userId, Long friendId) throws NotFoundException;

    void deleteFriend(Long userId, Long friendId) throws NotFoundException;

    List<User> findAllFriends(Long userId) throws NotFoundException;

    List<User> findMutualFriends(Long userId, Long otherId);

    void deleteUser(Long userId);

    public boolean existById(Long userId, Long friendId) throws NotFoundException;
}
