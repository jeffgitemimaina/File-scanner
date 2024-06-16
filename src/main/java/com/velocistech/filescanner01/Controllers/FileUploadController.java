package com.velocistech.filescanner01.Controllers;

import com.velocistech.filescanner01.Entity.File;
import com.velocistech.filescanner01.Registration.RegistrationService;
import com.velocistech.filescanner01.Service.FileService;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@CrossOrigin
public class FileUploadController implements ErrorController {
    String itd="";
    String fileName="";
    String clamav,email,status,unpack,stati,md5,sha256,sha1,sha3,filsize,filetype, lastseen,firstseen,fillingtabs="";
    @Autowired
    private FileService fileService;

    JSONObject jsonQuery= new JSONObject();
    RestTemplate restTemplate;

    @Autowired
    public FileUploadController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
//    @RequestMapping(value = "/**/{path:[^\\.]*}")
//    public String forward(){
//        return "forword:/";
//    }
    @RequestMapping("/")
    public String index() {

        return "uploader";
    }
    @RequestMapping("/index")
    public String indexpage() {
        return "uploader";
    }

    @PostMapping("/upload")
    @ModelAttribute
    public ResponseEntity<?> handleFileUpload(Model model,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestParam("clamav_s") int clamav_s,
                                              @RequestParam("unpack_f") int unpack_f,
                                              @RequestParam("share_f") int share_f
                                              ) {
            fileName = file.getOriginalFilename();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String serverUrl = "https://yaraify-api.abuse.ch/api/v1/?clamav_scan=" + clamav_s + "&unpack=" + unpack_f + "&share_file=" + share_f;
            ResponseEntity<String> response = restTemplate
                    .postForEntity(serverUrl, requestEntity, String.class);
            JSONObject jsonupload = new JSONObject(response.getBody());
            JSONObject data = jsonupload.getJSONObject("data");
            itd = String.valueOf(data.get("task_id"));
            md5 = String.valueOf(data.get("md5_hash"));
            sha256 = String.valueOf(data.get("sha256_hash"));
            sha1 = String.valueOf(data.get("sha1_hash"));
            sha3 = String.valueOf(data.get("sha3_384_hash"));
            filsize = String.valueOf(data.get("file_size"));
            filetype = String.valueOf(data.get("mime_type"));
            firstseen = String.valueOf(data.get("first_seen"));
            status = "Queued";
            createFile();
            model.addAttribute("status", status);
            filesresults(model);

            return response;

    }
    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserEmail(Authentication authentication) {
        System.out.println(authentication);
        return String.valueOf(authentication);
    }
    @PostMapping("/createfile")
    public String createFile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        File body = new File();
        body.setTaskid(itd);
        body.setQuery_status(status);
        body.setMd5_hash(md5);
        body.setSha256_hash(sha256);
        body.setSha1_hash(sha1);
        body.setSha3_384_hash(sha3);
        body.setFile_size(Integer.valueOf(filsize));
        body.setFilename(fileName);
        body.setMime_type(filetype);
        body.setFirst_seen(firstseen);
       body.setEmail(currentPrincipalName);
        return fileService.createFile(body);
    }
    @PostMapping("/query" )
    @ResponseBody
    public String queryResponse() {
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "application/json");
        Map<String, Object> dataBody = new HashMap<>();
        dataBody.put("query", "get_results");
        dataBody.put("malpedia-token", "");
        dataBody.put("task_id", itd);
        HttpEntity entity = new HttpEntity<Object>(dataBody, header);
        String url = "https://yaraify-api.abuse.ch/api/v1/";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                String.class);
         jsonQuery = new JSONObject(response.getBody());
        if (response.getStatusCodeValue() == 200) {
            while(jsonQuery.get("query_status").equals("queued") ||
                    jsonQuery.get("data").equals("queued")) {
                status="Queued";
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queryResponse();
                return "Queued";
            }

            JSONObject jsonn = new JSONObject(response.getBody());

            JSONObject data = jsonn.getJSONObject("data");
            JSONArray clamav_results = data.getJSONArray("clamav_results");
            JSONArray static_results = data.getJSONArray("static_results");
            JSONArray unpack_results = data.getJSONArray("unpack_results");
            clamav= String.valueOf(clamav_results);
            stati=String.valueOf(static_results);
            unpack= String.valueOf(unpack_results);
            if (clamav_results.length() >= 1 || static_results.length() >= 1 || unpack_results.length() >= 1) {
                status="This file is considered unsafe, malicious found by the ClamAv scanning engine";
                updateFile();
                fillingtabs=response.getBody();
                    return "Malicious found";
                    } else {
                status="malicious not found";
                updateFile();
                fillingtabs=response.getBody();
                return "malicious not found";
            }
        }
        System.out.println(response.getBody());
        queryResponse();
        return "Error occurred, retrying";
    }
    @GetMapping("/resultupload")
    @ResponseBody
    public String filesresults(Model model) {
        model.addAttribute("status", status);
        return "uploader";
    }

    @GetMapping("/fillingtabs")
    @ResponseBody
    public String resultsToTabs() {
        return fillingtabs;
    }


    @GetMapping("/previousScannedFiles")
    public String previousScanned(ModelMap model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        model.addAttribute("Files", fileService.readFiles(currentPrincipalName));
        System.out.println("after model_-------------------");
        return "alluploads";
    }

    @GetMapping("readfiles")
    public String readFiles(){
        return "alluploads";
    }

    @CrossOrigin
    @GetMapping("/listFiles")
    public java.util.List<File> getAllFiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
       ModelAndView mav = new ModelAndView();
        mav.addObject("files",fileService.readFiles(currentPrincipalName) );
        return fileService.readFiles(currentPrincipalName);
    }
    @PutMapping( "/updatefile")
    public String updateFile(){
        File body = new File();
        body.setTaskid(itd);
        body.setQuery_status(status);
        body.setClamav_results(clamav);
        body.setUnpack_results(unpack);
        body.setStatic_results(stati);

        return fileService.updateFile(body);
    }
    @RequestMapping("/signing")
    public String signing(){
        return "SigningPage";
    }
    @Autowired
    private RegistrationService registrationService;
    @GetMapping(path = "EmailConfirmed")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
    @RequestMapping("/registerpage")
    public String registerpage(){
        return "register";
    }
    @GetMapping("/isLoggedIn")
    public Boolean IsloggedIn(){
        return true;
    }
//    @PostMapping("/signIn")
//    public String signIn(){
//    UserAuth userAuth=new UserAuth();
//        return "";
//    }
    @RequestMapping("/registersuccess")
    public String registersuccess(){
        return "register_success";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // TODO: log error details here
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error";
            }
        }
        return "error";
    }
}

