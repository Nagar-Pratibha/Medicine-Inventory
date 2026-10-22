package com.example.Meds.controller;

import com.example.Meds.dto.AddressDTO;
import com.example.Meds.dto.AddressResponseDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.mapper.AddressMapper;
import com.example.Meds.security.OwnershipValidator;
import com.example.Meds.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
// The URL is updated to be more RESTful, nesting a user's addresses under that user's endpoint.
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;
    private final OwnershipValidator ownershipValidator;

    @Autowired
    public AddressController(AddressService addressService, AddressMapper addressMapper,
                             OwnershipValidator ownershipValidator) {
        this.addressService = addressService;
        this.addressMapper = addressMapper;
        this.ownershipValidator = ownershipValidator;
    }

    /**
     * Creates a new address for a specific user.
     */
    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            @PathVariable Integer userId,
            @Valid @RequestBody AddressDTO addressDTO) {

        ownershipValidator.assertCanAccessUser(userId);

        // The service layer handles creating the address entity
        Address savedAddress = addressService.addAddress(userId, addressDTO);

        // The mapper converts the saved entity to a response DTO
        AddressResponseDTO response = addressMapper.toAddressResponseDTO(savedAddress);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all addresses for a specific user.
     */
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(@PathVariable Integer userId) {
        ownershipValidator.assertCanAccessUser(userId);
        List<Address> addresses = addressService.getAddressesByUserId(userId);

        // Map the list of entities to a list of response DTOs for the API response
        List<AddressResponseDTO> response = addresses.stream()
                .map(addressMapper::toAddressResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Sets a specific address as the default for the user.
     */
    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDTO> setDefault(
            @PathVariable Integer userId,
            @PathVariable Long addressId) {

        ownershipValidator.assertCanAccessUser(userId);
        Address updatedAddress = addressService.setDefaultAddress(addressId, userId);
        AddressResponseDTO response = addressMapper.toAddressResponseDTO(updatedAddress);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a specific address.
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Integer userId,
            @PathVariable Long addressId) {
        ownershipValidator.assertCanAccessUser(userId);
        // userId is passed through so the service can confirm the address is this user's.
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}