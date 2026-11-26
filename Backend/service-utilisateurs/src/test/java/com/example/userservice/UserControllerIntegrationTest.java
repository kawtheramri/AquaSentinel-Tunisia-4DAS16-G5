package com.example.userservice;

import com.example.userservice.dto.Requests;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class UserControllerIntegrationTest {
 @Autowired MockMvc mvc;@Autowired ObjectMapper json;@Autowired UserRepository users;
 @Test void userCrudLifecycle() throws Exception {
  String body=json.writeValueAsString(new Requests.Register("Grace","Hopper","grace@example.com","123","securePass1",com.example.userservice.entity.RoleName.CONSUMER,null,null));
  String response=mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("grace@example.com")).andExpect(jsonPath("$.passwordHash").doesNotExist()).andReturn().getResponse().getContentAsString();
  long id=json.readTree(response).get("id").asLong();
  mvc.perform(get("/api/users/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Grace"));
  String update=json.writeValueAsString(new Requests.UpdateUser("Grace","Hopper","456",com.example.userservice.entity.RoleName.CONSUMER));
  mvc.perform(put("/api/users/"+id).contentType(MediaType.APPLICATION_JSON).content(update)).andExpect(status().isOk()).andExpect(jsonPath("$.phone").value("456"));
  mvc.perform(delete("/api/users/"+id)).andExpect(status().isOk());org.junit.jupiter.api.Assertions.assertFalse(users.existsById(id));
 }
}
