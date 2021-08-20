package ru.chat.mock;

import lombok.Data;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class UserRepositoryMock implements UserRepository {
    private List<User> listUser = new ArrayList<>();

    @Override
    public Optional<User> findByEmail(String email) {
        return listUser.stream()
                .filter(t -> t.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return listUser.stream()
                .filter(t -> t.getEmail().equals(username))
                .findFirst();
    }

    @Override
    public User findByChatsContains(UserInChat chat) {
        return listUser.stream()
                .map(u -> u.getChats())
                .flatMap(l -> l.stream())
                .filter(uic -> uic.equals(chat))
                .map(UserInChat::getUser)
                .findFirst()
                .get();
    }

    @Override
    public List<User> findAll() {
        return listUser;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<User> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return listUser.size();
    }

    @Override
    public void deleteById(Long aLong) {
        listUser.remove(aLong - 1);
    }

    @Override
    public void delete(User entity) {
        listUser.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {
        listUser = new ArrayList<>();
    }

    @Override
    public <S extends User> S save(S entity) {
        var user = listUser.stream()
                .filter(t -> t.getId() == entity.getId())
                .findFirst()
                .orElse(null);

        if (user != null) {
            user.setUsername(entity.getUsername());
            user.setEmail(entity.getEmail());
            return (S) user;
        }

        listUser.add(entity);
        var index = String.valueOf(listUser.size() - 1);
        user = listUser.get(Integer.parseInt(index));
        user.setId(Long.parseLong(index) + 1);

        return (S) user;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<User> findById(Long aLong) {
        return listUser.stream()
                .filter(t -> t.getId().equals(aLong))
                .findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public User getById(Long aLong) {
        return listUser.stream()
                .filter(user -> user.getId().equals(aLong))
                .findFirst()
                .get();
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }
}
