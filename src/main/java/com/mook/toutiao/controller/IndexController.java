package com.mook.toutiao.controller;

import com.mook.toutiao.model.User;
import com.mook.toutiao.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by mook on 2017/6/12.
 */
//@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String index(HttpSession session) {
        logger.info("Visit Index");
        session.setAttribute("msg", "session_msg");
        return "Hello NowCoder," + session.getAttribute("msg")
                + "<br> Say:" + toutiaoService.say();
    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId, @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "mook") String key) {

        return String.format("GID{%s}, UID{%d}, TYPE{%d}, KEY{%s}", groupId, userId, type, key);
    }

    @RequestMapping(value = {"/vm"})
    public String news(Model model) {
        model.addAttribute("value1", "1");

        List<String> colors = Arrays.asList(new String[] {"RED", "GREEN", "BLUE"});
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }
        model.addAttribute("colors", colors);
        model.addAttribute("map", map);

        model.addAttribute("user", new User("Tom"));
        return "news";
    }

    @RequestMapping(value = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            String name = headNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for (Cookie cookie : request.getCookies()) {
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        sb.append("getMethod:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");
        sb.append("getRequestURI:" + request.getRequestURI() + "<br>");

        return sb.toString();
    }

    @RequestMapping(value = {"/response"})
    @ResponseBody
//    http://127.0.0.1:8080/response?key=nowcoderid&value=23
    public String response(@CookieValue(value = "nowcoderid", defaultValue = "a") String nowcoderId,
                           @RequestParam(value = "key", defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response) {
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "NowCoderId From Cookie:" + nowcoderId + "<br>" + response.getHeader(key);
    }

    @RequestMapping("/redirect/{code}")
    public String redirect(@PathVariable("code") int code,
                           HttpSession session) {
        /*
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;*/
        session.setAttribute("msg", "Jump from redirect.");
        return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key) {
        if ("admin".equals(key)) {
            return "hello admin";
        }
        throw new IllegalArgumentException("Key 错误");
    }

    // Spring MVC外的Exception或Spring MVC没有处理的Exception
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }
}
