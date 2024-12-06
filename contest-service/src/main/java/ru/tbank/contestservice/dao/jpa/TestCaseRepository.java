package ru.tbank.contestservice.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.TestCaseEntity;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {

    void removeAllByProblem(ProblemEntity problem);

}
