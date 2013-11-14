import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyControllerTest.Config.class)
@ActiveProfiles("unit") // we don't want the component scan to pick the config class
@WebAppConfiguration
public class MyControllerTest {

    @Autowired WebApplicationContext wac;

    @Autowired MyService service;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(this.wac).build();
        when(service.list()).thenReturn(asList(new MyEntity("1"), new MyEntity("2")));
    }

    @Test
    public void testList() throws Exception {
        mockMvc.perform(get("/my"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().methodName(is("list")))
                .andExpect(header().string("Content-Type", is("application/json;charset=UTF-8")))
                .andExpect(content().string(is("[{\"id\":\"1\"},{\"id\":\"2\"}]")))
                .andExpect(jsonPath("$[0].id").value(is("1")))
        ;
    }

    @Configuration
    @Profile("unit")
    @EnableWebMvc
    static class Config extends WebMvcConfigurerAdapter {

        @Bean MyController controller(MyService service) {
            return new MyController(service);
        }

        @Bean MyService service() {
            return mock(MyService.class);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            super.addInterceptors(registry);
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            super.addArgumentResolvers(argumentResolvers);
        }
    }
}
