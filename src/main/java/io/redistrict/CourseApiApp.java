package io.redistrict;

import io.redistrict.AppData.AppData;
import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;
import io.redistrict.Utils.NeighborsLoader;
import io.redistrict.Utils.StateLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.LinkedHashMap;

@SpringBootApplication(scanBasePackages = "io.redistrict")
public class CourseApiApp extends SpringBootServletInitializer {
//    @Override
//    protected SpringApplicationBuilder configure (SpringApplicationBuilder application) {
//        return application.sources(CourseApiApp.class);
//    }


    public static void main(String[] args) {
        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));

        SpringApplication.run(CourseApiApp.class, args);
    }
}
