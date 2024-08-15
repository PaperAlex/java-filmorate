package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        // проверяем выполнение необходимых условий
        log.info("Создание нового пользователя login: {}", user.getLogin());
        log.debug("user = {}", user);
        checkDuplicatedEmail(user);
        nameValidation(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь успешно создан, id={}, login={}, email={}", user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) throws ValidationException {
        if (newUser.getId() == null) {
            log.warn("Ошибка валидации, id null недопустимо");
            throw new ValidationException("Id должен быть указан");
        }
        log.debug("Обновляем информацию пользователя id: {}", newUser.getId());
        updateUserValidation(newUser);

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

    private void updateUserValidation(User user) throws ValidationException {
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

    private void checkDuplicatedEmail(User user) throws ValidationException {
        if (users.values().stream().anyMatch(list -> list.getEmail().equals(user.getEmail()))) {
            log.warn("Ошибка валидации, почта {} уже существует", user.getEmail());
            throw new ValidationException("Этот имейл уже используется");
        }
    }
}

