package ru.chat.mock;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.chat.entity.Chat;
import ru.chat.entity.Message;
import ru.chat.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageRepositoryMock implements MessageRepository {

    List<Message> listMessage = new ArrayList<>();

    @Override
    public List<Message> findAllByChat(Chat chat) {
        return listMessage.stream()
                .filter(msg -> msg.getChat().equals(chat))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAll() {
        return listMessage;
    }

    @Override
    public List<Message> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Message> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return listMessage.size();
    }

    @Override
    public void deleteById(Long aLong) {
        Message msg = getById(aLong);
        if (msg != null)
            listMessage.remove(msg);
    }

    @Override
    public void delete(Message entity) {
        listMessage.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Message> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Message> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Message> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Message> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Message> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Message> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Message> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Message getOne(Long aLong) {
        return null;
    }

    @Override
    public Message getById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Message> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Message> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Message> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Message> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Message> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Message> boolean exists(Example<S> example) {
        return false;
    }
}
