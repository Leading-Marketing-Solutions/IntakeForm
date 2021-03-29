package com.lmsplus.intakeform.dao.repository;

import com.lmsplus.intakeform.dao.entity.FieldValues;
import com.lmsplus.intakeform.dao.entity.IntakeForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldValuesRepository extends JpaRepository<FieldValues, Integer> {

    public List<FieldValues> getByIntakeForm(IntakeForm intakeForm);

    public FieldValues getByIntakeFormAndName(IntakeForm intakeForm, String fieldName);

    public void deleteByValue(String value);

}
