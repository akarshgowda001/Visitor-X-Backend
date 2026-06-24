package com.visitor_x.repository;

import com.visitor_x.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findByEmail(String email);

    Optional<Visitor> findByMobileNumber(String mobileNumber);

    long countByVisitDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Search by name OR mobile — matches the image
    @Query("SELECT v FROM Visitor v WHERE " +
            "LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "v.mobileNumber LIKE CONCAT('%', :keyword, '%')")
    Page<Visitor> searchByNameOrMobile(
            @Param("keyword") String keyword, Pageable pageable);

    // Paginated full list
    Page<Visitor> findAll(Pageable pageable);

    // Today's visitors list
    List<Visitor> findByVisitDateTimeBetween(
            LocalDateTime start, LocalDateTime end);
    boolean existsByEmailAndVisitorIdNot(
            String email,
            Long visitorId);

    boolean existsByMobileNumberAndVisitorIdNot(
            String mobileNumber,
            Long visitorId);
}