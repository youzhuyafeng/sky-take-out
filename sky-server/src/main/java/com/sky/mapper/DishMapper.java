package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(operationType = OperationType.INSERT)
    void saveDish(Dish dish);

    void saveDishFlavorBatch(List<DishFlavor> flavors);

    List<DishVO> getDishListWithCategory(DishPageQueryDTO dishPageQueryDTO);

    void deleteByIds(List<Long> ids);

    void deleteDishFlavorByIds(List<Long> ids);

    List<Dish> selectDishByIds(List<Long> ids);

    void update(Dish dish);

    DishVO getDishByIdWithFlavor(Long id);

    @Select("select * from dish where category_id=#{categortyId}")
    List<Dish> getDishyCategoryId(Long categoryId);

    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}
