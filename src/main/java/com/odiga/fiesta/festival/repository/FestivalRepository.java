package com.odiga.fiesta.festival.repository;

import com.odiga.fiesta.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
