package com.example.Meds.repository;

import com.example.Meds.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Order,Long> {

}
