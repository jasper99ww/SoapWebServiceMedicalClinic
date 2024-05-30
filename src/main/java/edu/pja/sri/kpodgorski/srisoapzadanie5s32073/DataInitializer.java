package edu.pja.sri.kpodgorski.srisoapzadanie5s32073;

import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.model.Patient;
import edu.pja.sri.kpodgorski.srisoapzadanie5s32073.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    private final PatientRepository patientRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initData();
    }

    private void initData() {
        Patient p1 = Patient.builder()
                .firstName("Daniel")
                .lastName("Jeziorny")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        Patient p2 = Patient.builder()
                .firstName("Adam")
                .lastName("Kaczuba")
                .dateOfBirth(LocalDate.of(1991, 1, 1))
                .build();

        Patient p3 = Patient.builder()
                .firstName("Anna")
                .lastName("Kowalska")
                .dateOfBirth(LocalDate.of(1992, 1, 1))
                .build();

        patientRepository.saveAll(Arrays.asList(p1, p2, p3));
        LOG.info("Data initialized");
    }
}