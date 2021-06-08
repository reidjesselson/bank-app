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
import com.ippon.bankapp.service.exception.DepositLimitReachedException;
import com.ippon.bankapp.service.exception.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {


    private AccountRepository accountRepository;
    private NotificationFactory notificationFactory;
    private final TransactionRepository transactionRepository;
    private int count;
    private BigDecimal depoSum;
    private BigDecimal depoLimit;
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
        depoLimit = getDepositLimit(account);
        if (depoLimit.add(amountVal).compareTo(BigDecimal.valueOf(5000)) != 1){
            account.setBalance(account.getBalance().add(amountVal));

            Transaction deposit = new Transaction("deposit", amountVal, account, LocalDate.now());
            transactionRepository.save(deposit);
        }
        else{
            throw new DepositLimitReachedException();
        }

        Account save = accountRepository.save(account);
        return mapAccountToDTO(save);
    }

    public BigDecimal getDepositLimit(Account account){
        count = 0;
        depoSum = BigDecimal.ZERO;
        ArrayList<Transaction> transactions = transactionRepository.findAllByAccount(account);
        while(transactions.size() > count) {
            if ((transactions.get(count).getDateTime().isEqual(LocalDate.now())) && (transactions.get(count).getTransactionType() == "deposit")){
                depoSum = depoSum.add(transactions.get(count).getAmount());
            }
            count++;
        }
        return depoSum;
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
            throw new InsufficientFundsException();
        }
        Transaction withdrawal = new Transaction("withdrawal", amountVal, account, LocalDate.now());
        transactionRepository.save(withdrawal);
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
        System.out.println(amountVal);
        System.out.println(account1.getBalance());
        if (amountVal.compareTo(account1.getBalance()) != 1) {
            account1.setBalance(account1.getBalance().subtract(amountVal));
            account2.setBalance(account2.getBalance().add(amountVal));
            System.out.println("transfering");
        }
        Transaction transferSent = new Transaction("Transfer sent", amountVal, account1, LocalDate.now());
        Transaction transferReceived = new Transaction("Transfer received", amountVal, account2, LocalDate.now());
        transactionRepository.save(transferSent);
        transactionRepository.save(transferReceived);

        Account save1 = accountRepository.save(account1);
        Account save2 = accountRepository.save(account2);
        System.out.println("val");
        System.out.println(save2.getBalance());
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

    private TransactionDTO mapTransactionToDTO(Transaction transaction){
        return new TransactionDTO()
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .account(transaction.getAccount())
                .dateTime(transaction.getDateTime());
    }

   public List<Transaction> getLastTransaction(int id) {
       Account account = accountRepository
               .findById(id)
               .orElseThrow(AccountNotFoundException::new);
        ArrayList<Transaction> transactions = transactionRepository.findAllByAccount(account);
        if(transactions.size() < 10) {
            return transactions.subList(0, transactions.size());
        }
        else{
            return transactions.subList(transactions.size() - 10, transactions.size());
        }
    }


}
