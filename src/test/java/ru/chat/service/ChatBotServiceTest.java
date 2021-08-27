package ru.chat.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.MessageRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ChatBotServiceTest {


}