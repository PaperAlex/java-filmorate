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
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {

    private Validator validator;
    private User user;

    @BeforeEach
    public void beforeEach() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
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
                .name("login")
                .birthday(LocalDate.of(1980, 8, 8))
                .build();

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

    private void assertValidation(User user) {
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        assertEquals(1, violationSet.size());
    }
}