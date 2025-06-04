package com.example.shopapp.repository;

import com.example.shopapp.entity.Order;
import com.example.shopapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAll(Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user.id = :userId")
    List<Order> findAllByUserIdWithItems(@Param("userId") Long userId);

    @Query(value = """
    SELECT DISTINCT o FROM Order o
    LEFT JOIN FETCH o.orderItems oi
    LEFT JOIN FETCH oi.product
    WHERE o.user = :user
    """,
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    Page<Order> findAllWithItemsAndProductsByUser(@Param("user") User user, Pageable pageable);

}
