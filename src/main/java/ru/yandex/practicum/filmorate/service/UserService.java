package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage"/*"InMemoryUserStorage"*/) UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User findUserById(Long id) throws NotFoundException {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.existById(userId, friendId);
        log.debug("addFriend: {} to {}", friendId, userId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {
        if (userStorage.findUserById(userId).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        log.debug("deleteFriend: {} from {}", friendId, userId);
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> findAllFriends(Long userId) throws NotFoundException {
        User findUser = findUserById(userId);
        log.debug("findAllFriends: {}", userId);
        return userStorage.findAllFriends(userId);
    }

    public Collection<User> findMutualFriends(Long userId, Long friendId) throws NotFoundException {
        Collection<User> userFriends = findAllFriends(userId);
        Collection<User> friendFriends = findAllFriends(friendId);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) throws ValidationException, DuplicatedDataException {
        userNameValidation(user);
        log.info("Создание нового пользователя login: {}", user.getLogin());
        log.debug("user = {}", user);
        userNameValidation(user);
        return userStorage.create(user);
    }

    public User update(User newUser) throws ValidationException, NotFoundException, DuplicatedDataException {
        log.debug("Обновляем информацию пользователя id: {}", newUser.getId());
        userNameValidation(newUser);
        User findUser = findUserById(newUser.getId());
        if (findUser == null) {
            log.warn("Ошибка валидации, id null недопустимо");
            throw new NotFoundException("Id должен быть указан");
        }
        return userStorage.update(newUser);
    }

    private void userNameValidation(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public void acceptFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.acceptFriend(userId, friendId);
    }
}
