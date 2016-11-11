/**
 * Copyright (C) 2016 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK Samples project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.activiti.extension.bean;

import com.activiti.api.datamodel.AlfrescoCustomDataModelService;
import com.activiti.model.editor.datamodel.DataModelDefinitionRepresentation;
import com.activiti.model.editor.datamodel.DataModelEntityRepresentation;
import com.activiti.runtime.activiti.bean.datamodel.AttributeMappingWrapper;
import com.activiti.variable.VariableEntityWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author martin.bergljung@alfresco.com
 */
@Service
public class CustomDataModelServiceImpl implements AlfrescoCustomDataModelService {
    private static Logger logger = LoggerFactory.getLogger(CustomDataModelServiceImpl.class);

    /**
     * Database table names
     */
    private static final String SALARIES_TABLE_NAME = "salaries";

    /**
     * Mapping entity into JSON
     */
    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Use Spring JDBC Template for database access
     */
    private SimpleJdbcTemplate jdbcTemplate;

    /**
     * Salary Data Transfer Object (DTO)
     */
    private class Salary {
        private long empNo;
        private Date fromDate;
        private Date toDate;
        private long salary;

        public long getEmpNo() { return empNo; }
        public void setEmpNo(long empNo) { this.empNo = empNo; }
        public Date getFromDate() { return fromDate; }
        public void setFromDate(Date fromDate) { this.fromDate = fromDate; }
        public Date getToDate() { return toDate; }
        public void setToDate(Date toDate) { this.toDate = toDate; }
        public long getSalary() { return salary; }
        public void setSalary(long salary) { this.salary = salary; }
    }

    /**
     * Salary Spring JDBC Row Mapper
     */
    class SalaryRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            Salary salary = new Salary();
            salary.setEmpNo(rs.getLong("emp_no"));
            salary.setFromDate(rs.getDate("from_date"));
            salary.setToDate(rs.getDate("to_date"));
            salary.setSalary(rs.getLong("salary"));

            return salary;
        }
    }

    /**
     * CREATE USER employees@localhost IDENTIFIED BY '1234';
     * GRANT ALL PRIVILEGES ON employees.* TO employees@localhost IDENTIFIED BY '1234';
     * FLUSH PRIVILEGES;
     */
    public CustomDataModelServiceImpl() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/employees");
        ds.setUsername("employees");
        ds.setPassword("1234");

        jdbcTemplate = new SimpleJdbcTemplate(ds);
    }

    /**
     * This method is called when Activiti wants to fetch a row from the database table
     * that has been mapped as a "Custom" entity.
     *
     * @param entityDefinition the definition of the "custom" entity that was mapped in the Custom Data Model (e.g. Salary)
     * @param fieldName the entity field that represents the PK (e.g. Employee No)
     * @param fieldValue the entity field value (e.g. 10001)
     * @return an object representing the fetched data
     */
    @Override
    public ObjectNode getMappedValue(DataModelEntityRepresentation entityDefinition,
                                     String fieldName, Object fieldValue) {

        logger.info("getMappedValue() EntityDefinition [Name=" + entityDefinition.getName() +
                "][TableName=" + entityDefinition.getTableName() + "][Id=" + entityDefinition.getId() +
                "][Attributes=" + entityDefinition.getAttributes().size() +
                "] [fieldName=" + fieldName +
                "] [variableValue=" + fieldValue + "]");

        // Check if are to get something from the salaries table
        if (StringUtils.equals(entityDefinition.getTableName(), SALARIES_TABLE_NAME)) {
            // Fetch the Salary row we are looking for
            Long employeeNo = (Long) fieldValue;
            Date currentDate = new Date();
            String sql = "SELECT * FROM " + SALARIES_TABLE_NAME +
                    " WHERE emp_no = ? and from_date <= ? and to_date > ?";
            Salary salary = (Salary) jdbcTemplate.queryForObject(
                    sql, new SalaryRowMapper(), new Object[]{employeeNo, currentDate, currentDate});


            // The following fields have to match the attributes set up in the Custom Data Model
            // and what is used in the form that will display them
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // The expected entity date format is ISO-8601
            ObjectNode fetchedSalaryRowAsJSON = objectMapper.createObjectNode();
            fetchedSalaryRowAsJSON.put("Employee No", salary.getEmpNo());
            fetchedSalaryRowAsJSON.put("From Date", sdf.format(salary.getFromDate()) + "Z"); // see https://github.com/FasterXML/jackson-databind/issues/338
            fetchedSalaryRowAsJSON.put("To Date", sdf.format(salary.getToDate()) + "Z");
            fetchedSalaryRowAsJSON.put("Salary", salary.getSalary());

            logger.info("getMappedValue() Response: " + fetchedSalaryRowAsJSON.toString());
            return fetchedSalaryRowAsJSON;
        }

        return null;
    }

    @Override
    public VariableEntityWrapper getVariableEntity(String keyValue, String variableName,
                                                   String processDefinitionId,
                                                   DataModelEntityRepresentation entityValue) {

        logger.info("getVariableEntity() Entity [Name=" + entityValue.getName() +
                "][TableName=" + entityValue.getTableName() + "][Id=" + entityValue.getId() +
                "][Attributes=" + entityValue.getAttributes().size() +
                "] [keyValue=" + keyValue +
                "] [variableName=" + variableName + "]");


        return null;
    }

    /**
     * This method is called when Activiti wants to store a "Custom" entity in the
     * database.
     *
     * @param attributeDefinitionsAndValues attributes that will become the column values
     * @param entityDefinition the definition of the "custom" entity that was mapped in the Custom Data Model (e.g. Salary)
     * @param dataModel the custom data model that contains the "custom" entity definition
     * @return
     */
    @Override
    public String storeEntity(List<AttributeMappingWrapper> attributeDefinitionsAndValues,
                              DataModelEntityRepresentation entityDefinition,
                              DataModelDefinitionRepresentation dataModel) {

        logger.info("storeEntity() EntityDefinition [Name=" + entityDefinition.getName() +
                "][TableName=" + entityDefinition.getTableName() + "][Id=" + entityDefinition.getId() +
                "][Attributes=" + entityDefinition.getAttributes().size() +
                "] [dataModel=" + dataModel.getName() +
                "] [attributeDefinitionsAndValues=" + attributeDefinitionsAndValues.size() + "]");

        // Check if we are to store something in the salaries table
        if (StringUtils.equals(entityDefinition.getTableName(), SALARIES_TABLE_NAME)) {
            // Set up a map of all the column names and values
            Map<String, Object> parameters = new HashMap<String, Object>();
            for (AttributeMappingWrapper attributeMappingWrapper : attributeDefinitionsAndValues) {
                // Get the column name = mapped name
                // And the column value = attr value
                parameters.put(attributeMappingWrapper.getAttribute().getMappedName(),
                        attributeMappingWrapper.getValue());
            }

            // Update current salary entry to previous
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sql = "UPDATE " + SALARIES_TABLE_NAME +
                    " SET to_date = '" +  sdf.format(parameters.get("from_date")) + "'" +
                    " WHERE emp_no = " + parameters.get("emp_no") +
                    " AND to_date = '9999-01-01'";
            jdbcTemplate.update(sql);

            // Insert new salary entry
            sql = "INSERT INTO " + SALARIES_TABLE_NAME +
                    " (emp_no, from_date, to_date, salary) VALUES (:emp_no, :from_date, :to_date, :salary)";
            jdbcTemplate.update(sql, parameters);
        }

        return null;
    }
}
