package ru.ivalykhin.assistant_ai.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ivalykhin.assistant_ai.model.Prompt;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.PromptTypeService;
import ru.ivalykhin.assistant_ai.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromptWebController.class)
public class PromptWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PromptService promptService;
    @MockitoBean
    private PromptTypeService promptTypeService;
    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptListPageByUser_success() throws Exception {
        when(promptService.getAllPrompts()).thenReturn(List.of(new Prompt()));

        mockMvc.perform(get("/prompts"))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt/list"))
                .andExpect(model().attributeExists("prompts"));

        verify(promptService, times(1)).getAllPrompts();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptCreatePageByUser_success() throws Exception {
        when(userService.getCurrentAdminUsername()).thenReturn("user");

        mockMvc.perform(get("/prompts/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt/form"))
                .andExpect(model().attributeExists("prompt"))
                .andExpect(model().attributeExists("promptTypes"));

        verify(promptTypeService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptCreationByAdminWithCsrf_success() throws Exception {
        Prompt prompt = Prompt.builder()
                .name("Test Prompt")
                .lastChangedBy("admin")
                .build();

        mockMvc.perform(post("/prompts")
                        .flashAttr("prompt", prompt)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prompts"));

        verify(promptService, times(1)).savePrompt(prompt);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptCreationByAdminWithoutCsrf_error() throws Exception {
        Prompt prompt = Prompt.builder()
                .name("Test Prompt")
                .lastChangedBy("admin")
                .build();

        mockMvc.perform(post("/prompts")
                        .flashAttr("prompt", prompt))
                .andExpect(status().isForbidden());

        verify(promptService, times(0)).savePrompt(prompt);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptEditPageByUser_success() throws Exception {
        Long promptId = 1L;
        Prompt prompt = Prompt.builder()
                .id(promptId)
                .name("Test Prompt")
                .build();

        when(promptService.getPromptById(promptId)).thenReturn(Optional.of(prompt));
        when(userService.getCurrentAdminUsername()).thenReturn("user");

        mockMvc.perform(get("/prompts/edit/{id}", promptId))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt/form"))
                .andExpect(model().attributeExists("prompt"))
                .andExpect(model().attributeExists("promptTypes"));

        verify(promptTypeService, times(1)).findAll();
        verify(promptService, times(1)).getPromptById(promptId);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptUpdateByAdminWithCsrf_success() throws Exception {
        Long promptId = 1L;
        Prompt prompt = Prompt.builder()
                .id(promptId)
                .name("Updated Prompt")
                .build();

        mockMvc.perform(post("/prompts/update/{id}", promptId)
                        .flashAttr("prompt", prompt)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prompts"));

        verify(promptService, times(1)).savePrompt(prompt);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptUpdateByAdminWithoutCsrf_success() throws Exception {
        Long promptId = 1L;
        Prompt prompt = Prompt.builder()
                .id(promptId)
                .name("Updated Prompt")
                .build();

        mockMvc.perform(post("/prompts/update/{id}", promptId)
                        .flashAttr("prompt", prompt))
                .andExpect(status().isForbidden());

        verify(promptService, times(0)).savePrompt(prompt);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptDeletionByUser_success() throws Exception {
        Long promptId = 1L;

        mockMvc.perform(get("/prompts/delete/{id}", promptId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prompts"));

        verify(promptService, times(1)).deletePrompt(promptId);
    }
}
