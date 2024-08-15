package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Вызываем список пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        // проверяем выполнение необходимых условий
        log.info("Создаем нового юзера");
        checkDuplicatedEmail(user);
        nameValidation(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан юзер {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Не указан id юзера");
            throw new ValidationException("Id должен быть указан");
        }
        log.info("Обновляем информацию юзера");
        updateUserValidation(newUser);

        if (users.containsKey(newUser.getId())) {
            users.put(newUser.getId(), newUser);
        }
        log.trace("Обновили информацию юзера {}", newUser);
        return newUser;
    }

    private long getNextId() {
        log.info("Создаем id юзера");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void updateUserValidation(User user) {
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
        if (name == null || name.isBlank() || name.isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Сохранили имя юзера {}", user.getId());
        }
    }

    private void newNameValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String name = newUser.getName();
        if (name == null || name.isBlank() || name.isEmpty()) {
            newUser.setName(oldUser.getName());
            log.debug("Обновили имя юзера {}", newUser.getId());
        }
    }

    private void newEmailValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String email = newUser.getEmail();
        if (email == null || email.isBlank() || email.isEmpty()) {
            newUser.setEmail(oldUser.getEmail());
            log.debug("Обновили почту юзера {}", newUser.getId());
        }
    }

    private void newLoginValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String login = newUser.getLogin();
        if (login == null || login.isBlank() || login.isEmpty()) {
            newUser.setLogin(oldUser.getLogin());
            log.debug("Обновили login юзера {}", newUser.getId());
        }
    }

    private void newBirthdayValidation(User newUser) {
        User oldUser = users.get(newUser.getId());
        String birthday = newUser.getLogin();
        if (birthday == null || birthday.isBlank() || birthday.isEmpty()) {
            newUser.setBirthday(oldUser.getBirthday());
            log.debug("Обновили login юзера {}", newUser.getId());
        }
    }

    private void birthdayValidation(LocalDate birthday) {
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            log.error("Ошибка даты рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void checkDuplicatedEmail(User user) {
        if (users.values().stream().anyMatch(list -> list.getEmail().equals(user.getEmail()))) {
            log.error("Такая почта уже используется");
            throw new ValidationException("Этот имейл уже используется");
        }
    }
}

