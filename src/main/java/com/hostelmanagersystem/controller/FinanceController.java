package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.ContractRequest;
import com.hostelmanagersystem.dto.request.ExpenseRequest;
import com.hostelmanagersystem.dto.request.IncomeRequest;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.dto.response.ExpenseResponse;
import com.hostelmanagersystem.dto.response.IncomeResponse;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.service.ContractService;
import com.hostelmanagersystem.service.FinanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/owner")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class FinanceController {
    FinanceService financeService;
    ContractService contractService;

    @PostMapping("/income")
    public ResponseEntity<IncomeResponse> addIncome(@RequestParam String userId, @RequestBody IncomeRequest request) {
        return ResponseEntity.ok(financeService.createIncome(userId, request));
    }

    @PostMapping("/expense")
    public ResponseEntity<ExpenseResponse> addExpense(@RequestParam String userId, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(financeService.createExpense(userId, request));
    }

    @GetMapping("/incomes")
    public ResponseEntity<List<IncomeResponse>> getIncomes(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(financeService.getIncomes(userId, from, to));
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(financeService.getExpenses(userId, from, to));
    }
    @PostMapping("/contracts")
    public ResponseEntity<ContractResponse> createContract(@RequestBody ContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createContract(request));
    }
}
