package ch.uzh.ifi.hase.soprafs23.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class WebSocketController {

    private AtomicInteger idProducer = new AtomicInteger();

    @RequestMapping("/getName")
    public String index(Model model) {
        return "user" +idProducer.getAndIncrement();
    }
}

