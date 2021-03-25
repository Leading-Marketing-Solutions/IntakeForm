package com.lmsplus.intakeform.web.rest;

import com.lmsplus.intakeform.dao.entity.FieldValues;
import com.lmsplus.intakeform.dao.entity.IntakeForm;
import com.lmsplus.intakeform.dao.repository.FieldValuesRepository;
import com.lmsplus.intakeform.dao.repository.IntakeFormRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("api")
public class APIController {


    @Value("${logos.path}")
    private String LOGOS_PATH;

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
    public String uploadLogo(@RequestParam MultipartFile file) throws IOException
    {
        String orgName = file.getOriginalFilename().replaceAll(" ", "");
        String filePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + LOGOS_PATH + orgName;
        File dest = new File(filePath);
        file.transferTo(dest);

        return "OK";
    }

    @RequestMapping("/logo/{name}")
    @ResponseBody
    public HttpEntity<byte[]> getArticleImage(@PathVariable String name) throws IOException
    {
        String fileName = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + LOGOS_PATH + name;
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        byte[] bytes = extractBytes(fileName, ext);

        if(ext.equals("png"))
            ext = "image/png";
        else if(ext.equals("jpg") || ext.equals("jpeg"))
            ext = "image/jpeg";

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ext));

        return new ResponseEntity<>(bytes, headers, OK);


    }


    public byte[] extractBytes (String ImageName, String ext)  {

        try
        {
            ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
            BufferedImage img=ImageIO.read(new File(ImageName));
            ImageIO.write(img, ext, baos);
            baos.flush();

            return baos.toByteArray();
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
