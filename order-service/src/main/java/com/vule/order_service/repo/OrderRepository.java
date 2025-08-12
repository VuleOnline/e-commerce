package com.vule.order_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vule.order_service.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
