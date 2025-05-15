package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findAllByUserIdAndDateBetween(String userId, LocalDate from, LocalDate to);
}
