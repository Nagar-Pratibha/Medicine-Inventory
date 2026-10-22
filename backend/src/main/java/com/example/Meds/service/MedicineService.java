package com.example.Meds.service;

import com.example.Meds.dto.MedicineDTO;
import com.example.Meds.entity.Medicine;
import java.util.List;

public interface MedicineService {

    Medicine addMedicine(MedicineDTO medicineDTO);

    Medicine getMedicineById(Long id);

    List<Medicine> getAllMedicines();

    Medicine updateMedicine(Long id, MedicineDTO medicineDTO);

    void deleteMedicine(Long id);
}