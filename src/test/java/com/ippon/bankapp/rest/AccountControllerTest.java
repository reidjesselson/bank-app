package com.ippon.bankapp.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ippon.bankapp.rest.errors.RestErrorHandler;
import com.ippon.bankapp.service.AccountService;
import com.ippon.bankapp.service.dto.AccountDTO;
import com.ippon.bankapp.service.dto.AmountDTO;
import com.ippon.bankapp.service.exception.AccountLastNameExistsException;
import com.ippon.bankapp.service.exception.AccountNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {AccountController.class, RestErrorHandler.class})
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private RestErrorHandler restErrorHandler;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void before() {
        AccountController subject = new AccountController(accountService);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(subject)
                .setControllerAdvice(restErrorHandler)
                .build();
    }

    @Test
    public void testAccountRetrieval_AccountExists() throws Exception {
        given(accountService.getAccount("Scott"))
                .willReturn(new AccountDTO()
                        .lastName("Scott")
                        .firstName("Ben"));

        mockMvc
                .perform(get("/api/account/Scott"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ben"))
                .andExpect(jsonPath("$.lastName").value("Scott"));
    }

    @Test
    public void testAccountRetrieval_AccountDoesNotExist() throws Exception {
        given(accountService.getAccount("Scott"))
                .willThrow(new AccountNotFoundException());

        String errorMessage = mockMvc
                .perform(get("/api/account/Scott"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getErrorMessage();

        assertThat(errorMessage, is("Account not found"));
    }

    @Test
    public void testCreateAccount_requestValid() throws Exception {
        AccountDTO newAccount = new AccountDTO()
                .firstName("Ben")
                .lastName("Scott");

        given(accountService.createAccount(newAccount))
                .willReturn(new AccountDTO()
                        .lastName("Scott")
                        .firstName("Ben")
                        .balance(BigDecimal.ZERO)
                        .notificationPreference("email"));

        mockMvc
                .perform(post("/api/account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ben"))
                .andExpect(jsonPath("$.lastName").value("Scott"))
                .andExpect(jsonPath("$.balance").value(0.0))
                .andExpect(jsonPath("$.notificationPreference").value("email"));

    }

    @Test
    public void testCreateAccount_missingFirstName() throws Exception {

        AccountDTO newAccount = new AccountDTO()
                .lastName("Scott");

        ResultActions mvcResult = mockMvc
                .perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAccount_missingLastName() throws Exception {
        AccountDTO newAccount = new AccountDTO()
                .firstName("Ben");

        ResultActions mvcResult = mockMvc
                .perform(post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAccount_lastNameExists_throwsException() throws Exception {
        AccountDTO newAccount = new AccountDTO()
                .firstName("Ben")
                .lastName("Scott");

        given(accountService.createAccount(newAccount))
                .willThrow(new AccountLastNameExistsException());

        mockMvc
                .perform(post("/api/account/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccount)))
                .andExpect(status().isConflict());
    }

    @Test
    public void depositValid() throws Exception{
        AmountDTO amount = new AmountDTO();
        amount.setAmount(BigDecimal.TEN);

        mockMvc
                .perform(post("/api/deposit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isAccepted());
    }

    @Test
    public void withdrawValid() throws Exception{
        AmountDTO amount = new AmountDTO();
        amount.setAmount(BigDecimal.TEN);

        mockMvc
                .perform(post("/api/withdraw/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isAccepted());
    }
    @Test
    public void transferValid() throws Exception{
        AmountDTO amount = new AmountDTO();
        amount.setAmount(BigDecimal.TEN);

        mockMvc
                .perform(post("/api/transfer/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isAccepted());
    }
    @Test
    public void depositMissingAmount() throws Exception{
        AmountDTO amount = new AmountDTO();

        mockMvc
                .perform(post("/api/deposit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void withdrawMissingAmount() throws Exception{
        AmountDTO amount = new AmountDTO();

        mockMvc
                .perform(post("/api/withdraw/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void transferMissingAmount() throws Exception{
        AmountDTO amount = new AmountDTO();

        mockMvc
                .perform(post("/api/transfer/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amount)))
                .andExpect(status().isBadRequest());
    }
}
