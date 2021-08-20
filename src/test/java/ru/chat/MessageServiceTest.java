package ru.chat;

//
//@Slf4j
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class MessageServiceTest {
//    public static ChatService chatService;
//
//    @BeforeAll
//    public void initUserService() {
//        this.userService = TestUtils.initUserService();
//    }
//
//    @Test
//    @DisplayName("Check get by id")
//    public void testGetById() {
//        try {
//            userService.create(new UserRegRequestDTO("test@gmail.com", "test", "test"));
//            UserResponseDTO user = userService.getById(userService.getUserRepository().count());
//
//            Assertions.assertEquals(userService.getUserRepository().count(), user.getId());
//            Assertions.assertEquals("test", user.getUsername());
//            Assertions.assertEquals(new HashMap<String, Long>(), user.getChats());
//
//            log.info("Successfully checked getting by id");
//        } catch (Exception e) {
//            fail("Exception " + e.getMessage());
//        }
//    }
//}
