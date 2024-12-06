package ru.tbank.contestservice.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.contestservice.model.entities.ContestEntity;

@Repository
public interface ContestRepository extends JpaRepository<ContestEntity, Long> {
}
