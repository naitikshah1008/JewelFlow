package com.jewelflow.backend.config;

import com.jewelflow.backend.auth.AcceptUserInviteRequest;
import com.jewelflow.backend.auth.AuthController;
import com.jewelflow.backend.auth.AuthService;
import com.jewelflow.backend.auth.UserController;
import com.jewelflow.backend.auth.UserManagementService;
import com.jewelflow.backend.auth.UserResponse;
import com.jewelflow.backend.auth.UserRole;
import com.jewelflow.backend.customer.CustomerController;
import com.jewelflow.backend.customer.CustomerService;
import com.jewelflow.backend.inventory.JewelleryItemController;
import com.jewelflow.backend.inventory.JewelleryItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        CustomerController.class,
        JewelleryItemController.class,
        AuthController.class
})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "jewelflow.security.jwt.secret=local-demo-secret-change-before-production-123456",
        "jewelflow.security.cors.allowed-origin=http://localhost:5173"
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserManagementService userManagementService;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JewelleryItemService jewelleryItemService;

    @MockitoBean
    private AuthService authService;

    @Test
    void anonymousRequestsRequireAuthenticationForProtectedApis() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void staffCannotAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/users").with(staffJwt()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userManagementService);
    }

    @Test
    void adminCanAccessUserManagement() throws Exception {
        when(userManagementService.getUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users").with(adminJwt()))
                .andExpect(status().isOk());

        verify(userManagementService).getUsers();
    }

    @Test
    void staffCannotArchiveCustomers() throws Exception {
        mockMvc.perform(delete("/api/customers/1").with(staffJwt()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(customerService);
    }

    @Test
    void staffCannotArchiveInventoryItems() throws Exception {
        mockMvc.perform(delete("/api/items/1").with(staffJwt()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(jewelleryItemService);
    }

    @Test
    void inviteAcceptanceRemainsPublic() throws Exception {
        AcceptUserInviteRequest request = new AcceptUserInviteRequest();
        request.setToken("invite-token");
        request.setPassword("Welcome123!");
        UserResponse response = UserResponse.builder()
                .id(2L)
                .username("staff")
                .role(UserRole.STAFF)
                .enabled(true)
                .build();
        when(userManagementService.acceptInvite(any(AcceptUserInviteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/invites/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userManagementService).acceptInvite(any(AcceptUserInviteRequest.class));
    }

    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_STAFF"));
    }
}
