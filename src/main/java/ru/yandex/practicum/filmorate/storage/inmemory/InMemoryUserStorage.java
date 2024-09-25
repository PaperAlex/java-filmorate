package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) throws ValidationException, DuplicatedDataException {
        // проверяем выполнение необходимых условий
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь успешно создан, id={}, login={}, email={}", user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public User update(User newUser) throws ValidationException, NotFoundException, DuplicatedDataException {
        if (users.containsKey(newUser.getId())) {
            users.put(newUser.getId(), newUser);
        }
        log.info("Обновили информацию пользователя");
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    public void newUserValidation(User user) throws DuplicatedDataException {
        checkDuplicatedEmail(user);
        nameValidation(user);
    }

    public void updateUserValidation(User user) throws ValidationException, DuplicatedDataException, NotFoundException {
        userIdValidation(user);
        checkDuplicatedEmail(user);
        newEmailValidation(user);
        nameValidation(user);
        newNameValidation(user);
        newLoginValidation(user);
        birthdayValidation(user.getBirthday());
        newBirthdayValidation(user);
    }


    private void nameValidation(User user) {
        String name = user.getName();
        if (StringUtils.isBlank(name)) {
            user.setName(user.getLogin());
            log.info("Сохранили login как имя пользователя");
        }
    }

    private void newNameValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String name = newUser.getName();
        if (StringUtils.isBlank(name)) {
            newUser.setName(oldUser.getName());
            log.info("Обновили имя пользователя");
        }
    }

    private void newEmailValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String email = newUser.getEmail();
        if (StringUtils.isBlank(email)) {
            newUser.setEmail(oldUser.getEmail());
            log.info("Обновили почту пользователя");
        }
    }

    private void newLoginValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String login = newUser.getLogin();
        if (StringUtils.isBlank(login)) {
            newUser.setLogin(oldUser.getLogin());
            log.info("Обновили login пользователя");
        }
    }

    private void newBirthdayValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        LocalDate birthday = newUser.getBirthday();
        if (birthday == null) {
            newUser.setBirthday(oldUser.getBirthday());
            log.info("Обновили дату рождения");
        }
    }

    private void birthdayValidation(LocalDate birthday) throws ValidationException {
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации, дата {} недопустима", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void checkDuplicatedEmail(User user) throws DuplicatedDataException {
        if (users.values().stream().anyMatch(list -> list.getEmail().equals(user.getEmail()))) {
            log.warn("Ошибка валидации, почта {} уже существует", user.getEmail());
            throw new DuplicatedDataException("Эта почта уже используется");
        }
    }

    private void userIdValidation(User user) throws NotFoundException {
        if (users.values().stream().noneMatch(list -> list.getId().equals(user.getId()))) {
            log.warn("Ошибка валидации, пользователя с Id: {} не существует", user.getId());
            throw new NotFoundException("Пользователя с таким Id не существует");
        }
    }


    @Override
    public Optional<User> findUserById(Long userId) throws NotFoundException {
        if (users.values().stream().anyMatch((x -> x.getId().equals(userId)))) {
            return Optional.ofNullable(users.get(userId));
        } else {
            log.warn("Ошибка валидации, пользователя с Id: {} не существует", userId);
            throw new NotFoundException("Пользователя с таким Id не существует");
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) throws NotFoundException {

    }

    @Override
    public void acceptFriend(Long userId, Long friendId) throws NotFoundException {

    }

    @Override
    public void deleteFriend(Long userId, Long friendId) throws NotFoundException {

    }

    @Override
    public List<User> findAllFriends(Long userId) throws NotFoundException {
        return null;
    }

    @Override
    public List<User> findMutualFriends(Long userId, Long otherId) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {

    }
}
