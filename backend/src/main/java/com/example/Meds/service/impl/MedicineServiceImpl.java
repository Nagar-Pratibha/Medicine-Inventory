package com.example.Meds.service.impl;

import com.example.Meds.dto.MedicineDTO;
import com.example.Meds.entity.Medicine;
import com.example.Meds.exception.ResourceNotFoundException;
import com.example.Meds.mapper.MedicineMapper;
import com.example.Meds.repository.MedicineRepository;
import com.example.Meds.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    @Autowired
    public MedicineServiceImpl(MedicineRepository medicineRepository, MedicineMapper medicineMapper) {
        this.medicineRepository = medicineRepository;
        this.medicineMapper = medicineMapper;
    }

    @Override
    public Medicine addMedicine(MedicineDTO medicineDTO) {
        Medicine medicine = medicineMapper.toMedicineEntity(medicineDTO);
        return medicineRepository.save(medicine);
    }

    @Override
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));
    }

    @Override
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    @Override
    public Medicine updateMedicine(Long id, MedicineDTO updatedMedicineDTO) {
        Medicine existing = getMedicineById(id);

        // Apply updates from the DTO to the existing entity
        existing.setName(updatedMedicineDTO.getName());
        existing.setDescription(updatedMedicineDTO.getDescription());
        existing.setPrice(updatedMedicineDTO.getPrice());
        existing.setStock(updatedMedicineDTO.getStock());
        existing.setManufacture_name(updatedMedicineDTO.getManufacture_name());
        existing.setPack_size(updatedMedicineDTO.getPack_size());
        existing.setCompositionText(updatedMedicineDTO.getCompositionText());

        return medicineRepository.save(existing);
    }

    @Override
    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }
}