package com.example.Meds.mapper;

import com.example.Meds.dto.AddressDTO;
import com.example.Meds.dto.AddressResponseDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    /**
     * Converts an AddressDTO to an Address entity.
     * The User object must be provided separately since the DTO only has the userId.
     */
    public Address toAddressEntity(AddressDTO addressDTO, User user) {
        if (addressDTO == null) return null;

        Address address = new Address();
        address.setUser(user); // Set the full User object
        address.setLine1(addressDTO.getLine1());
        address.setLine2(addressDTO.getLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        address.setDefault(addressDTO.isDefault());
        return address;
    }

    /**
     * Converts an Address entity to an AddressResponseDTO.
     */
    public AddressResponseDTO toAddressResponseDTO(Address address) {
        if (address == null) return null;

        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setAddressId(address.getAddressId());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setDefault(address.isDefault());
        return dto;
    }
}