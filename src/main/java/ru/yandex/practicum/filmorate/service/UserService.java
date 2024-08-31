package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User findUserById(Long id) throws NotFoundException {
        return userStorage.findById(id).get();
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        findUserById(userId).getFriends().add(friendId);
        findUserById(friendId).getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {
        findUserById(userId).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(userId);
    }

    public Collection<User> findAllFriends(Long userId) throws NotFoundException {
        User user = findUserById(userId);
        Set<Long> friendsIds = user.getFriends();

        ArrayList<User> friendsList = new ArrayList<>();
        for (Long id : friendsIds) {
            friendsList.add(findUserById(id));
        }
        return friendsList;
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
        log.info("Создание нового пользователя login: {}", user.getLogin());
        log.debug("user = {}", user);
        userStorage.newUserValidation(user);
        return userStorage.create(user);
    }

    public User update(User newUser) throws ValidationException, NotFoundException, DuplicatedDataException {
        log.debug("Обновляем информацию пользователя id: {}", newUser.getId());
        userStorage.updateUserValidation(newUser);
        if (newUser.getId() == null) {
            log.warn("Ошибка валидации, id null недопустимо");
            throw new NotFoundException("Id должен быть указан");
        }
        return userStorage.update(newUser);
    }

}
