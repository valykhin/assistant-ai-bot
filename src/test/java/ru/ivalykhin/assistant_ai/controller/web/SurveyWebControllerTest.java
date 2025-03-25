package ru.ivalykhin.assistant_ai.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ivalykhin.assistant_ai.model.Prompt;
import ru.ivalykhin.assistant_ai.model.Survey;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.SurveyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SurveyWebController.class)
public class SurveyWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PromptService promptService;
    @MockitoBean
    private SurveyService surveyService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestSurveyListPageByUser_success() throws Exception {
        List<Survey> surveys = List.of(Survey.builder()
                .prompt(Prompt.builder().id(1L).build())
                .build());
        when(surveyService.getAllSurveys()).thenReturn(surveys);

        mockMvc.perform(get("/surveys"))
                .andExpect(status().isOk())
                .andExpect(view().name("survey/list"))
                .andExpect(model().attributeExists("surveys"));

        verify(surveyService, times(1)).getAllSurveys();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestSurveyCreatePageByUser_success() throws Exception {
        mockMvc.perform(get("/surveys/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("survey/form"))
                .andExpect(model().attributeExists("survey"))
                .andExpect(model().attributeExists("prompts"));

        verify(promptService, times(1)).getAllPrompts();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestSurveyCreationByAdminWithCsrf_success() throws Exception {
        Survey survey = Survey.builder()
                .name("Test Survey")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/surveys")
                        .flashAttr("survey", survey)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/surveys"));

        verify(surveyService, times(1)).saveSurvey(survey);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestSurveyCreationByAdminWithoutCsrf_error() throws Exception {
        Survey survey = Survey.builder()
                .name("Test Survey")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/surveys")
                        .flashAttr("survey", survey))
                .andExpect(status().isForbidden());

        verify(surveyService, times(0)).saveSurvey(survey);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestSurveyEditPageByUser_success() throws Exception {
        Long surveyId = 1L;
        Survey survey = Survey.builder()
                .id(surveyId)
                .name("Test Survey")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        when(surveyService.getSurveyById(surveyId)).thenReturn(Optional.of(survey));

        mockMvc.perform(get("/surveys/edit/{id}", surveyId))
                .andExpect(status().isOk())
                .andExpect(view().name("survey/form"))
                .andExpect(model().attributeExists("survey"))
                .andExpect(model().attributeExists("prompts"));

        verify(promptService, times(1)).getAllPrompts();
        verify(surveyService, times(1)).getSurveyById(surveyId);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestSurveyUpdateByAdminWithCsrf_success() throws Exception {
        Long surveyId = 1L;
        Survey survey = Survey.builder()
                .id(surveyId)
                .name("Updated Survey")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/surveys/update/{id}", surveyId)
                        .flashAttr("survey", survey)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/surveys"));

        verify(surveyService, times(1)).saveSurvey(survey);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestSurveyUpdateByAdminWithoutCsrf_success() throws Exception {
        Long surveyId = 1L;
        Survey survey = Survey.builder()
                .id(surveyId)
                .name("Updated Survey")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/surveys/update/{id}", surveyId)
                        .flashAttr("survey", survey))
                .andExpect(status().isForbidden());

        verify(surveyService, times(0)).saveSurvey(survey);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestSurveyDeletionByUser_success() throws Exception {
        Long surveyId = 1L;

        mockMvc.perform(get("/surveys/delete/{id}", surveyId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/surveys"));

        verify(surveyService, times(1)).deleteSurvey(surveyId);
    }
}
