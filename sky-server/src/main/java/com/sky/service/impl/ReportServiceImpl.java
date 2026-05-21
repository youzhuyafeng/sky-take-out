package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    @Override
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
            dateQueryList.add(map);
        }
        Map<String, BigDecimal> resultMap = orderMapper.addAmountByDate(dateQueryList);
        for(int i=0;i<dateQueryList.size();i++){
            turnoverList.add(resultMap.get("date_"+i).doubleValue());
        }


        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end){
        List<LocalDate> dateList = new ArrayList<>();
        while(!begin.isAfter(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> nowUserList = new ArrayList<>();
        List<Map<String,Object>> dateQueryListNew = new ArrayList<>();
        List<Map<String,Object>> dateQueryListNow = new ArrayList<>();
        for(LocalDate date : dateList){
            Map<String,Object> map = new HashMap<>();
            LocalDateTime tBegin =LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime tEnd =LocalDateTime.of(date, LocalTime.MAX);
            map.put("end",tEnd);
            dateQueryListNow.add(map);
            Map<String,Object> map2 = new HashMap<>(map);
            map2.put("begin",tBegin);
            dateQueryListNew.add(map2);
            
        }
        Map<String, BigDecimal> resultMapNew = userMapper.countUser(dateQueryListNew);
        Map<String,BigDecimal>  resultMapNow = userMapper.countUser(dateQueryListNow);
        for(int i=0;i<dateQueryListNew.size();i++){
            newUserList.add(resultMapNew.get("date_"+i).intValue());
            nowUserList.add(resultMapNow.get("date_"+i).intValue());
        }
        return UserReportVO.builder()
                .newUserList(StringUtils.join(newUserList,","))
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(nowUserList,","))
                .build();
    }

    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end){
        List<LocalDate> dateList = new ArrayList<>();
        while(!begin.isAfter(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        List<Integer> validOrderList = new ArrayList<>();
        List<Integer> allOrderList = new ArrayList<>();
        List<Map<String,Object>> dateQueryList = new ArrayList<>();
        for(LocalDate date : dateList){
            Map<String,Object> map = new HashMap<>();
            LocalDateTime tBegin =LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime tEnd =LocalDateTime.of(date, LocalTime.MAX);
            map.put("end",tEnd);
            map.put("begin",tBegin);
            dateQueryList.add(map);
        }
        Map<String,BigDecimal> validOrder = orderMapper.countByStatus(dateQueryList,Orders.COMPLETED);
        Map<String,BigDecimal> allOrder = orderMapper.countByStatus(dateQueryList,null);
        for(int i=0;i<dateQueryList.size();i++){
            validOrderList.add(validOrder.get("date_"+i).intValue());
            allOrderList.add(allOrder.get("date_"+i).intValue());
        }
        int allOrderSum = allOrderList.stream().mapToInt(Integer::intValue).sum();
        int validOrderSum = validOrderList.stream().mapToInt(Integer::intValue).sum();
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(allOrderList,","))
                .validOrderCountList(StringUtils.join(validOrderList,","))
                .totalOrderCount(allOrderSum)
                .validOrderCount(validOrderSum)
                .orderCompletionRate((double) validOrderSum /allOrderSum)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end){
        Map<String,Object> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status", Orders.COMPLETED);
        List<Map<String,Object>> dishList = orderMapper.top10Dishs(map);
        List<String> dishNameList = new ArrayList<>();
        List<String> dishCountList = new ArrayList<>();
        dishList.forEach(item->{
            String name = item.get("name").toString();
            String count = item.get("count").toString();
            dishNameList.add(name);
            dishCountList.add(count);
        });

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(dishNameList,","))
                .numberList(StringUtils.join(dishCountList,","))
                .build();

    }

    @Override
    public void exportExcel(HttpServletResponse response) {
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now();
        BusinessDataVO businessDataVO =  workspaceService.getBusinessData(LocalDateTime.of(dateBegin,LocalTime.MIN),LocalDateTime.of(dateEnd,LocalTime.MAX));
        InputStream is= this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        assert is != null;
        try {
            XSSFWorkbook excel = new XSSFWorkbook(is);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=运营数据报表.xlsx");
            XSSFSheet sheet =  excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间"+dateBegin+"至"+dateEnd);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            for(int i=0;i<30;i++) {
                LocalDate date = LocalDate.now().plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN),LocalDateTime.of(date,LocalTime.MAX));
                row = sheet.getRow(7+i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());



            }

            excel.write(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
