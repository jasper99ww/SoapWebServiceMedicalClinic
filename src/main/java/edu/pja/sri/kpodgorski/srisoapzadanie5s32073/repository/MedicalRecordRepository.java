package edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository;

import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.MedicalRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MedicalRecordRepository extends CrudRepository<MedicalRecord, Long> {
    @Override
    List<MedicalRecord> findAll();
}
