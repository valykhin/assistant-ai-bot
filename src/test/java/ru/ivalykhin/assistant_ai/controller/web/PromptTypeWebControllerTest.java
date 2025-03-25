package ru.ivalykhin.assistant_ai.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ivalykhin.assistant_ai.model.PromptType;
import ru.ivalykhin.assistant_ai.service.PromptTypeService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromptTypeWebController.class)
public class PromptTypeWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PromptTypeService promptTypeService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptTypeListPageByUser_success() throws Exception {
        when(promptTypeService.findAll()).thenReturn(List.of(new PromptType()));

        mockMvc.perform(get("/prompt-types"))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt_type/list"))
                .andExpect(model().attributeExists("promptTypes"));

        verify(promptTypeService, times(1)).findAll();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptTypeCreatePageByUser_success() throws Exception {
        mockMvc.perform(get("/prompt-types/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt_type/form"))
                .andExpect(model().attributeExists("promptType"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptTypeCreationByAdminWithCsrf_success() throws Exception {
        PromptType promptType = PromptType.builder()
                .name("Test PromptType")
                .criteria("everytime")
                .build();

        mockMvc.perform(post("/prompt-types")
                        .flashAttr("promptType", promptType)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prompt-types"));

        verify(promptTypeService, times(1)).save(promptType);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptTypeCreationByAdminWithoutCsrf_error() throws Exception {
        PromptType promptType = PromptType.builder()
                .name("Test PromptType")
                .criteria("everytime")
                .build();

        mockMvc.perform(post("/prompt-types")
                        .flashAttr("promptType", promptType))
                .andExpect(status().isForbidden());

        verify(promptTypeService, times(0)).save(promptType);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptTypeEditPageByUser_success() throws Exception {
        String promptTypeName = "Test PromptType";
        PromptType promptType = PromptType.builder()
                .name(promptTypeName)
                .criteria("everytime")
                .build();

        when(promptTypeService.getByName(promptTypeName)).thenReturn(Optional.of(promptType));

        mockMvc.perform(get("/prompt-types/edit/{name}", promptTypeName))
                .andExpect(status().isOk())
                .andExpect(view().name("prompt_type/form"))
                .andExpect(model().attributeExists("promptType"));

        verify(promptTypeService, times(1)).getByName(promptTypeName);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptTypeUpdateByAdminWithCsrf_success() throws Exception {
        String promptTypeName = "Test PromptType";
        PromptType promptType = PromptType.builder()
                .name(promptTypeName)
                .criteria("everytime")
                .build();

        mockMvc.perform(post("/prompt-types/update/{name}", promptTypeName)
                        .flashAttr("promptType", promptType)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prompt-types"));

        verify(promptTypeService, times(1)).save(promptType);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestPromptTypeUpdateByAdminWithoutCsrf_success() throws Exception {
        String promptTypeName = "Updated PromptType";
        PromptType promptType = PromptType.builder()
                .name(promptTypeName)
                .criteria("everytime")
                .build();

        mockMvc.perform(post("/prompt-types/update/{name}", promptTypeName)
                        .flashAttr("promptType", promptType))
                .andExpect(status().isForbidden());

        verify(promptTypeService, times(0)).save(promptType);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestPromptTypeDeletionByUser_success() throws Exception {
        String promptTypeName = "Test PromptType";

        mockMvc.perform(get("/prompt-types/delete/{name}", promptTypeName))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/prompt-types"));

        verify(promptTypeService, times(1)).delete(promptTypeName);
    }
}
