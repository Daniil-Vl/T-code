package ru.tbank.contestservice.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.contestservice.model.entities.ProblemEntity;

@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {

    @Query(value = """
            UPDATE problem SET description = :description WHERE id = :problem_id
            """, nativeQuery = true)
    @Modifying
    void updateProblemDescription(
            @Param("problem_id") long problemId,
            @Param("description") String description
    );

}
