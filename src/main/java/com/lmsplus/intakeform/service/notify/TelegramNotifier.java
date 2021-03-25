package com.lmsplus.intakeform.service.notify;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TelegramNotifier {


    private static final ContentType TEXT_PLAIN_CONTENT_TYPE = ContentType.create("text/plain", StandardCharsets.UTF_8);
    private volatile CloseableHttpClient httpclient;
    private volatile RequestConfig requestConfig;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.files.path}")
    private String TEMP_FILES_FOLDER;

    private static final String telegramAPI = "https://api.telegram.org/bot";



    public TelegramNotifier()
    {
        this.requestConfig = RequestConfig.copy(RequestConfig.custom().build())
                .setSocketTimeout(75000)
                .setConnectTimeout(75000)
                .setConnectionRequestTimeout(75000).build();

        this.httpclient = HttpClientBuilder.create()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setConnectionTimeToLive(70, TimeUnit.SECONDS)
                .setMaxConnTotal(100)
                .build();
    }


    public void notify(String message, long userId)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("chat_id", Long.toString(userId));
        map.add("text", message);

        org.springframework.http.HttpEntity<MultiValueMap<String, String>> request = new  org.springframework.http.HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(telegramAPI + token + "/sendmessage", request , String.class );
    }


    public void notifyWithKeyboard(String message, long userId, String[] keys)
    {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        try {
            URI targetUrl = UriComponentsBuilder.fromUriString(telegramAPI + token + "/sendmessage")
                    .queryParam("text", message)
                    .queryParam("chat_id", userId)
                    .build()
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonBody = "{\n" +
                    "\"reply_markup\": {\n" +
                    "\"keyboard\": [\n";
            if(keys != null) {
                for (String key : keys) {
                    jsonBody += "[\"" + key + "\"],\n";

                }
                jsonBody = jsonBody.substring(0, jsonBody.length() - 2);
            }

            jsonBody += "],\n" +
                    "\"one_time_keyboard\": true\n" +
                    "}\n" +
                    "}";

            org.springframework.http.HttpEntity entity = new org.springframework.http.HttpEntity(jsonBody, headers);
            ResponseEntity<String> json =  restTemplate.exchange(targetUrl, HttpMethod.POST, entity, String.class);



            String response = json.toString();
            System.out.println(response);



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public String sendPost(String operation, Map<String,String> params)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        for(Map.Entry<String,String> entry: params.entrySet())
        {
            map.add(entry.getKey(), entry.getValue());
        }


        org.springframework.http.HttpEntity<MultiValueMap<String, String>> request = new  org.springframework.http.HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(telegramAPI + token + operation, request , String.class );
        return response.getBody().toString();
    }

    public void getFile(String filePath, String name)
    {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(
                new ByteArrayHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<String>(headers);

        String url = "https://api.telegram.org/file/bot" + token + "/" + filePath;
        System.out.println(url);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET, entity, byte[].class, "1");

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Files.write(Paths.get(TEMP_FILES_FOLDER + name), response.getBody());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }




    public void sendFile(String path, String id)
    {
        System.out.println("Sending file " + path);
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        FileSystemResource value = new FileSystemResource(new File(path));
        map.add("document", value);
        map.add("chat_id", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        org.springframework.http.HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(map, headers);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setBufferRequestBody(false);
        RestTemplate restTemplate = new RestTemplate(factory);
        ResponseEntity<String> res = restTemplate.exchange(telegramAPI + token + "/sendDocument", HttpMethod.POST, requestEntity, String.class);
        System.out.println(res.toString());
    }

}
