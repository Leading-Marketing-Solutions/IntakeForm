package com.lmsplus.intakeform.web.rest;

import com.lmsplus.intakeform.dao.entity.FieldValues;
import com.lmsplus.intakeform.dao.entity.IntakeForm;
import com.lmsplus.intakeform.dao.repository.FieldValuesRepository;
import com.lmsplus.intakeform.dao.repository.IntakeFormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class APIController {

    private IntakeFormRepository intakeFormRepository;
    private FieldValuesRepository fieldValuesRepository;

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);


    public APIController(IntakeFormRepository intakeFormRepository,
                         FieldValuesRepository fieldValuesRepository)
    {
        this.intakeFormRepository = intakeFormRepository;
        this.fieldValuesRepository = fieldValuesRepository;
    }


    @GetMapping("test")
    public String test()
    {
        return "it works";
    }

    @RequestMapping("set")
    public String setValue(@RequestParam String hash, @RequestParam String field, @RequestParam String value)
    {
        IntakeForm intakeForm = intakeFormRepository.findByHash(hash);
        if(intakeForm == null)
            return "data not found";

        FieldValues fieldValues = fieldValuesRepository.getByIntakeFormAndName(intakeForm, field);
        if(fieldValues == null)
        {
            fieldValues = new FieldValues();
            fieldValues.setName(field);
            fieldValues.setIntakeForm(intakeForm);
        }
        fieldValues.setValue(value);
        fieldValuesRepository.save(fieldValues);

        return "OK";
    }


    @RequestMapping("get")
    public String getValue(@RequestParam String hash, @RequestParam String field, @RequestParam String value)
    {
        IntakeForm intakeForm = intakeFormRepository.findByHash(hash);
        if(intakeForm == null)
            return "data not found";

        FieldValues fieldValues = fieldValuesRepository.getByIntakeFormAndName(intakeForm, field);
        if(fieldValues == null)
            return "";
        else
            return fieldValues.getValue();
    }

}
