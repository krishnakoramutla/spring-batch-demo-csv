package com.batchprocess.batch.demo.repository;

import com.batchprocess.batch.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
