package com.bfa.searchms.service;

import com.bfa.searchms.service.dto.SearchFlightDTO;
import java.util.List;

/**
 * Service Interface for managing SearchFlight.
 */
public interface SearchFlightService {

    /**
     * Save a searchFlight.
     *
     * @param searchFlightDTO the entity to save
     * @return the persisted entity
     */
    SearchFlightDTO save(SearchFlightDTO searchFlightDTO);

    /**
     *  Get all the searchFlights.
     *  
     *  @return the list of entities
     */
    List<SearchFlightDTO> findAll();

    /**
     *  Get the "id" searchFlight.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    SearchFlightDTO findOne(Long id);

    /**
     *  Delete the "id" searchFlight.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Initialize the search flight database.
     * 
     * @param flights
     * @return
     */
	List<SearchFlightDTO> init(List<SearchFlightDTO> flights);
	
	
	/**
	 * Search flights based on the criterion 
	 * @param dto
	 * @return
	 */
	List<SearchFlightDTO> find(SearchFlightDTO dto);

	
	
}
