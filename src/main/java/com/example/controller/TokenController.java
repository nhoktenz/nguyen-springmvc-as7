package com.example.controller;

import com.example.service.StudentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TokenController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/token")
    public String getToken(@RequestParam("userName") final String userName, @RequestParam("password") final String password){
       
       System.out.println("in TokenController - getToken    ....   " + userName + "      " + password) ;
       
    //     String userName1 = "lfun"; 
    //     String password1 = "lfpw";
    //     System.out.println("I changed it to ..............   " + userName1 + "      " + password1) ;
       
       String token= studentService.login(userName,password);

//       String token= studentService.login(userName1,password1);

       System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!      " + "=         " + token);

       if(StringUtils.isEmpty(token)){
           return "no token found";
       }
       return token;
    }
}
