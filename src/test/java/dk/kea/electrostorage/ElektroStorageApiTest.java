package dk.kea.electrostorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ElektroStorageApiTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void componentsCanBeListedAndCreated() throws Exception {
        mockMvc.perform(get("/api/components"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(11))));

        var body = Map.of("internalNumber", 3001, "name", "Testdiode", "externalNumber", "TEST-1", "supplierId", 1);
        mockMvc.perform(post("/api/components").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Testdiode"));
    }

    @Test
    void componentCanBeDiscontinued() throws Exception {
        mockMvc.perform(patch("/api/components/1010/discontinue"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.discontinued").value(true));
    }

    @Test
    void sentOrderCannotReceiveMoreLines() throws Exception {
        var body = Map.of("componentNumber", 1005, "quantity", 2);
        mockMvc.perform(post("/api/orders/2/lines").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Der kan ikke tilføjes til en sendt ordre"));
    }

    @Test
    void inventoryCanBeListedAndCounted() throws Exception {
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].receivedQuantity").value(100));

        var body = Map.of("countedBy", "Testperson", "quantity", 97);
        mockMvc.perform(post("/api/inventory/1001/counts").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.quantity").value(97));
    }

    @Test
    void assemblyContainsFourComponents() throws Exception {
        mockMvc.perform(get("/api/assemblies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultComponent.name").value("Lysende LED"))
                .andExpect(jsonPath("$[0].lines", hasSize(4)));
    }
}
