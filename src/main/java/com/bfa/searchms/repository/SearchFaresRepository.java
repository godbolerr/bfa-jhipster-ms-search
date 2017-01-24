package com.bfa.searchms.repository;

import com.bfa.searchms.domain.SearchFares;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SearchFares entity.
 */
@SuppressWarnings("unused")
public interface SearchFaresRepository extends JpaRepository<SearchFares,Long> {

}
