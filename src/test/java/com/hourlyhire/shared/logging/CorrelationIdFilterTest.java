package com.hourlyhire.shared.logging;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class CorrelationIdFilterTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void shouldGenerateCorrelationIdWhenHeaderIsMissing()
            throws Exception {

        mockMvc.perform(get("/test/ping"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"));
    }

    @Test
    void shouldPreserveValidIncomingCorrelationId()
            throws Exception {

        mockMvc.perform(
                        get("/test/ping")
                                .header("X-Correlation-ID", "hourlyhire-test-123")
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                "X-Correlation-ID",
                                "hourlyhire-test-123"
                        )
                );
    }

    @Test
    void shouldReplaceInvalidCorrelationId() throws Exception {

        mockMvc.perform(
                        get("/test/ping")
                                .header("X-Correlation-ID", "invalid id with spaces")
                )
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(
                        header().string(
                                "X-Correlation-ID",
                                org.hamcrest.Matchers.not(
                                        "invalid id with spaces"
                                )
                        )
                );
    }

    @RestController
    static class TestController {

        @GetMapping("/test/ping")
        public String ping() {
            return "ok";
        }
    }
}
