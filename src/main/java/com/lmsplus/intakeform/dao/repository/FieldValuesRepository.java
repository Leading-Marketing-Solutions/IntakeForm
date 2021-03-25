package com.lmsplus.intakeform.dao.repository;

import com.lmsplus.intakeform.dao.entity.IntakeForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldValuesRepository extends JpaRepository<IntakeForm, Integer> {


}
