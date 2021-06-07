package com.ippon.bankapp.service;

import com.ippon.bankapp.domain.Account;
import com.ippon.bankapp.domain.Transaction;
import com.ippon.bankapp.repository.AccountRepository;
import com.ippon.bankapp.repository.TransactionRepository;
import com.ippon.bankapp.service.dto.AccountDTO;
import com.ippon.bankapp.service.dto.AmountDTO;
import com.ippon.bankapp.service.dto.TransactionDTO;
import com.ippon.bankapp.service.exception.AccountLastNameExistsException;
import com.ippon.bankapp.service.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {


    private AccountRepository accountRepository;
    private NotificationFactory notificationFactory;
    private final TransactionRepository transactionRepository;

    @Autowired(required = true)
    public AccountService(AccountRepository accountRepository, NotificationFactory notificationFactory, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.notificationFactory = notificationFactory;
        this.transactionRepository = transactionRepository;

    }

    public AccountDTO createAccount(AccountDTO newAccount) {
        validateLastNameUnique(newAccount.getLastName());
        Account account = new Account(newAccount.getFirstName(), newAccount.getLastName());
        account.setNotificationPreference(notificationFactory
                .getDefaultNotification()
                .getName());

        Account save = accountRepository.save(account);

        notificationFactory
                .getPreferredService(save.getNotificationPreference())
                .orElseGet(notificationFactory::getDefaultNotification)
                .sendMessage("bank",
                        account.getLastName(),
                        "Account Created",
                        "Welcome aboard!");

        return mapAccountToDTO(save);
    }

    public AccountDTO getAccount(String lastName) {
        Account account = accountRepository
                .findByLastName(lastName)
                .orElseThrow(AccountNotFoundException::new);

        return mapAccountToDTO(account);
    }

    public AccountDTO getAccountFirstName(String firstName) {
        Account account = accountRepository
                .findByFirstName(firstName)
                .orElseThrow(AccountNotFoundException::new);

        return mapAccountToDTO(account);
    }

    public AccountDTO depositId(int id, AmountDTO amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal amountVal = amount.getAmount();
        account.setBalance(account.getBalance().add(amountVal));

        Transaction deposit = new Transaction("deposit", amountVal, account.getFirstName(), account.getLastName(), "", "");
        transactionRepository.save(deposit);

        Account save = accountRepository.save(account);

        return mapAccountToDTO(save);
    }

    public AccountDTO withdrawId(int id, AmountDTO amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal amountVal = amount.getAmount();
        if (amountVal.compareTo(account.getBalance()) != 1) {
            account.setBalance(account.getBalance().subtract(amountVal));
        }
        else {
            throw new IllegalArgumentException("Withdrawing more than balance");
        }

        Account save = accountRepository.save(account);

        return mapAccountToDTO(save);
    }

    public AccountDTO transferBal(int id1, int id2, AmountDTO amount) {
        Account account1 = accountRepository
                .findById(id1)
                .orElseThrow(AccountNotFoundException::new);
        Account account2 = accountRepository
                .findById(id2)
                .orElseThrow(AccountNotFoundException::new);

        BigDecimal amountVal = amount.getAmount();
        if (amountVal.compareTo(account1.getBalance()) != 1) {
            account1.setBalance(account1.getBalance().subtract(amountVal));
            account2.setBalance(account2.getBalance().add(amountVal));
        }

        Account save1 = accountRepository.save(account1);
        Account save2 = accountRepository.save(account2);

        return mapAccountToDTO(save2);
    }

    private void validateLastNameUnique(String lastName) {
        accountRepository
                .findByLastName(lastName)
                .ifPresent(t -> {throw new AccountLastNameExistsException();});
    }

    private AccountDTO mapAccountToDTO(Account account) {
        return new AccountDTO()
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .balance(account.getBalance())
                .notificationPreference(account.getNotificationPreference());
    }

}
