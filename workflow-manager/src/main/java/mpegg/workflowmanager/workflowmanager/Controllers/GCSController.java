package mpegg.workflowmanager.workflowmanager.Controllers;

import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gcs")
public class GCSController {
    @GetMapping("/get")
    public String get(@AuthenticationPrincipal Jwt jwt) {
        String url = "http://localhost:8082/get";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal Jwt jwt, @RequestParam("dg_md") MultipartFile dg_md) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_md", dg_md.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String url = "http://localhost:8082/api/create";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return "ok";
    }
}
