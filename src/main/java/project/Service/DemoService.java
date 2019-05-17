package project.Service;

import webby.bean.annotation.Bean;

/**
 * @author Makise
 * @descirption
 * @date 2019/5/17
 */
@Bean
public class DemoService {
    public String hello() {
        return "this is a IOC demo";
    }
}
