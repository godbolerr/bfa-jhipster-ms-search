package com.bfa.searchms.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bfa.searchms.domain.SearchFares;
import com.bfa.searchms.domain.SearchFlight;
import com.bfa.searchms.domain.SearchInventory;
import com.bfa.searchms.repository.SearchFlightRepository;
import com.bfa.searchms.service.SearchFlightService;
import com.bfa.searchms.service.dto.FaresDTO;
import com.bfa.searchms.service.dto.InventoryDTO;
import com.bfa.searchms.service.dto.SearchFlightDTO;
import com.bfa.searchms.service.mapper.SearchFlightMapper;

/**
 * Service Implementation for managing SearchFlight.
 */
@Service
@Transactional
public class SearchFlightServiceImpl implements SearchFlightService{

    private final Logger log = LoggerFactory.getLogger(SearchFlightServiceImpl.class);
    
    @Inject
    private SearchFlightRepository searchFlightRepository;

    @Inject
    private SearchFlightMapper searchFlightMapper;
    
	@Autowired
	@Qualifier("myRestTemplate")
	private OAuth2RestOperations restTemplate;    

    /**
     * Save a searchFlight.
     *
     * @param searchFlightDTO the entity to save
     * @return the persisted entity
     */
    public SearchFlightDTO save(SearchFlightDTO searchFlightDTO) {
        log.debug("Request to save SearchFlight : {}", searchFlightDTO);
        SearchFlight searchFlight = searchFlightMapper.searchFlightDTOToSearchFlight(searchFlightDTO);
        searchFlight = searchFlightRepository.save(searchFlight);
        SearchFlightDTO result = searchFlightMapper.searchFlightToSearchFlightDTO(searchFlight);
        return result;
    }

    /**
     *  Get all the searchFlights.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<SearchFlightDTO> findAll() {
        log.debug("Request to get all SearchFlights");
        List<SearchFlightDTO> result = searchFlightRepository.findAll().stream()
            .map(searchFlightMapper::searchFlightToSearchFlightDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one searchFlight by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public SearchFlightDTO findOne(Long id) {
        log.debug("Request to get SearchFlight : {}", id);
        SearchFlight searchFlight = searchFlightRepository.findOne(id);
        SearchFlightDTO searchFlightDTO = searchFlightMapper.searchFlightToSearchFlightDTO(searchFlight);
        return searchFlightDTO;
    }

    /**
     *  Delete the  searchFlight by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete SearchFlight : {}", id);
        searchFlightRepository.delete(id);
    }
    
	/**
	 * Initalize initial flight database.
	 */
	@Override
	public List<SearchFlightDTO> init(List<SearchFlightDTO> flights) {

		List<SearchFlight> fList = searchFlightRepository.findAll();

		// Populate only for the first time.
		if (fList != null && fList.size() == 0) {

			for (Iterator iterator = flights.iterator(); iterator.hasNext();) {
				SearchFlightDTO searchFlightDTO = (SearchFlightDTO) iterator.next();

				SearchFlight searchFlight = searchFlightMapper.searchFlightDTOToSearchFlight(searchFlightDTO);
				SearchFares sFares = new SearchFares();
				sFares.setFare(searchFlightDTO.getFare() + "");
				sFares.setCurrency("USD");
				searchFlight.setSFlightFare(sFares);

				SearchInventory sI = new SearchInventory();
				sI.setCount(Integer.parseInt(searchFlightDTO.getInventory() + ""));
				searchFlight.setSFlightInv(sI);

				searchFlight = searchFlightRepository.save(searchFlight);

				searchFlightDTO.setId(searchFlight.getId());
				
				

				JSONObject newInventoryRequest = new JSONObject();
				try {
					newInventoryRequest.put("flightDate", searchFlightDTO.getFlightDate());
					newInventoryRequest.put("fligthNumber", searchFlightDTO.getFlightNumber());
					newInventoryRequest.put("available", searchFlightDTO.getInventory().intValue());	
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				
				HttpEntity<String> entity = new HttpEntity<String>(newInventoryRequest.toString(), headers);
				

				restTemplate.exchange(
						"http://localhost:11000/bookms/api/inventories", HttpMethod.POST, entity,
						new ParameterizedTypeReference<InventoryDTO>() {
						});


	
				JSONObject newFareRequest = new JSONObject();
				try {
					newFareRequest.put("currency", "USD");
					newFareRequest.put("fligthNumber", searchFlightDTO.getFlightNumber());
					newFareRequest.put("flightDate", searchFlightDTO.getFlightDate());
					newFareRequest.put("fare", searchFlightDTO.getFare());	
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				HttpEntity<String> fareEntity = new HttpEntity<String>(newFareRequest.toString(), headers);
				

				restTemplate.exchange(
						"http://localhost:11000/faresms/api/fares", HttpMethod.POST, fareEntity,
						new ParameterizedTypeReference<FaresDTO>() {
						});



			}

		}

		return flights;
	}
	


	@Override
	public List<SearchFlightDTO> find(SearchFlightDTO dto) {

		List<SearchFlightDTO> dtoList = new ArrayList<SearchFlightDTO>();

		List<SearchFlight> sfList = searchFlightRepository.findByOriginAndDestinationAndFlightDate(dto.getOrigin(),
				dto.getDestination(), dto.getFlightDate());

		for (Iterator iterator = sfList.iterator(); iterator.hasNext();) {
			SearchFlight searchFlight = (SearchFlight) iterator.next();
			SearchFares sf = searchFlight.getSFlightFare();
			SearchInventory si = searchFlight.getSFlightInv();
			SearchFlightDTO searchFlightDTO = searchFlightMapper.searchFlightToSearchFlightDTO(searchFlight);
			if (sf != null) {
				searchFlightDTO.setFare(Long.parseLong(sf.getFare()));
			}
			dtoList.add(searchFlightDTO);

//			InventoryDTO invDto = invService.findByFlightNumberAndFlightDate(searchFlightDTO.getFlightNumber(),
//					searchFlightDTO.getFlightDate());
//
//			int availableSeats = invDto.getAvailable();

//			if (invDto != null) {
//				searchFlightDTO.setInventory(new Long(invDto.getAvailable()));
//			}
//
//			if (availableSeats != 0) {
//				dtoList.add(searchFlightDTO);
//			}
		}

		return dtoList;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
