package ru.chat.mock;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.repository.UserInChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserInChatRepositoryMock implements UserInChatRepository {

    List<UserInChat> listUserInChat = new ArrayList<>();

    @Override
    public List<UserInChat> findAllByChatAndAndInChat(Chat chat, boolean inChat) {
        return listUserInChat.stream()
                .filter(t -> t.getChat().equals(chat) && t.isInChat())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserInChat> findAllByUserAndInChat(User user, Boolean inChat) {
        return listUserInChat.stream()
                .filter(t -> t.getUser().equals(user) && t.isInChat())
                .collect(Collectors.toList());
    }

    @Override
    public UserInChat findByUserAndChat(User user, Chat chat) {
        return listUserInChat.stream()
                .filter(t -> t.getUser().equals(user) && t.getChat().equals(chat))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteAllByUser(User user) {
        listUserInChat.removeAll(
                listUserInChat.stream()
                        .filter(t -> t.getUser().equals(user))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<UserInChat> findAll() {
        return listUserInChat;
    }

    @Override
    public List<UserInChat> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<UserInChat> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<UserInChat> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return listUserInChat.size();
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(UserInChat entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends UserInChat> entities) {

    }

    @Override
    public void deleteAll() {
        listUserInChat = new ArrayList<>();
    }

    @Override
    public <S extends UserInChat> S save(S entity) {
        listUserInChat.add(entity);
        entity.setId((long) listUserInChat.size());
        return entity;
    }

    @Override
    public <S extends UserInChat> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<UserInChat> findById(Long aLong) {
        return listUserInChat.stream()
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
    public <S extends UserInChat> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends UserInChat> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<UserInChat> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public UserInChat getOne(Long aLong) {
        return null;
    }

    @Override
    public UserInChat getById(Long aLong) {
        return listUserInChat.stream()
                .filter(t -> t.getId().equals(aLong))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <S extends UserInChat> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends UserInChat> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends UserInChat> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends UserInChat> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends UserInChat> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends UserInChat> boolean exists(Example<S> example) {
        return false;
    }
}
