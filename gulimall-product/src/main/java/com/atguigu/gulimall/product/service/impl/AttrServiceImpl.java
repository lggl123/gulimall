package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional  //事务注解 对于操作多张数据表
    @Override
    public void saveAttr(AttrVo attr) {
        //前端传过来了一个vo实体类
        //1，先保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.save(attrEntity);
        //2,保存关联表的数据
        AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
        entity.setAttrGroupId(attr.getAttrGroupId());
        entity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(entity);

    }

//    @Override
//    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
//        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(type)?ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
//
//        if(catelogId != 0){
//            queryWrapper.eq("catelog_id",catelogId);
//        }
//
//        String key = (String) params.get("key");
//        if(!StringUtils.isEmpty(key)){
//            //attr_id  attr_name
//            queryWrapper.and((wrapper)->{
//                wrapper.eq("attr_id",key).or().like("attr_name",key);
//            });
//        }
//
//        IPage<AttrEntity> page = this.page(
//                new Query<AttrEntity>().getPage(params),
//                queryWrapper
//        );
//
//        PageUtils pageUtils = new PageUtils(page);
//        List<AttrEntity> records = page.getRecords();
//        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
//            AttrRespVo attrRespVo = new AttrRespVo();
//            BeanUtils.copyProperties(attrEntity, attrRespVo);
//
//            //1、设置分类和分组的名字
//            if("base".equalsIgnoreCase(type)){
//                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
//                if (attrId != null && attrId.getAttrGroupId()!=null) {
//                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
//                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
//                }
//
//            }
//
//
//            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
//            if (categoryEntity != null) {
//                attrRespVo.setCatelogName(categoryEntity.getName());
//            }
//            return attrRespVo;
//        }).collect(Collectors.toList());
//
//        pageUtils.setList(respVos);
//        return pageUtils;
//    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,respVo);



        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //1、设置分组信息
            AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(attrgroupRelation!=null){
                respVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
                if(attrGroupEntity!=null){
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


        //2、设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity!=null){
            respVo.setCatelogName(categoryEntity.getName());
        }


        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //1、修改分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();

            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());

            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if(count>0){

                relationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));

            }else{
                relationDao.insert(relationEntity);
            }
        }


    }

    /**
     * 根据分组id查找关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

        List<Long> attrIds = entities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        if(attrIds == null || attrIds.size() == 0){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        //relationDao.delete(new QueryWrapper<>().eq("attr_id",1L).eq("attr_group_id",1L));
        //
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);
    }

    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1，通过组id获取当前分类id
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //当前分类id
        Long catelogId = attrGroupEntity.getCatelogId();
        //2，通过分类id查看当前分类下的所有分组
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id",catelogId);
        List<AttrGroupEntity> groupEntityList = attrGroupDao.selectList(wrapper);

        List<Long> collect = groupEntityList.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //3，这些分组关联的属性
        List<AttrAttrgroupRelationEntity> attrList = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        //获取已经关联了的属性id
        List<Long> collect1 = attrList.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //排除这些属性  这样就可以进行查询了
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catelog_id",catelogId);
        if (collect1!=null && collect1.size()>0){
            queryWrapper.notIn("attr_id",collect1);
        }
        //封装IPage
        //模糊查询条件
        String  key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper1)->{
                wrapper1.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        if (catelogId!=0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key =(String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),queryWrapper);
        return new PageUtils(page);
    }

}