package com.shzshz.filesystem.mapper;

import com.shzshz.filesystem.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {

    void delete(List<Integer> ids);

    @Insert("INSERT INTO user(email,password,role) VALUES(#{email},#{password},#{role})")
    void add(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getById(Integer id);

    @Select("SELECT * FROM user WHERE email = #{email} AND password = #{password}")
    User getByUsernameAndPassword(User user);

    @Update("UPDATE user SET userkey = #{userkey} WHERE id = #{id}")
    void setKey(User user);
}
