package ru.ivalykhin.assistant_ai.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.ivalykhin.assistant_ai.model.Prompt;
import ru.ivalykhin.assistant_ai.model.Event;
import ru.ivalykhin.assistant_ai.service.PromptService;
import ru.ivalykhin.assistant_ai.service.EventService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventWebController.class)
public class EventWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PromptService promptService;
    @MockitoBean
    private EventService eventService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestEventListPageByUser_success() throws Exception {
        List<Event> events = List.of(Event.builder()
                .prompt(Prompt.builder().id(1L).build())
                .build());
        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/list"))
                .andExpect(model().attributeExists("events"));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestEventCreatePageByUser_success() throws Exception {
        mockMvc.perform(get("/events/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("prompts"));

        verify(promptService, times(1)).getAllPrompts();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestEventCreationByAdminWithCsrf_success() throws Exception {
        Event event = Event.builder()
                .name("Test event")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/events")
                        .flashAttr("event", event)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService, times(1)).saveEvent(event);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestEventCreationByAdminWithoutCsrf_error() throws Exception {
        Event event = Event.builder()
                .name("Test event")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/events")
                        .flashAttr("event", event))
                .andExpect(status().isForbidden());

        verify(eventService, times(0)).saveEvent(event);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestEventEditPageByUser_success() throws Exception {
        Long eventId = 1L;
        Event event = Event.builder()
                .id(eventId)
                .name("Test event")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        when(eventService.getEventById(eventId)).thenReturn(Optional.of(event));

        mockMvc.perform(get("/events/edit/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("prompts"));

        verify(promptService, times(1)).getAllPrompts();
        verify(eventService, times(1)).getEventById(eventId);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestEventUpdateByAdminWithCsrf_success() throws Exception {
        Long eventId = 1L;
        Event event = Event.builder()
                .id(eventId)
                .name("Updated event")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/events/update/{id}", eventId)
                        .flashAttr("event", event)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService, times(1)).saveEvent(event);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void requestEventUpdateByAdminWithoutCsrf_success() throws Exception {
        Long eventId = 1L;
        Event event = Event.builder()
                .id(eventId)
                .name("Updated event")
                .lastChangedBy("admin")
                .prompt(Prompt.builder().id(1L).build())
                .build();

        mockMvc.perform(post("/events/update/{id}", eventId)
                        .flashAttr("event", event))
                .andExpect(status().isForbidden());

        verify(eventService, times(0)).saveEvent(event);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void requestEventDeletionByUser_success() throws Exception {
        Long eventId = 1L;

        mockMvc.perform(get("/events/delete/{id}", eventId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService, times(1)).deleteEvent(eventId);
    }
}
