package ${package}.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {"classpath:spring.xml"})
@MapperScan("${package}.**.dao")
public class SpringConfig {
}
