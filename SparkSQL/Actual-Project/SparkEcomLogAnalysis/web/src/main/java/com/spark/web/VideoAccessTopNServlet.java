package com.spark.web;

import com.spark.DAO.VideoAccessTopNDAO;
import com.spark.domain.VideoAccessTopN;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 最受欢迎的TopN课程
 *
 * web层  ===》service ===》 DAO
 *
 */
public class VideoAccessTopNServlet extends HttpServlet {


    private VideoAccessTopNDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new VideoAccessTopNDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String day = req.getParameter("day");
        List<VideoAccessTopN> results =  dao.query(day);
        //将结果写成json
        JSONArray json = JSONArray.fromObject(results);
        resp.setContentType("text/html;charset=utf-8");

        PrintWriter writer = resp.getWriter();
        writer.println(json);
        writer.flush();
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
