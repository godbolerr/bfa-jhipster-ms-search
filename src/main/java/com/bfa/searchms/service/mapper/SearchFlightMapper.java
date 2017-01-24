package com.bfa.searchms.service.mapper;

import com.bfa.searchms.domain.*;
import com.bfa.searchms.service.dto.SearchFlightDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity SearchFlight and its DTO SearchFlightDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SearchFlightMapper {

//    @Mapping(source = "sFlightFare.id", target = "sFlightFareId")
//   @Mapping(source = "sFlightInv.id", target = "sFlightInvId")
    SearchFlightDTO searchFlightToSearchFlightDTO(SearchFlight searchFlight);

    List<SearchFlightDTO> searchFlightsToSearchFlightDTOs(List<SearchFlight> searchFlights);

//    @Mapping(source = "sFlightFareId", target = "sFlightFare")
//    @Mapping(source = "sFlightInvId", target = "sFlightInv")
    SearchFlight searchFlightDTOToSearchFlight(SearchFlightDTO searchFlightDTO);

    List<SearchFlight> searchFlightDTOsToSearchFlights(List<SearchFlightDTO> searchFlightDTOs);

    default SearchFares searchFaresFromId(Long id) {
        if (id == null) {
            return null;
        }
        SearchFares searchFares = new SearchFares();
        searchFares.setId(id);
        return searchFares;
    }

    default SearchInventory searchInventoryFromId(Long id) {
        if (id == null) {
            return null;
        }
        SearchInventory searchInventory = new SearchInventory();
        searchInventory.setId(id);
        return searchInventory;
    }
}
