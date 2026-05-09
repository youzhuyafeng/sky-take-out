package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @AutoFill(operationType = OperationType.INSERT)
    @Insert("insert into employee(username,name,phone,sex,id_number,password,status,create_time,create_user,update_time,update_user) " +
            "values (#{username},#{name},#{phone},#{sex},#{idNumber},#{password},#{status},#{createTime},#{createUser},#{updateTime},#{updateUser})")
    Boolean addEmployee(Employee employee);

    List<Employee> getEmployeeListByName(String name);

    @AutoFill(operationType = OperationType.UPDATE)
    void update(Employee employee);

    @Select("select * from employee where id=#{id}")
    Employee getEmployeeById(Long id);
}
