package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Data
@Builder
public class User {
    private Long id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная должна содержать символ @")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
    /* true -подтверждённая дружба — когда второй пользователь согласился на добавление,
     false - неподтверждённая — когда один пользователь отправил запрос на добавление другого пользователя в друзья.*/
    boolean friendStatus;
}
