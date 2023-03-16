package test;

import com.company.cards.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.company.server.RouteManager;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockitoTesting {
    @Mock
    public User userMock;

    User user;

    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RouteManager route1=new RouteManager();
    @Mock
    private RouteManager route2=new RouteManager();

    @Test
    void testPostUsersRoute() throws JsonProcessingException {
        when(objectMapper.readValue(anyString(), eq(User.class))).thenReturn(userMock);
        route1.handleRoute("POST /users","dummyUserObject","dummyToken");
        verify(userMock).register();
    }
}
