package io.redistrict.springbootstarter;

import io.redistrict.AppData.AppData;
import io.redistrict.Territory.State;
import io.redistrict.Utils.NeighborsLoader;
import io.redistrict.Utils.PrecinctLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.LinkedHashMap;

@SpringBootApplication(scanBasePackages = "io.redistrict")
public class CourseApiApp extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure (SpringApplicationBuilder application) {
        return application.sources(CourseApiApp.class);
    }


    public static void main(String[] args) {
        NeighborsLoader.loadDefaultProperties();
        PrecinctLoader.loadDefaultProperties();
        AppData.setStateMap(new LinkedHashMap<String, State>());

        AppData.setPrecinctMap(PrecinctLoader.loadPrecinct("MO")); //temporarly will only use state later on
        AppData.getStateMap().put("MO",new State("MO",AppData.getPrecinctMap()));

        SpringApplication.run(CourseApiApp.class, args);
    }
}
