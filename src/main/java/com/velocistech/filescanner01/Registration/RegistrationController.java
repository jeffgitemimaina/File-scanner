package com.velocistech.filescanner01.Registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController  {
private RegistrationService registrationService;
    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody @ModelAttribute RegistrationRequest request){
        return registrationService.register(request);
    }
}
