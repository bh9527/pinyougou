package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        // TODO Auto-generated method stub
        return tbBrandMapper.selectByExample(null);
    }

    // 分页查询
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        // 调用pageHelper里面的方法传入当前页和当前页记录数;使用dao层调取方法获取得到page,并把值传如自己建的类中返回.
        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 添加
     */
    @Override
    public void add(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);

    }
    // 查询单实体

    @Override
    public TbBrand findOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    // 修改
    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    // 批量删除
    @Override
    public void delete(Long[] ids) {
        for (long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }

    }

    // 条件查询
    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 获取条件
        TbBrandExample example = new TbBrandExample();
        Criteria criteria = example.createCriteria();
        if (tbBrand != null) {

            if (tbBrand.getName() != null && tbBrand.getName().length() > 0) {
                criteria.andNameLike("%" + tbBrand.getName() + "%");
            }

            if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length() > 0) {
                criteria.andFirstCharLike("%" + tbBrand.getFirstChar() + "%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }

}
