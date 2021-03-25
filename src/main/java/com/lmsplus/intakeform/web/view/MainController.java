package com.lmsplus.intakeform.web.view;

import com.lmsplus.intakeform.dao.entity.IntakeForm;
import com.lmsplus.intakeform.dao.repository.IntakeFormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private IntakeFormRepository intakeFormRepository;

    public MainController(IntakeFormRepository intakeFormRepository){
        this.intakeFormRepository = intakeFormRepository;
    }


    @GetMapping("/{hash}")
    public String showForm(@PathVariable String hash, ModelMap model)
    {
        IntakeForm intakeForm = intakeFormRepository.findByHash(hash);
        if(intakeForm == null)
            return "error";

        return "index";
    }


}
