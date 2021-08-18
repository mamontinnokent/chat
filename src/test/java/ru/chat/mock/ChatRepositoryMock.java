package ru.chat.mock;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.chat.entity.Chat;
import ru.chat.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatRepositoryMock implements ChatRepository {

    List<Chat> listChat = new ArrayList<>();

    @Override
    public Optional<Chat> findByNameChat(String nameChat) {
        return listChat.stream()
                .filter(t -> t.getNameChat().equals(nameChat))
                .findFirst();
    }

    @Override
    public Chat getByNameChat(String chatName) {
        return null;
    }

    @Override
    public List<Chat> getAllByPrivacy(boolean privacy) {
        return listChat.stream()
                .filter(t -> t.isPrivacy())
                .collect(Collectors.toList());
    }

    @Override
    public List<Chat> findAll() {
        return listChat;
    }

    @Override
    public List<Chat> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Chat> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Chat> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        Chat chat = getById(aLong);
        delete(chat);
    }

    @Override
    public void delete(Chat entity) {
        listChat.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Chat> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Chat> S save(S entity) {
        listChat.add(entity);
        entity.setId((long) listChat.size());
        return entity;
    }

    @Override
    public <S extends Chat> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Chat> findById(Long aLong) {
        return listChat.stream()
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
    public <S extends Chat> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Chat> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Chat> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Chat getOne(Long aLong) {
        return null;
    }

    @Override
    public Chat getById(Long aLong) {
        return listChat.stream()
                .filter(t -> t.getId().equals(aLong))
                .findFirst()
                .get();
    }

    @Override
    public <S extends Chat> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Chat> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Chat> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Chat> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Chat> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Chat> boolean exists(Example<S> example) {
        return listChat.contains(example);
    }
}
