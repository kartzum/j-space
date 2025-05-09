package io.rdlab.ic.mod.service;

import io.rdlab.ic.mod.ContainersRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ChatServiceTests extends ContainersRunner {
    @Autowired
    private ChatService chatService;

    @Test
    void simpleTest() {
        String answer = chatService.prompt("Provide 3 short bullet points explaining why Java is awesome");
        assertThat(answer).isNotBlank();
    }
}
