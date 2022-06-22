package tec.dao;

import org.apache.ibatis.annotations.*;
import tec.pojo.Student;

import java.util.List;
@Mapper
public interface StudentMapper {

    @Select("select * from student where id = #{id}")
    Student getStudentByid(String id);
    @Insert("insert into student(id, name) values(#{id}, #{name})")
    int insertStudent(Student student);
    @Delete("delete from student where id = #{id} and name = #{name}")
    int deleteStudent(Student student);
    @Update("update student set name = #{name} where id = #{id}")
    int updateStudent(Student student);
    @Select("select * from student")
    List<Student> getStudents();
}
