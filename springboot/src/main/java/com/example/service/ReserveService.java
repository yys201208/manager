package com.example.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ReserveEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.common.enums.StatusEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 实验室预约信息表业务处理
 **/
@Service
public class ReserveService implements MailSender {

    @Resource
    private ReserveMapper reserveMapper;
    @Resource
    private LabMapper labMapper;
//    @Resource
//    private StudentMapper studentMapper;
//    @Resource
//    private TeacherMapper teacherMapper;
//    @Resource
//    private JavaMailSender javaMailSender;

    /**
     * 新增
     */
    public void add(Reserve reserve) {
        reserve.setTime(DateUtil.now());
        reserveMapper.insert(reserve);
        Lab lab = labMapper.selectById(reserve.getLabId());
        lab.setStatus(StatusEnum.NO.status);
//        Account currentUser = TokenUtils.getCurrentUser();
//        Integer studentId = currentUser.getId();
//        Student student = studentMapper.selectById(studentId);
//        String studentEmail = student.getEmail();
//        Integer teacherId = lab.getTeacherId();
//        Teacher teacher = teacherMapper.selectById(teacherId);
//        String teacherEmail = teacher.getEmail();
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo("2965709663@qq.com");
////        simpleMailMessage.setTo(teacherEmail);
//        simpleMailMessage.setSubject("系统邮件");
//        simpleMailMessage.setText("111");
//        javaMailSender.send(simpleMailMessage);
        labMapper.updateById(lab);
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {

    }


    /**
     * 取消预约
     */
    public void deleteById(Integer id) {
        Reserve reserve = reserveMapper.selectById(id);
        reserveMapper.deleteById(id);
        Lab lab = labMapper.selectById(reserve.getLabId());
        lab.setStatus(StatusEnum.OK.status);

        labMapper.updateById(lab);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Reserve reserve) {
        if (ReserveEnum.DONE.status.equals(reserve.getDostatus()) || ReserveEnum.NO.status.equals(reserve.getStatus())){
//            释放实验室资源
            Lab lab = labMapper.selectById(reserve.getLabId());
            lab.setStatus(StatusEnum.OK.status);
            labMapper.updateById(lab);
        }
        reserveMapper.updateById(reserve);
    }

    /**
     * 根据ID查询
     */
    public Reserve selectById(Integer id) {
        return reserveMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Reserve> selectAll(Reserve reserve) {
        return reserveMapper.selectAll(reserve);
    }

    /**
     * 分页查询
     */
    public PageInfo<Reserve> selectPage(Reserve reserve, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.TEACHER.name())){
            reserve.setTeacherId(currentUser.getId());
        }
        if (currentUser.getRole().equals(RoleEnum.STUDENT.name())){
            reserve.setStudentId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Reserve> list = reserveMapper.selectAll(reserve);
        return PageInfo.of(list);
    }


    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}