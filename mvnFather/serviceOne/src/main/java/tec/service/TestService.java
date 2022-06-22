package tec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.dao.ESDAO;
import tec.dao.StudentMapper;
import tec.pojo.ESTest;
import tec.pojo.Student;

import java.util.List;

@Service("testService")
public class TestService {
    @Autowired
    StudentMapper studentMapper;
    @Autowired
    ESDAO esdao;
    public void test() {
        studentMapper.getStudentByid("1").toPrint();
        Student student = new Student();
        student.setId("2");
        student.setName("小李");
        int i = studentMapper.insertStudent(student);
        student.setId("3");
        student.setName("小李3");
        studentMapper.insertStudent(student);
        student.setId("4");
        student.setName("小李4");
        studentMapper.insertStudent(student);
        System.out.println("iiiiii="+i);
        student.setName("小李233");
        int j =studentMapper.updateStudent(student);
        System.out.println("jjjjj="+j);
        List<Student> list= studentMapper.getStudents();
        for (Student student1 : list)
        {
            student1.toPrint();
        }
        student.setId("3");
        student.setName("小李3");
        int q =studentMapper.deleteStudent(student);
        System.out.println("qqqqq="+q);
        List<Student> list1 = studentMapper.getStudents();
        for (Student student1 : list1)
        {
            student1.toPrint();
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "directQueue-One"),
            exchange = @Exchange(name = "DirectExchange-One", type = ExchangeTypes.DIRECT),
            key = {"One","Three"}
    ))
    public void listenDirectQueue(Object msg)
    {
        System.out.println(msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "directQueue-Two"),
            exchange = @Exchange(name = "DirectExchange-One", type = ExchangeTypes.DIRECT),
            key = {"Two","Three"}
    ))
    @RabbitHandler
    public void listenDirectQueue2(JSONObject msg)
    {
        System.out.println(msg.toJSONString());
    }

    public void testES()
    {
        ESTest esOne = new ESTest();
        esOne.setName("小明");
        esOne.setAge(18);
        esOne.setAddress("666街道");
        esOne.setId(1L);
        esdao.save(esOne);
    }
}
