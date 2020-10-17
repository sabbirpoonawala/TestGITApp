package com.yash.service;

import java.util.List;

import com.yash.model.EmployeesModel;

public interface EmployeeService {

	List<EmployeesModel> getAllEmployees();

	EmployeesModel getEmployeeById(int empId);

	boolean persistEmployee(EmployeesModel employeeModel);

	boolean UpdateEmployeeSalary(int empId, double newSalary);

	boolean deleteEmployee(int empId);

}