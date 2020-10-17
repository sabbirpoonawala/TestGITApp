package com.yash.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yash.controller.EmployeeController;
import com.yash.main.SpringBootMvcApplication;
import com.yash.model.EmployeeRequest;
import com.yash.model.EmployeeResponse;
import com.yash.model.EmployeesModel;
import com.yash.service.EmployeeService;

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = SpringBootMvcApplication.class)
class TestEmployeeController {

	 private MockMvc mockMvc;

	 @Mock
	 private EmployeeService employeeService;
	 
	//Using Spring Test-- without need of tomcat server
	 private MockRestServiceServer mockServer;

	    private ObjectMapper mapper = new ObjectMapper();

	 
	 @Mock
	 private RestTemplate restTemplate;
	 
	
	 @InjectMocks
	 private EmployeeController employeeController;
	 
	 RestTemplate template;
	 
	   @BeforeEach
	    public void init(){
	        MockitoAnnotations.initMocks(this);
	        mockMvc = MockMvcBuilders
	                .standaloneSetup(employeeController)
	                .build();
	        
	        template=new RestTemplate();
	        mockServer = MockRestServiceServer.createServer(template);

	    }
	   @Test
		public void test_handleGetAllEmployees_positive() {
			try {
			List<EmployeesModel> employeeResponseList=new ArrayList<EmployeesModel>();
			EmployeesModel employeeResponse=new EmployeesModel();
			employeeResponse.setEmpId(1001);
			employeeResponse.setEmpName("sabbir");
			employeeResponse.setEmpSalary(34000);
			employeeResponse.setEmpDesignation("Trainer");
			
			employeeResponseList.add(employeeResponse);
			when(employeeService.getAllEmployees()).thenReturn(employeeResponseList);
			ResponseEntity<List<EmployeeResponse>> response=employeeController.handleGetAllEmployees();
			List<EmployeeResponse> employeeList=response.getBody();
			assertEquals(true,employeeList.size()>0);
			}catch(NullPointerException e) {
				assertTrue(false);
			}
		}
	   @Test
		public void getEmployeePositive(){
			when(employeeService.getEmployeeById(Mockito.anyInt())).thenAnswer(new Answer<EmployeesModel>(){
				public EmployeesModel answer(InvocationOnMock invocation) throws Throwable {
					// TODO Auto-generated method stub
					EmployeesModel employeeModel=new EmployeesModel();
					employeeModel.setEmpId(1001);
					employeeModel.setEmpName("sabbir");
					employeeModel.setEmpSalary(34000);
					employeeModel.setEmpDesignation("Trainer");
				return employeeModel;
				}
			});
			ResponseEntity<EmployeeResponse> responseEntity=employeeController.getEmployee(1001);
			EmployeeResponse employeeResponse=responseEntity.getBody();
			assertEquals(1001,employeeResponse.getEmpId());
			
		}
	   
		@Test
		public void persistEmployee_Positive() {
			EmployeesModel employeesModel=new EmployeesModel();
			employeesModel.setEmpId(1009);
			employeesModel.setEmpName("sabbir");
			employeesModel.setEmpSalary(45000);
			employeesModel.setEmpDesignation("Trainer");
			try {
				when(employeeService.persistEmployee(employeesModel)).thenReturn(true);
				System.out.println("in test:"+employeeService.persistEmployee(employeesModel));
			EmployeeRequest employeeRequest=new EmployeeRequest();
			employeeRequest.setEmpId(1009);
			employeeRequest.setEmpName("sabbir");
			employeeRequest.setEmpSalary(45000);
			employeeRequest.setEmpDesignation("Trainer");
		    
            ResponseEntity<Void> responseEntity=employeeController.persistEmployee(employeeRequest);
            //HttpStatus.CREATED=responseEntity.getStatusCode();
            assertEquals(200,responseEntity.getStatusCode());
			}catch(Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
			
		}
		@Test
		void testgetEmployeeURI_Positive() {
			EmployeesModel mockEmployeesModel=new EmployeesModel();
			mockEmployeesModel.setEmpId(1001);
			when(employeeService.getEmployeeById(anyInt())).thenReturn(mockEmployeesModel);

			  try {
				MvcResult result=mockMvc.perform(get("http://localhost:8082/api/employees/{empId}",1001))
				  .andExpect(status().isFound()).andReturn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		@Test
		void testgetEmployeeURI_Negative() {
			EmployeesModel mockEmployeesModel=new EmployeesModel();
			mockEmployeesModel.setEmpId(0);
			when(employeeService.getEmployeeById(anyInt())).thenReturn(mockEmployeesModel);
			  try {
				MvcResult result=mockMvc.perform(get("http://localhost:8082/api/employees/{empId}", 1007))
				  .andExpect(status().isNotFound()).andReturn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		@Test
		void testUpdateEmployeeSalaryURI_Positive() {
			  try {
				MvcResult result=mockMvc.perform(patch("http://localhost:8082/api/employees/{empId}/{empSalary}", 1001,45000))
				  .andExpect(status().isAccepted()).andReturn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		@Test
		void testRestTemplate() {
			EmployeeResponse employeeResponse=new EmployeeResponse();
			employeeResponse.setEmpId(1001);
			employeeResponse.setEmpName("sabbir");
			employeeResponse.setEmpSalary(56000);
			employeeResponse.setEmpDesignation("Trainer");
		    Mockito.when(restTemplate.getForEntity("http://localhost:8082/api/employees/1001", EmployeeResponse.class))
	        .thenReturn(new ResponseEntity(employeeResponse, HttpStatus.OK));
		    ResponseEntity<EmployeeResponse> responseEntity=
		    		restTemplate.getForEntity("http://localhost:8082/api/employees/1001", EmployeeResponse.class);
		    EmployeeResponse response=responseEntity.getBody();
		    assertEquals(1001,response.getEmpId());
		}
		
		@Test
		void testMockServer() {
			try {
			EmployeeResponse employeeResponse=new EmployeeResponse();
			employeeResponse.setEmpId(1001);
			employeeResponse.setEmpName("sabbir");
			employeeResponse.setEmpSalary(56000);
			employeeResponse.setEmpDesignation("Trainer");
			 mockServer.expect(ExpectedCount.once(), 
			          requestTo(new URI("http://localhost:8082/api/employees/1001")))
			          .andExpect(method(HttpMethod.GET))
			          .andRespond(withStatus(HttpStatus.OK)
			          .contentType(MediaType.APPLICATION_JSON)
			          .body(mapper.writeValueAsString(employeeResponse))
			        ); 
			 
			  ResponseEntity<EmployeeResponse> responseEntity=
			    		template.getForEntity("http://localhost:8082/api/employees/1001", EmployeeResponse.class);
			  
			  assertEquals(1001,responseEntity.getBody().getEmpId());
			 
			 mockServer.verify();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

}
