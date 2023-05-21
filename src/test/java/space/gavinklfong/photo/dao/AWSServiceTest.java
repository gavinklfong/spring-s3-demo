package space.gavinklfong.photo.dao;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import space.gavinklfong.photo.config.AppConfig;
import space.gavinklfong.photo.dao.setup.AWSServiceTestInitializer;

@SpringBootTest(classes = {AppConfig.class})
@ContextConfiguration(
        initializers = { AWSServiceTestInitializer.class }
)
public abstract class AWSServiceTest {

}
