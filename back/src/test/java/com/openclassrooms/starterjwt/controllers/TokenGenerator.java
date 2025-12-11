package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

public class TokenGenerator {

    public static String getAuthToken(MockMvc mockMvc, String email, String password) throws Exception {
        String body = String.format(
            "{"
            + "\"email\":\"%s\","
            + "\"password\":\"%s\""
            + "}",
            email, password
        );
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();
                
        return "Bearer " + JsonPath.read(
            result.getResponse().getContentAsString(), 
            "$.token"
        );
    }
}
