# java-filmorate

Cервис, который работает с фильмами и оценками пользователей, а также возвращать топ-5 фильмов, рекомендованных к просмотру. Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.

## ER-диаграмма

![filmorate ER](/assets/images/er.png)
---
### users
- Информация о пользователях
- Информация о друзьях в таблице friends
- Информация о лайках к фильму в таблице likes

### films
- Информация о фильмах
- Информация о жанре фильма в таблице film_genre
- Информация о рейтинге в таблице rating

### genre
- Информация о название жанра

---

## Примеры SQL запросов:

Найти друзей пользователя:
```sql
SELECT u.login
FROM users AS u
WHERE u.user_id IN (SELECT f.friend_id
                  FROM friends AS f
                  WHERE f.user_id = {userId} 
                  AND status = TRUE;)
```


ТОП - 5 фильмов по количеству лайков:

```sql
SELECT name
FROM films
WHERE film_id IN (SELECT film_id
                  FROM likes
                  GROUP BY film_id
                  ORDER BY COUNT(user_id) DESC
                  LIMIT 5);
```

Список фильмов которые лайкнул пользователь:

```sql
SELECT f.name
FROM films AS f
WHERE f.film_id IN (SELECT film_id
                    FROM likes 
                    WHERE userd_id = {userId};)
```