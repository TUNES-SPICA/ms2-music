package org.music.web;

import jakarta.servlet.http.HttpServletResponse;
import org.music.Parser;
import org.music.entity.track.TrackRule;
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
    public void conversion(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "reduceRest", required = false) Boolean reduceRest,
            @RequestParam(value = "changeVolume", required = false) Boolean changeVolume,
            @RequestParam(value = "sustain", required = false) Boolean sustain,
            @RequestParam(value = "changeBPM", required = false) Boolean changeBPM,
            HttpServletResponse response
    ) {
        try (PrintWriter pw = new PrintWriter(response.getOutputStream())) {
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getOriginalFilename() + ".ms2mml", "utf-8"));
            TrackRule rule = TrackRule.init(reduceRest, changeVolume, sustain, changeBPM);
            Parser.convertMIDToMML(file.getInputStream(), rule).forEach(pw::println);
            System.out.println("转换成功:[" + file.getOriginalFilename() + "]");
        } catch (Exception e) {
            System.out.println("转换失败:[" + file.getOriginalFilename() + "]");
            e.printStackTrace();
        }
    }
}
