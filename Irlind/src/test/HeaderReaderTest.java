package test;

import org.junit.jupiter.api.Test;
import com.company.server.HeaderReader;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderReaderTest {
    private HeaderReader hr = new HeaderReader();

    @Test
    void contentType() {
        hr.ingest("Content-Type: application/json");

        assertThat(hr.getHeader("Content-Type"))
                .isEqualTo("application/json");
    }

    @Test
    void host() {
        hr.ingest("Host: localhost:8080");

        assertThat(hr.getHeader("Host"))
                .isEqualTo("localhost:8080");
    }

    @Test
    void contentLength() {
        hr.ingest("Content-Length: 44");

        assertThat(hr.getHeader("Content-Length"))
                .isEqualTo("44");
        assertThat(hr.getContentLength())
                .isEqualTo(44);
    }

    @Test
    void noContentLength() {
        assertThat(hr.getHeader("Content-Length"))
                .isNull();
        assertThat(hr.getContentLength())
                .isZero();
    }
}
