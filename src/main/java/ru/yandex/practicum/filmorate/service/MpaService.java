package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }


    public List<Mpa> findAllMpa() {
        log.debug("findAllMpa");
        return mpaStorage.findAllMpa();
    }

    public Integer findMpaCount() {
        log.debug("findMpaCount");
        return mpaStorage.findMpaCount();
    }

    public Optional<Mpa> findMpaById(int id) throws NotFoundException {
        Mpa mpa = mpaStorage.findMpaById(id)
                .orElseThrow(() -> new NotFoundException("Rating does not exist"));
        return Optional.ofNullable(mpa);
    }
}
