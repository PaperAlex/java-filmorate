package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user) throws ValidationException, DuplicatedDataException;

    User update(User newUser) throws ValidationException, NotFoundException, DuplicatedDataException;

    Optional<User> findById(Long id) throws NotFoundException;

    void newUserValidation(User user) throws DuplicatedDataException;

    void updateUserValidation(User user) throws ValidationException, DuplicatedDataException, NotFoundException;
}
