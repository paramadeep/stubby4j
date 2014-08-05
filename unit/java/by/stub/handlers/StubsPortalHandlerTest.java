package by.stub.handlers;

import by.stub.cli.ANSITerminal;
import by.stub.database.StubbedDataManager;
import by.stub.yaml.stubs.*;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;


/**
 * @author Alexander Zagniotov
 * @since 6/30/12, 8:15 PM
 */
@SuppressWarnings("serial")

public class StubsPortalHandlerTest {

   private StubbedDataManager mockStubbedDataManager = Mockito.mock(StubbedDataManager.class);
   private Request mockRequest = Mockito.mock(Request.class);
   private HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
   private HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
   private PrintWriter mockPrintWriter = Mockito.mock(PrintWriter.class);

   private final String someResultsMessage = "we have results";

   @BeforeClass
   public static void beforeClass() throws Exception {
      ANSITerminal.muteConsole(true);
   }

   @Before
   public void beforeEach() throws Exception {
      mockRequest = Mockito.mock(Request.class);
      mockStubbedDataManager = Mockito.mock(StubbedDataManager.class);
      mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
      mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
   }

   @Test
   public void verifyBehaviourDuringHandleGetRequestWithNoResults() throws Exception {

      final String requestPathInfo = "/path/1";

      final NotFoundStubResponse mockStubResponse = Mockito.mock(NotFoundStubResponse.class);

      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.GET.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.NOTFOUND);

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.NOT_FOUND_404);
      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.OK_200);
   }

   @Test
   public void verifyBehaviourDuringHandlePostRequestWithNoResults() throws Exception {

      final String postData = "postData";
      final String requestPathInfo = "/path/1";

      final NotFoundStubResponse mockStubResponse = Mockito.mock(NotFoundStubResponse.class);

      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.POST.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.NOTFOUND);
      final InputStream inputStream = new ByteArrayInputStream(postData.getBytes());
      Mockito.when(mockHttpServletRequest.getInputStream()).thenReturn(new ServletInputStream() {
         @Override
         public int read() throws IOException {
            return inputStream.read();
         }
      });

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.NOT_FOUND_404);
      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.OK_200);
   }

   @Test
   public void verifyBehaviourDuringHandlePostRequestWithMissingPostData() throws Exception {

      final String requestPathInfo = "/path/1";

      final NotFoundStubResponse mockStubResponse = Mockito.mock(NotFoundStubResponse.class);

      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.POST.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);

      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.OK_200);
      when(mockStubResponse.getStatus()).thenReturn("200");

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.BAD_REQUEST_400);
      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.OK_200);
   }

   @Test
   public void verifyBehaviourDuringHandlePostRequestWithEmptyPostData() throws Exception {

      final String requestPathInfo = "/path/1";

      final NotFoundStubResponse mockStubResponse = Mockito.mock(NotFoundStubResponse.class);

      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.POST.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.OK_200);
      when(mockStubResponse.getStatus()).thenReturn("200");

      final InputStream inputStream = new ByteArrayInputStream("".getBytes());
      Mockito.when(mockHttpServletRequest.getInputStream()).thenReturn(new ServletInputStream() {
         @Override
         public int read() throws IOException {
            return inputStream.read();
         }
      });

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.BAD_REQUEST_400);
      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.OK_200);
   }


   @Test
   public void verifyBehaviourDuringHandleGetRequestWithSomeResults() throws Exception {

      final String requestPathInfo = "/path/1";

      final StubResponse mockStubResponse = Mockito.mock(StubResponse.class);

      when(mockHttpServletResponse.getWriter()).thenReturn(mockPrintWriter);
      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.GET.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.OK_200);
      when(mockStubResponse.getStatus()).thenReturn("200");
      when(mockStubResponse.getResponseBodyAsBytes()).thenReturn(null);

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.OK_200);
   }


   @Test
   public void verifyBehaviourDuringHandleGetRequestWithNoAuthorizationHeaderSet() throws Exception {

      final String requestPathInfo = "/path/1";

      final UnauthorizedStubResponse mockStubResponse = Mockito.mock(UnauthorizedStubResponse.class);

      when(mockHttpServletResponse.getWriter()).thenReturn(mockPrintWriter);
      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.GET.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.UNAUTHORIZED);
      when(mockStubResponse.getStatus()).thenReturn("200");
      when(mockStubResponse.getBody()).thenReturn(someResultsMessage);

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED_401);
      verify(mockHttpServletResponse, times(1)).sendError(HttpStatus.UNAUTHORIZED_401, "You are not authorized to view this page without supplied 'Authorization' HTTP header");
      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.OK_200);
   }


   @Test
   public void verifyBehaviourDuringHandleGetRequestWithEmptyAuthorizationHeaderSet() throws Exception {

      final String requestPathInfo = "/path/1";

      // We already found that it was unauthorized
      final UnauthorizedStubResponse mockStubResponse = Mockito.mock(UnauthorizedStubResponse.class);

      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.GET.asString());
      when(mockHttpServletRequest.getHeader(StubRequest.AUTH_HEADER)).thenReturn("");
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);

      final StubRequest assertionStubRequest = StubRequest.createFromHttpServletRequest(mockHttpServletRequest);
      when(mockStubbedDataManager.findStubResponseFor(assertionStubRequest)).thenReturn(mockStubResponse);

      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.UNAUTHORIZED);
      when(mockStubResponse.getStatus()).thenReturn("200");

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.UNAUTHORIZED_401);
      verify(mockHttpServletResponse, times(1)).sendError(HttpStatus.UNAUTHORIZED_401, "You are not authorized to view this page without supplied 'Authorization' HTTP header");
      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.OK_200);
   }

   @Test
   public void verifyBehaviourDuringHandlePostRequestWithMatch() throws Exception {
      final String postData = "postData";
      final String requestPathInfo = "/path/1";

      final StubResponse mockStubResponse = Mockito.mock(StubResponse.class);

      when(mockHttpServletResponse.getWriter()).thenReturn(mockPrintWriter);
      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.POST.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubResponse.getStatus()).thenReturn("200");
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.OK_200);
      when(mockStubResponse.getResponseBodyAsBytes()).thenReturn(null);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);

      final InputStream inputStream = new ByteArrayInputStream(postData.getBytes());
      Mockito.when(mockHttpServletRequest.getInputStream()).thenReturn(new ServletInputStream() {
         @Override
         public int read() throws IOException {
            return inputStream.read();
         }
      });

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.OK_200);
   }


   @Test
   public void verifyBehaviourDuringHandleGetRequestWithLatency() throws Exception {

      final String requestPathInfo = "/path/1";

      final StubResponse mockStubResponse = Mockito.mock(StubResponse.class);

      when(mockHttpServletResponse.getWriter()).thenReturn(mockPrintWriter);
      when(mockHttpServletRequest.getMethod()).thenReturn(HttpMethod.GET.asString());
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubResponse.getLatency()).thenReturn("50");
      when(mockStubResponse.getStatus()).thenReturn("200");
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.OK_200);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);
      when(mockStubResponse.getResponseBodyAsBytes()).thenReturn(new byte[]{});
      Mockito.when(mockHttpServletResponse.getOutputStream()).thenReturn(new ServletOutputStream() {

         @Override
         public void write(final int i) throws IOException {

         }
      });

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.OK_200);
   }


   @Test
   public void verifyBehaviourDuringHandleGetRequestWithInvalidLatency() throws Exception {
      final String method = HttpMethod.GET.asString();
      final String requestPathInfo = "/path/1";

      final StubResponse mockStubResponse = Mockito.mock(StubResponse.class);

      when(mockHttpServletRequest.getMethod()).thenReturn(method);
      when(mockHttpServletRequest.getPathInfo()).thenReturn(requestPathInfo);
      when(mockStubResponse.getLatency()).thenReturn("43rl4knt3l");
      when(mockStubResponse.getStubResponseType()).thenReturn(StubResponseTypes.OK_200);
      when(mockStubbedDataManager.findStubResponseFor(Mockito.any(StubRequest.class))).thenReturn(mockStubResponse);

      final StubsPortalHandler stubsPortalHandler = new StubsPortalHandler(mockStubbedDataManager);
      stubsPortalHandler.handle(requestPathInfo, mockRequest, mockHttpServletRequest, mockHttpServletResponse);

      verify(mockHttpServletResponse, times(1)).setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);

      verify(mockHttpServletResponse, never()).setStatus(HttpStatus.OK_200);
      verify(mockPrintWriter, never()).println(someResultsMessage);
   }
}
