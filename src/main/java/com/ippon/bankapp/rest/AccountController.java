package com.ippon.bankapp.rest;

import com.ippon.bankapp.service.AccountService;
import com.ippon.bankapp.service.dto.AccountDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDTO createAccount(@Valid @RequestBody AccountDTO newAccount) {
        return accountService.createAccount(newAccount);
    }

    @PostMapping("/deposit/{id}/{amount}")
    public AccountDTO accountDeposit(@PathVariable int id, @PathVariable BigDecimal amount) {
        return accountService.depositId(id, amount);
    }

    @PostMapping("/withdawal/{id}/{amount}")
    public AccountDTO accountWithdrawal(@PathVariable int id, @PathVariable BigDecimal amount) {
        return accountService.withdrawId(id, amount);
    }

    @GetMapping("/account/{lastName}")
    public AccountDTO account(@PathVariable(name = "lastName") String lastName) {
        return accountService.getAccount(lastName);
    }

    @GetMapping("/account/first/{fout irstName}")
    public AccountDTO accountFirstName(@PathVariable(name = "firstName") String firstName) {
        return accountService.getAccountFirstName(firstName);
    }
}
