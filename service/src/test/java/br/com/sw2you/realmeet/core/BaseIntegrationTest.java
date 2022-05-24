package br.com.sw2you.realmeet.core;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import br.com.sw2you.realmeet.Application;
import br.com.sw2you.realmeet.api.ApiClient;
import java.net.MalformedURLException;
import java.net.URL;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Application.class)
public abstract class BaseIntegrationTest {
    @Autowired
    private Flyway flyway;

    @LocalServerPort
    private int serverPort;

    @BeforeEach
    void setup() throws Exception {
        setupFlyWay();
        setupEach();
    }

    protected void setupEach() throws Exception {}

    protected void setLocalHostBasePath(ApiClient apiClient, String path) throws MalformedURLException {
        apiClient.setBasePath(new URL("http", "localhost", serverPort, path).toString());
    }

    private void setupFlyWay() {
        flyway.clean();
        flyway.migrate();
    }
}
