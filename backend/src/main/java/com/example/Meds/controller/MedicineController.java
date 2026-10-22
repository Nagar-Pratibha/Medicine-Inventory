package com.example.Meds.controller;

import com.example.Meds.dto.MedicineDTO;
import com.example.Meds.dto.MedicineResponseDTO;
import com.example.Meds.entity.Medicine;
import com.example.Meds.mapper.MedicineMapper;
import com.example.Meds.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    @Autowired
    public MedicineController(MedicineService medicineService, MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @PostMapping
    public ResponseEntity<MedicineResponseDTO> addMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {
        Medicine savedMedicine = medicineService.addMedicine(medicineDTO);
        MedicineResponseDTO response = medicineMapper.toMedicineResponseDTO(savedMedicine);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicine(@PathVariable Long id) {
        Medicine medicine = medicineService.getMedicineById(id);
        return ResponseEntity.ok(medicineMapper.toMedicineResponseDTO(medicine));
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        List<Medicine> medicines = medicineService.getAllMedicines();
        List<MedicineResponseDTO> response = medicines.stream()
                .map(medicineMapper::toMedicineResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(@PathVariable Long id,
                                                              @Valid @RequestBody MedicineDTO medicineDTO) {
        Medicine updatedMedicine = medicineService.updateMedicine(id, medicineDTO);
        return ResponseEntity.ok(medicineMapper.toMedicineResponseDTO(updatedMedicine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }
}