package com.example.Meds.mapper;

import com.example.Meds.dto.MedicineDTO;
import com.example.Meds.dto.MedicineResponseDTO;
import com.example.Meds.entity.Medicine;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper {

    /**
     * Converts a MedicineDTO (from a request) to a Medicine entity.
     * This is typically used when creating a new medicine.
     */
    public Medicine toMedicineEntity(MedicineDTO dto) {
        if (dto == null) {
            return null;
        }

        Medicine entity = new Medicine();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setManufacture_name(dto.getManufacture_name());
        entity.setPack_size(dto.getPack_size());
        entity.setCompositionText(dto.getCompositionText());

        return entity;
    }

    /**
     * Converts a Medicine entity (from the database) to a MedicineResponseDTO.
     * This is used for all API responses to avoid exposing the entity directly.
     */
    public MedicineResponseDTO toMedicineResponseDTO(Medicine entity) {
        if (entity == null) {
            return null;
        }

        MedicineResponseDTO dto = new MedicineResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setManufacture_name(entity.getManufacture_name());
        dto.setPack_size(entity.getPack_size());
        dto.setCompositionText(entity.getCompositionText());

        return dto;
    }
}