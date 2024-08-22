package com.kelp_6.banking_apps.controller.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelp_6.banking_apps.controller.auth.AuthController;
import com.kelp_6.banking_apps.model.mutation.MutationRequest;
import com.kelp_6.banking_apps.model.mutation.MutationResponse;
import com.kelp_6.banking_apps.service.MutationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MutationControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MutationService mutationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MutationController mutationController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mutationController).build();
    }

//    @Test
//    @WithMockUser(username = "testUser", authorities = "USER")
//    void testGetMutationInfo() throws Exception {
////        // Mock user details
//        UserDetails userDetails = User.withUsername("testUser").password("password").authorities("USER").build();
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//
//        // Sample data
//        MutationResponse mutationResponse = new MutationResponse(); // Set appropriate values here
//
//        // Mock service
//        when(mutationService.getMutation(any(MutationRequest.class))).thenReturn(mutationResponse);
//
//        mockMvc.perform(get("/bank-statement")
//                        .param("fromDate", "01-01-2024")
//                        .param("toDate", "31-01-2024")
//                        .param("page", "0")
//                        .param("pageSize", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("success"))
//                .andExpect(jsonPath("$.message").value("success getting account info"))
//                .andExpect(jsonPath("$.data").exists());
//    }
}