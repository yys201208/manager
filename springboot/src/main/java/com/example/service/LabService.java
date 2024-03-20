package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.common.enums.StatusEnum;
import com.example.entity.Account;
import com.example.entity.Lab;
import com.example.entity.Type;
import com.example.exception.CustomException;
import com.example.mapper.LabMapper;
import com.example.mapper.TypeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 实验室信息表业务处理
 **/
@Service
public class LabService {

    @Resource
    private LabMapper labMapper;
    @Resource
    private  TypeMapper typeMapper;

    /**
     * 新增
     */
    public void add(Lab lab) {
        Type type = typeMapper.selectById(lab.getTypeId());
        if (ObjectUtil.isAllEmpty(type)){
            throw new CustomException(ResultCodeEnum.TYPE_NO_ERROR);
        }
        if (ObjectUtil.isEmpty(type.getTeacherId())){
            throw new CustomException(ResultCodeEnum.TYPE_TEACHER_NO_ERROR);
        }
        lab.setTeacherId(type.getTeacherId());
        lab.setStatus(StatusEnum.OK.status);

        labMapper.insert(lab);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        labMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            labMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Lab lab) {
        labMapper.updateById(lab);
    }

    /**
     * 根据ID查询
     */
    /**
     *
     * @param id
     * @return com.example.entity.Lab
     * @descption todo
     **/

    public Lab selectById(Integer id) {
        return labMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    /**
     *
     * @param lab
     * @return java.util.List<com.example.entity.Lab>
     * @descption todo
     **/

    public List<Lab> selectAll(Lab lab) {
        return labMapper.selectAll(lab);
    }

    /**
     * 分页查询
     */

   /**
    *
    * @param lab
    * @param pageNum
    * @param pageSize
    * @return com.github.pagehelper.PageInfo<com.example.entity.Lab>
    * @descption todo
    **/


   public PageInfo<Lab> selectPage(Lab lab, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.TEACHER.name())){
            lab.setTeacherId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Lab> list = labMapper.selectAll(lab);
        return PageInfo.of(list);
    }

}