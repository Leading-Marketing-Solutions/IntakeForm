package com.lmsplus.intakeform.web.view;

import com.lmsplus.intakeform.dao.entity.IntakeForm;
import com.lmsplus.intakeform.dao.repository.IntakeFormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Value("${logos.path}")
    private String LOGOS_PATH;

    @Value("${images.path}")
    private String IMAGES_PATH;

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

        model.put("status", intakeForm.getStatus());

        return "form";
    }

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnFavicon()
    {}


    @RequestMapping("/logo/{name}")
    @ResponseBody
    public HttpEntity<byte[]> getLogo(@PathVariable String name) throws IOException
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

    @RequestMapping("/image/{name}")
    @ResponseBody
    public HttpEntity<byte[]> getImage(@PathVariable String name) throws IOException
    {
        String fileName = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + IMAGES_PATH + name;
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




    //----------------------------------------------------------------------------------------------------------------------
    public byte[] extractBytes (String ImageName, String ext)  {

        try
        {
            ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
            BufferedImage img= ImageIO.read(new File(ImageName));
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
