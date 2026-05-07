package com.sky.mapper;

import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee(username,name,phone,sex,id_number,password,status,create_time,create_user,update_time,update_user) " +
            "values (#{username},#{name},#{phone},#{sex},#{idNumber},#{password},#{status},#{createTime},#{createUser},#{updateTime},#{updateUser})")
    Boolean addEmployee(Employee employee);
}
