package com.openclassrooms.starterjwt.payload;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.payload.response.MessageResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MessageResponse Tests")
class MessageResponseTest {

    @Test
    @DisplayName("Should create MessageResponse with message")
    void shouldCreateMessageResponseWithMessage() {
        String message = "Operation successful";

        MessageResponse response = new MessageResponse(message);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Operation successful");
    }

    @Test
    @DisplayName("Should set message using setter")
    void shouldSetMessageUsingSetter() {
        MessageResponse response = new MessageResponse("Initial message");

        response.setMessage("Updated message");

        assertThat(response.getMessage()).isEqualTo("Updated message");
    }
}
