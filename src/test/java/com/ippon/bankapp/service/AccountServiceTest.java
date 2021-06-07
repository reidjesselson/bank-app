package com.ippon.bankapp.service;

import com.ippon.bankapp.domain.Account;
import com.ippon.bankapp.repository.AccountRepository;
import com.ippon.bankapp.service.dto.AccountDTO;
import com.ippon.bankapp.service.dto.AmountDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationFactory notificationFactory;

    @Mock
    private EmailService emailService;

    @InjectMocks
    public AccountService subject;


    @Test
    public void createsAccount() {
        //Given
        AccountDTO accountDto = new AccountDTO()
                .firstName("Ben")
                .lastName("Scott");

        given(notificationFactory.getDefaultNotification())
            .willReturn(emailService);

        given(emailService.getName()).willReturn("email");

        given(notificationFactory.getPreferredService("email"))
                .willReturn(Optional.of(emailService));

        Account account = new Account(accountDto.getFirstName(), accountDto.getLastName());
        account.setNotificationPreference("email");

        given(accountRepository.save(account)).willReturn(account);

        ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);

        //act
        AccountDTO accountResult = subject.createAccount(accountDto);

        //assert
        assertThat(accountResult.getBalance(), is(BigDecimal.ZERO));
        assertThat(accountResult.getNotificationPreference(), is("email"));
        assertThat(accountResult.getFirstName(), is("Ben"));
        assertThat(accountResult.getLastName(), is("Scott"));

        verify(emailService, times(1))
                .sendMessage(message.capture(), message.capture(), message.capture(), message.capture());
        assertThat(message.getAllValues().get(0), is("bank"));
        assertThat(message.getAllValues().get(1), is(accountDto.getLastName()));
        assertThat(message.getAllValues().get(2), is("Account Created"));
        assertThat(message.getAllValues().get(3), is("Welcome aboard!"));
    }
    @Test
    public void depositIntoAccount() {
        Account account = new Account();
        account.setFirstName("Reid");
        account.setLastName("Jesselson");
        account.setBalance(BigDecimal.ZERO);
        int id = account.getId();

        given(accountRepository.findById(id)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willReturn(account);

        AmountDTO depo = new AmountDTO();
        depo.setAmount(BigDecimal.valueOf(10));
        AccountDTO accountDepo = subject.depositId(id, depo);

        assertThat(accountDepo.getBalance(), is(BigDecimal.valueOf(10)));
    }

    @Test
    public void makeAmountForTransactionLessThanOneCent() {
        AmountDTO amount = new AmountDTO();

        assertThrows(IllegalArgumentException.class, () -> amount.setAmount(BigDecimal.valueOf(0)));
    }


    @Test
    public void withdrawFromAccount() {
        Account account = new Account();
        account.setFirstName("Reid");
        account.setLastName("Jesselson");
        account.setBalance(BigDecimal.valueOf(10));
        int id = account.getId();

        given(accountRepository.findById(id)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willReturn(account);

        AmountDTO withdraw = new AmountDTO();
        withdraw.setAmount(BigDecimal.valueOf(10));
        AccountDTO accountWithdraw= subject.withdrawId(id, withdraw);

        assertThat(accountWithdraw.getBalance(), is(BigDecimal.ZERO));
    }

    @Test
    public void withdrawMoreThanBalanceFromAccount() {
        Account account = new Account();
        account.setFirstName("Reid");
        account.setLastName("Jesselson");
        account.setBalance(BigDecimal.valueOf(10));
        int id = account.getId();

        given(accountRepository.findById(id)).willReturn(Optional.of(account));

        AmountDTO withdraw = new AmountDTO();
        withdraw.setAmount(BigDecimal.valueOf(11));

        assertThrows(IllegalArgumentException.class, () -> subject.withdrawId(id, withdraw));
    }
}
