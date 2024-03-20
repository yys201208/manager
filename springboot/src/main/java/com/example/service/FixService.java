package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Fix;
import com.example.entity.Lab;
import com.example.entity.Type;
import com.example.mapper.FixMapper;
import com.example.mapper.LabMapper;
import com.example.mapper.TypeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报修业务处理
 **/
@Service
public class FixService {

    @Resource
    private FixMapper fixMapper;
    @Resource
    private LabMapper labMapper;
    @Resource
    private TypeMapper typeMapper;
    /**
     * 新增
     */
    public void add(Fix fix) {
        fix.setTime(DateUtil.now());
        Lab lab = labMapper.selectById(fix.getLabId());
        if (ObjectUtil.isNotEmpty(lab) && ObjectUtil.isNotEmpty(lab.getTypeId())){
            Type type = typeMapper.selectById(lab.getTypeId());
            if (ObjectUtil.isNotEmpty(type)){
                fix.setTypeId(type.getId());
            }

        }
        fixMapper.insert(fix);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        fixMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    /**
     * @param ids
     * @return void
     * @description TODO
     */

    /**
     * @param ids
     * @return void
     * @description TODO
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            fixMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    /**
     *
     * @param fix
     */
    public void updateById(Fix fix) {
        fix.setFixTime(DateUtil.now());
        fixMapper.updateById(fix);
    }

    /**
     * 根据ID查询
     */
    public Fix selectById(Integer id) {
        return fixMapper.selectById(id);
    }

    /**
     * 查询所有
     */

    /**
     *
     * @param fix
     * @return
     */
    public List<Fix> selectAll(Fix fix) {
        return fixMapper.selectAll(fix);
    }

    /**
     * 分页查询
     */

    /**
     * @param fix
	 * @param pageNum
	 * @param pageSize
     * @return com.github.pagehelper.PageInfo<com.example.entity.Fix>
     * @description TODO
     */
    public PageInfo<Fix> selectPage(Fix fix, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())){
            fix.setTeacherId(currentUser.getId());
        }
        if (RoleEnum.STUDENT.name().equals(currentUser.getRole())){
            fix.setStudentId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Fix> list = fixMapper.selectAll(fix);
        return PageInfo.of(list);
    }

}