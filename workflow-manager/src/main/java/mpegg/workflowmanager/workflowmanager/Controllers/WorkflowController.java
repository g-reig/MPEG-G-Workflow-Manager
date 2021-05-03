package mpegg.workflowmanager.workflowmanager.Controllers;

import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class WorkflowController {

    private final String urlGCS = "http://localhost:8082";

    @PostMapping("/addFile")
    public ResponseEntity<String> addFile(@AuthenticationPrincipal Jwt jwt, @RequestParam("file_name") String file_name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_name", file_name);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(urlGCS + "/api/v1/addFile", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addDatasetGroup")
    public ResponseEntity<String> addDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestPart("dg_md") MultipartFile dg_md, @RequestPart("dg_pr") MultipartFile dg_pr, @RequestPart(value = "dt_md",required = false) MultipartFile[] dt_md, @RequestPart("file_id") String file_id) {
        HttpHeaders headers = new HttpHeaders();
        JSONObject a = (JSONObject) jwt.getClaims().get("realm_access");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_id",file_id);
        body.add("dg_md", dg_md.getResource());
        body.add("dg_pr", dg_pr.getResource());
        if (dt_md != null) {
            for (MultipartFile file : dt_md) {
                body.add("dt_md", file.getResource());
            }
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/addDatasetGroup", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String >("ok",HttpStatus.OK);
    }

    @PostMapping("/addDataset")
    public ResponseEntity<String> addDataset(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dt_md",required = false) MultipartFile dt_md, @RequestPart(value = "dt_pr",required = false) MultipartFile dt_pr, @RequestPart("dg_id") String dg_id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id",dg_id);
        if (dt_md != null) body.add("dt_md", dt_md.getResource());
        if (dt_pr != null) body.add("dt_pr", dt_pr.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlGCS + "/api/v1/addDataset", HttpMethod.POST, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String >("ok",HttpStatus.OK);
    }

    @PostMapping("/editDatasetGroup")
    public ResponseEntity<String> editDatasetGroup(@AuthenticationPrincipal Jwt jwt, @RequestPart(value = "dg_md",required = false) MultipartFile dg_md, @RequestPart(value = "dg_pr",required = false) MultipartFile dg_pr, @RequestPart(value = "dg_id") String dg_id) {
        if (dg_md == null && dg_pr == null) return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization" , "Bearer "+jwt.getTokenValue());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("dg_id",dg_id);
        if (dg_md != null) body.add("dg_md", dg_md.getResource());
        if (dg_pr != null) body.add("dg_pr", dg_pr.getResource());
        return new ResponseEntity<String>("ok",HttpStatus.OK);
    }
}