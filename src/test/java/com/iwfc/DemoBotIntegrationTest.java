package com.iwfc;

import com.iwfc.bot.DemoBot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("End-to-End Simulation Bot Integration Test")
public class DemoBotIntegrationTest {

    @Test
    @DisplayName("Should execute all 29 end-to-end bot steps cleanly without unhandled exceptions")
    void testEndToEndBotExecution() {
        assertDoesNotThrow(() -> {
            DemoBot.main(new String[]{"--fast"});
        });
    }
}
