package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.ExpenseRequest;
import com.hostelmanagersystem.dto.request.IncomeRequest;
import com.hostelmanagersystem.dto.response.ExpenseResponse;
import com.hostelmanagersystem.dto.response.IncomeResponse;
import com.hostelmanagersystem.entity.manager.Expense;
import com.hostelmanagersystem.entity.manager.Income;
import com.hostelmanagersystem.mapper.FinanceMapper;
import com.hostelmanagersystem.repository.ExpenseRepository;
import com.hostelmanagersystem.repository.IncomeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class FinanceService {
    IncomeRepository incomeRepo;
    ExpenseRepository expenseRepo;
    FinanceMapper financeMapper;

    public IncomeResponse createIncome(String userId, IncomeRequest req) {
        Income income = financeMapper.toIncome(req);
        income.setUserId(userId);
        return financeMapper.toIncomeResponse(incomeRepo.save(income));
    }

    public ExpenseResponse createExpense(String userId, ExpenseRequest req) {
        Expense expense = financeMapper.toExpense(req);
        expense.setUserId(userId);
        return financeMapper.toExpenseResponse(expenseRepo.save(expense));
    }

    public List<IncomeResponse> getIncomes(String userId, LocalDate from, LocalDate to) {
        return incomeRepo.findAllByUserIdAndDateBetween(userId, from, to)
                .stream()
                .map(financeMapper::toIncomeResponse)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpenses(String userId, LocalDate from, LocalDate to) {
        return expenseRepo.findAllByUserIdAndDateBetween(userId, from, to)
                .stream()
                .map(financeMapper::toExpenseResponse)
                .collect(Collectors.toList());
    }
}
