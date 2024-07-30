package org.music.web;

import jakarta.servlet.http.HttpServletResponse;
import org.music.Parser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.net.URLEncoder;

@RestController
@RequestMapping("/")
public class ParserController {

    @RequestMapping(value = "/conversion", method = {RequestMethod.GET, RequestMethod.POST})
    public void conversion(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try (PrintWriter pw = new PrintWriter(response.getOutputStream())) {
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getOriginalFilename() + ".ms2mml", "utf-8"));
            Parser.convertMIDToMML(file.getInputStream()).forEach(pw::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
