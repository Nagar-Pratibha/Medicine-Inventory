package com.example.Meds.service;

import com.example.Meds.dto.AddressDTO;
import com.example.Meds.entity.Address;
import java.util.List;

public interface AddressService {
    // Updated to accept userId and a DTO
    Address addAddress(Integer userId, AddressDTO addressDTO);

    List<Address> getAddressesByUserId(Integer userId);

    Address setDefaultAddress(Long addressId, Integer userId);

    void deleteAddress(Long addressId, Integer userId);
}