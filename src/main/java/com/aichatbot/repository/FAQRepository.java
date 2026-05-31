package com.aichatbot.repository;

import com.aichatbot.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByActiveTrue();

    @Query("SELECT f FROM FAQ f WHERE f.active = true AND " +
           "(LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.answer) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<FAQ> searchFAQs(String keyword);

    List<FAQ> findByCategoryAndActiveTrue(String category);
}
