package mpegg.workflowmanager.workflowmanager.Controllers;

import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class WorkflowController {

    private final String urlGCS = "http://localhost:8082";

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

    @GetMapping("/ownFiles")
    public String getOwnedFiles(@AuthenticationPrincipal Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        response = restTemplate.exchange(urlGCS+"/api/v1/ownFiles", HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    @GetMapping("/getMetadata")
    public String getMetadata(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer dgId, @RequestParam Integer dtId) {
        return "ok";
    }

    @PostMapping("/search")
    public String search(@AuthenticationPrincipal Jwt jwt, @RequestBody JSONObject searchParams) {
        return "ok";
    }

    @PostMapping("/upload")
    public String create(@AuthenticationPrincipal Jwt jwt, @RequestParam("dg_md") MultipartFile dg_md, @RequestParam("dt_mt") MultipartFile[] dt_md) {
        HttpHeaders headers = new HttpHeaders();
        JSONObject a = (JSONObject) jwt.getClaims().get("realm_access");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_md", dg_md.getResource());
        for (MultipartFile file : dt_md) {
            body.add("dt_mt",file.getResource());
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(urlGCS+"/api/v1/uploadMD", HttpMethod.POST, requestEntity, String.class);
        return "ok";
    }
}