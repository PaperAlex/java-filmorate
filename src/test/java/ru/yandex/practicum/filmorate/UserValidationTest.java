package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {

    private Validator validator;
    private User user;
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private UserController controller;

    @BeforeEach
    public void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new UserController(userService);
    }

    @Test
    public void emailValidation() {
        user = User.builder()
                .email("email.email.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980, 8, 8))
                .build();

        assertValidation(user);

    }

    @Test
    public void loginValidation() {
        user = User.builder()
                .email("email@email.ru")
                .login("lo gin")
                .name("name")
                .birthday(LocalDate.of(1980, 8, 8))
                .build();

        assertValidation(user);
    }

    @Test
    void userNameValidation() throws ValidationException, DuplicatedDataException {
        user = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.of(1980, 8, 8))
                .build();

        controller.create(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void birthdayValidation() {
        user = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(3000, 8, 8))
                .build();

        assertValidation(user);
    }

    @Test
    void userUpdateTest() throws ValidationException, DuplicatedDataException, NotFoundException {
        user = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1980, 8, 8))
                .build();
        controller.create(user);
        System.out.println(user);

        user = User.builder()
                .id(1L)
                .email("")
                .login("")
                .name("")
                .birthday(LocalDate.of(1980, 9, 8))
                .build();
        controller.update(user);
        System.out.println(user);

        assertEquals("login", user.getLogin());
        assertEquals(1L, user.getId());

    }

    private void assertValidation(User user) {
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        assertEquals(1, violationSet.size());
    }
}