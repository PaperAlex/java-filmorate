package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User findUserById(Long id) {
        return userStorage.findById(id).get();
    }

    public void addFriend (Long userId, Long friendId) {
        findUserById(userId).getFriends().add(friendId);
        findUserById(friendId).getFriends().add(userId);
    }

    public void deleteFriend (Long userId, Long friendId) {
        findUserById(userId).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(userId);
    }

    public Collection<User> findAllFriends (Long userId) {
        User user = findUserById(userId);
        Set<Long> friendsIds = user.getFriends();

        ArrayList<User> friendsList = new ArrayList<>();
        for (Long id : friendsIds) {
            friendsList.add(findUserById(id));
        }
        return friendsList;
    }
    public Collection<User> findMutualFriends(Long userId, Long friendId) {
        Collection<User> userFriends = findAllFriends(userId);
        Collection<User> friendFriends = findAllFriends(friendId);
        userFriends.retainAll(friendFriends);
        return userFriends;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) throws ValidationException {
        return userStorage.create(user);
    }

    public User update(User newUser) throws ValidationException {
        return userStorage.update(newUser);
    }

}
