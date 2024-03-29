package com.lmsplus.intakeform.web.rest;

import com.lmsplus.intakeform.dao.entity.FieldValues;
import com.lmsplus.intakeform.dao.entity.IntakeForm;
import com.lmsplus.intakeform.dao.repository.FieldValuesRepository;
import com.lmsplus.intakeform.dao.repository.IntakeFormRepository;
import com.lmsplus.intakeform.service.notify.TelegramNotifier;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api")
public class APIController {


    @Value("${logos.path}")
    private String LOGOS_PATH;

    @Value("${images.path}")
    private String IMAGES_PATH;

    @Value("${telegram.admin.chat_id}")
    private Long TELEGRAM_ADMIN_ID;

    private Set imgSet = new HashSet<>();

    private IntakeFormRepository intakeFormRepository;
    private FieldValuesRepository fieldValuesRepository;
    private TelegramNotifier telegramNotifier;

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);


    public APIController(IntakeFormRepository intakeFormRepository,
                         FieldValuesRepository fieldValuesRepository,
                         TelegramNotifier telegramNotifier)
    {
        this.intakeFormRepository = intakeFormRepository;
        this.fieldValuesRepository = fieldValuesRepository;
        this.telegramNotifier = telegramNotifier;

        imgSet.add("jpg");
        imgSet.add("jpeg");
        imgSet.add("png");
    }


    @GetMapping("test")
    public String test()
    {
        return "it works";
    }

    @RequestMapping("set")
    public String setValue(@RequestParam String hash, @RequestParam String field, @RequestParam String value)
    {

        logger.info("form: " + hash + ", field: " + field + ", value: " + value);

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
    public String getValue(@RequestParam String hash, @RequestParam String field)
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

    @RequestMapping("logo")
    public String uploadLogo(@RequestParam MultipartFile file, @RequestParam String hash) throws IOException
    {
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if(!checkImgExt(ext)){
            return "ERROR";
        }

        String newFileName = hash + "_logo." + ext;
        String filePath = LOGOS_PATH + newFileName;
        File dest = new File(filePath);
        file.transferTo(dest);

        return newFileName;
    }

    @RequestMapping("image")
    public String uploadImage(@RequestParam MultipartFile file, @RequestParam String hash, @RequestParam String htmlId) throws IOException
    {
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if(!checkImgExt(ext)){
            return "ERROR";
        }

        String newFileName = hash + "_" + htmlId + "." + ext;
        String filePath = IMAGES_PATH + newFileName;
        File dest = new File(filePath);
        file.transferTo(dest);

        return newFileName;
    }


    @RequestMapping("images/{hash}")
    public String[] getAllImages(@PathVariable String hash)
    {
        IntakeForm intakeForm = intakeFormRepository.findByHash(hash);
        if(intakeForm == null)
            return new String[0];

        List<FieldValues> fieldValues = fieldValuesRepository.getByIntakeForm(intakeForm);

        List<String> strList =  fieldValues.stream().filter(val -> val.getName().startsWith("img_")).map(e -> e.getValue()).collect(Collectors.toList());

        return strList.toArray(new String[0]);

    }

    @Transactional
    @DeleteMapping("image/{value}")
    public void deleteImage(@PathVariable String value)
    {
        fieldValuesRepository.deleteByValue(value);
    }


    @PostMapping("/{hash}/submit")
    public void submitForm(@PathVariable String hash)
    {
        IntakeForm intakeForm = intakeFormRepository.findByHash(hash);
        intakeForm.setStatus(2);
        intakeFormRepository.save(intakeForm);

        telegramNotifier.notify("intake form with hash = " + hash + " is submitted", TELEGRAM_ADMIN_ID);

    }

//----------------------------------------------------------------------------------------------------------------------


    private boolean checkImgExt(String ext)
    {
        if(imgSet.contains(ext.toLowerCase()))
            return true;
        else
            return false;
    }

    private void checkDir(String path, String hash)
    {
        try
        {
            File dir = new File(path + hash);
            if(!dir.exists())
                dir.mkdir();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
