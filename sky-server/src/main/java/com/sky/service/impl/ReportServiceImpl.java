package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end){
        List<LocalDate> dateList = new ArrayList<>();
        while(!begin.isAfter(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        List<Double> turnoverList = new ArrayList<>();
        List<Map<String,Object>> dateQueryList = new ArrayList<>();
        for(LocalDate date : dateList){
            Map<String,Object> map = new HashMap<>();
            LocalDateTime tBegin =LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime tEnd =LocalDateTime.of(date, LocalTime.MAX);
            map.put("begin",tBegin);
            map.put("end",tEnd);
            map.put("status", Orders.COMPLETED);
            Double result = orderMapper.addAmountByDate(map);
            result = result==null?0:result;
            turnoverList.add(result);
        }




        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }
}
