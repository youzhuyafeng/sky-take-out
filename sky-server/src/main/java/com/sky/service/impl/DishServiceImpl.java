package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Transactional
    @Override
    public void saveDishWithFlavor(DishDTO dishDTO){
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.saveDish(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors!=null && !flavors.isEmpty()){
            flavors.forEach(f->{
                f.setDishId(dish.getId());
            });
            dishMapper.saveDishFlavorBatch(flavors);
        }
    }

    @Override
    public PageResult getDishList(DishPageQueryDTO dishPageQueryDTO){
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        List<DishVO> dishList = dishMapper.getDishListWithCategory(dishPageQueryDTO);
        Page<DishVO> page = (Page<DishVO>)dishList;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    @Override
    public void delete(List<Long> ids){
        dishMapper.selectDishByIds(ids).forEach(d->{
            if(d.getStatus() == 1){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        List<Setmeal> meal = setmealMapper.selectByIds(ids);
        if(meal!=null && !meal.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        dishMapper.deleteByIds(ids);
        dishMapper.deleteDishFlavorByIds(ids);
    }

    @Override
    public void setDishStatus(Integer status, Long id){
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.update(dish);
    }

    @Override
    public DishVO getDishById(Long id){
        DishVO dishVO = dishMapper.getDishByIdWithFlavor(id);
        return dishVO;
    }

    @Override
    public List<Dish> getDishByCategoryId(Long categoryId){
        return dishMapper.getDishyCategoryId(categoryId);
    }
}
