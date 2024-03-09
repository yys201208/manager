package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Teacher;
import com.example.exception.CustomException;
import com.example.mapper.TeacherMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员业务处理
 **/
@Service
public class TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    /**
     * 新增
     */
    public void add(Teacher teacher) {
        Teacher dbTeacher = teacherMapper.selectByUsername(teacher.getUsername());
//        去重
        if (ObjectUtil.isNotNull(dbTeacher)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
//        赋默认值
        if (ObjectUtil.isEmpty(teacher.getPassword())) {
            teacher.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(teacher.getName())) {
            teacher.setName(teacher.getUsername());
        }
        teacher.setRole(RoleEnum.TEACHER.name());
        teacher.setAvatar("https://tse3-mm.cn.bing.net/th/id/OIP-C.-EhRx0Rn4RzBmReW1cf88wHaHa?w=202&h=202&c=7&r=0&o=5&dpr=1.5&pid=1.7");
        teacherMapper.insert(teacher);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        teacherMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            teacherMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Teacher teacher) {
        teacherMapper.updateById(teacher);
    }

    /**
     * 根据ID查询
     */
    public Teacher selectById(Integer id) {
        return teacherMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Teacher> selectAll(Teacher teacher) {
        return teacherMapper.selectAll(teacher);
    }

    /**
     * 分页查询
     */
    public PageInfo<Teacher> selectPage(Teacher teacher, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Teacher> list = teacherMapper.selectAll(teacher);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbTeacher = teacherMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbTeacher.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbTeacher.getId() + "-" + RoleEnum.TEACHER.name();
        String token = TokenUtils.createToken(tokenData, dbTeacher.getPassword());
        dbTeacher.setToken(token);
        return dbTeacher;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(account, teacher);
        add(teacher);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Teacher dbTeacher = teacherMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbTeacher)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbTeacher.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbTeacher.setPassword(account.getNewPassword());
        teacherMapper.updateById(dbTeacher);
    }

}