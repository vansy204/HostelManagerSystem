package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.ExpenseRequest;
import com.hostelmanagersystem.dto.request.IncomeRequest;
import com.hostelmanagersystem.dto.response.ExpenseResponse;
import com.hostelmanagersystem.dto.response.IncomeResponse;
import com.hostelmanagersystem.entity.manager.Expense;
import com.hostelmanagersystem.entity.manager.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinanceMapper {
    Income toIncome(IncomeRequest request);

    @Mapping(target = "id", source = "id")
    IncomeResponse toIncomeResponse(Income income);

    Expense toExpense(ExpenseRequest request);

    @Mapping(target = "id", source = "id")
    ExpenseResponse toExpenseResponse(Expense expense);
}
