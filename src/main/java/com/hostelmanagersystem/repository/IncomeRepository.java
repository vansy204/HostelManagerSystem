package com.hostelmanagersystem.repository;


import com.hostelmanagersystem.entity.manager.Income;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends MongoRepository<Income, String> {
    List<Income> findAllByUserIdAndDateBetween(String userId, LocalDate from, LocalDate to);
}
