package com.example.Meds.service.impl;

import com.example.Meds.dto.AddressDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.entity.User;
import com.example.Meds.exception.ForbiddenException;
import com.example.Meds.exception.IllegalArgumentException;
import com.example.Meds.exception.ResourceNotFoundException;

import com.example.Meds.mapper.AddressMapper;
import com.example.Meds.repository.AddressRepository;
import com.example.Meds.repository.UsersRepository;
import com.example.Meds.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, UsersRepository usersRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.usersRepository = usersRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public Address addAddress(Integer userId, AddressDTO addressDTO) {
        // 1. Find the user this address will belong to
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 2. Use the mapper to create an Address entity from the DTO and User
        Address address = addressMapper.toAddressEntity(addressDTO, user);

        // 3. If the new address is set as default, unset any other default addresses for that user
        if (address.isDefault()) {
            addressRepository.findByUserId(userId).forEach(oldAddress -> {
                if (oldAddress.isDefault()) {
                    oldAddress.setDefault(false);
                    addressRepository.save(oldAddress);
                }
            });
        }

        // 4. Save the new address
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddressesByUserId(Integer userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Address setDefaultAddress(Long addressId, Integer userId) {
        // Find the address that needs to be set as default
        Address targetAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with id: " + addressId));

        // Verify the address belongs to the user
        if (!targetAddress.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address does not belong to the user");
        }

        // Unset the old default address
        addressRepository.findByUserId(userId).forEach(address -> {
            if (address.isDefault()) {
                address.setDefault(false);
                addressRepository.save(address);
            }
        });

        // Set the new default address
        targetAddress.setDefault(true);
        return addressRepository.save(targetAddress);
    }

    @Override
    public void deleteAddress(Long addressId, Integer userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // Addresses are addressed by their own id, so confirm this one is the caller's.
        if (address.getUser() == null || !address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Address " + addressId + " does not belong to user " + userId);
        }

        addressRepository.delete(address);
    }
}