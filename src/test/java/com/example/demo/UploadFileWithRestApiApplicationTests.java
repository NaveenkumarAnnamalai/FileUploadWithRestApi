package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

class UploadFileWithRestApiApplicationTests extends AbstractTest{
	
	@Override
    @Before
    public void setUp() {
       super.setUp();
    }
	
	   @Test
	   public void readValuesfromCSV() throws Exception {
	      String uri = "/getPropertyById/2";
	      MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
	    		  .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	      
	      int status = mvcResult.getResponse().getStatus();
	      assertEquals(200, status);
	      String content = mvcResult.getResponse().getContentAsString();
	      assertEquals(content, "Property retrived successfully");
	   }
	
		@Test
		public void deleteValuesfromCSV() throws Exception {
		    String uri = "/deletePropertyById/2";
		    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
		    int status = mvcResult.getResponse().getStatus();
		    assertEquals(200, status);
		    String content = mvcResult.getResponse().getContentAsString();
		    assertEquals(content, "Property is deleted successsfully");
		}

}
