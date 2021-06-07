package com.ippon.bankapp.rest;

import com.fasterxml.jackson.databind.node.TextNode;
import com.ippon.bankapp.service.AccountService;
import com.ippon.bankapp.service.dto.AccountDTO;
import com.ippon.bankapp.service.dto.AmountDTO;
import com.ippon.bankapp.service.dto.TransactionDTO;
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

    @PostMapping("/deposit/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDTO accountDeposit(@Valid @RequestBody AmountDTO amount, @PathVariable int id) {
        return accountService.depositId(id, amount);
    }

    @PostMapping("/withdraw/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDTO accountWithdrawal(@Valid @RequestBody AmountDTO amount, @PathVariable int id) {
        return accountService.withdrawId(id, amount);
    }

    @PostMapping("/transfer/{id1}/{id2}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDTO accountWithdrawal(@Valid @RequestBody AmountDTO amount, @PathVariable int id1, @PathVariable int id2) {
        return accountService.transferBal(id1, id2, amount);
    }

    @GetMapping("/account/{lastName}")
    public AccountDTO account(@PathVariable(name = "lastName") String lastName) {
        return accountService.getAccount(lastName);
    }

    @GetMapping("/account/first/{firstName}")
    public AccountDTO accountFirstName(@PathVariable(name = "firstName") String firstName) {
        return accountService.getAccountFirstName(firstName);
    }

}
