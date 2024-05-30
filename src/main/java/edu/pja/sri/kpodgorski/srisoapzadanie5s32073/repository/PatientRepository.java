package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository;

import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.Patient;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PatientRepository extends CrudRepository<Patient, Long> {
    @Override
    List<Patient> findAll();
}
