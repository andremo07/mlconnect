package br.com.mpconnect.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

public class NotificationServlet implements Servlet{

	 /*
     * A white space separated list of case sensitive HTTP method names
     * that are allowed to be processed by this servlet. * means allow all
     */
    private static final String ALLOWED_HTTP_METHODS_ATTR =
            "com.sun.faces.allowedHttpMethods";
    
    // Http method names must be upper case. http://www.w3.org/Protocols/HTTP/NoteMethodCS.html
    // List of valid methods in Http 1.1 http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9

    private enum HttpMethod {
        
        OPTIONS("OPTIONS"),
        GET("GET"),
        HEAD("HEAD"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        TRACE("TRACE"),
        CONNECT("CONNECT");
        
        private String name;
        
        HttpMethod(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
        
    }


    private Set<String> allowedUnknownHttpMethods;
    private Set<HttpMethod> allowedKnownHttpMethods;
    final private Set<HttpMethod> defaultAllowedHttpMethods = 
            EnumSet.range(HttpMethod.OPTIONS, HttpMethod.CONNECT);
    private Set<HttpMethod> allHttpMethods;

    private boolean allowAllMethods;

    /**
     * <p>Context initialization parameter name for a comma delimited list
     * of context-relative resource paths (in addition to
     * <code>/WEB-INF/faces-config.xml</code> which is loaded automatically
     * if it exists) containing JavaServer Faces configuration information.</p>
     */
    public static final String CONFIG_FILES_ATTR =
        "javax.faces.CONFIG_FILES";


    /**
     * <p>Context initialization parameter name for the lifecycle identifier
     * of the {@link Lifecycle} instance to be utilized.</p>
     */
    public static final String LIFECYCLE_ID_ATTR =
        "javax.faces.LIFECYCLE_ID";


    /**
     * The <code>Logger</code> for this class.
     */
    private static final Logger LOGGER =
          Logger.getLogger("javax.faces.webapp", "javax.faces.LogStrings");


    /**
     * <p>Factory for {@link FacesContext} instances.</p>
     */
    private FacesContextFactory facesContextFactory = null;


    /**
     * <p>The {@link Lifecycle} instance to use for request processing.</p>
     */
    private Lifecycle lifecycle = null;


    /**
     * <p>The <code>ServletConfig</code> instance for this servlet.</p>
     */
    private ServletConfig servletConfig = null;

    /**
     * From GLASSFISH-15632.  If true, the FacesContext instance
     * left over from startup time has been released.  
     */
    private boolean initFacesContextReleased = false;
    

    /**
     * <p>Release all resources acquired at startup time.</p>
     */
    @Override
	public void destroy() {

        facesContextFactory = null;
        lifecycle = null;
        servletConfig = null;
        uninitHttpMethodValidityVerification();

    }


    /**
     * <p>Return the <code>ServletConfig</code> instance for this servlet.</p>
     */
    @Override
	public ServletConfig getServletConfig() {

        return (this.servletConfig);

    }


    /**
     * <p>Return information about this Servlet.</p>
     */
    @Override
	public String getServletInfo() {

        return (this.getClass().getName());

    }


    /**
     * <p>Acquire the factory instances we will require.</p>
     *
     * @throws ServletException if, for any reason, the startup of
     * this Faces application failed.  This includes errors in the
     * config file that is parsed before or during the processing of
     * this <code>init()</code> method.
     */
    @Override
	public void init(ServletConfig servletConfig) throws ServletException {

        // Save our ServletConfig instance
        this.servletConfig = servletConfig;

        // Acquire our FacesContextFactory instance
        try {
            facesContextFactory = (FacesContextFactory)
                FactoryFinder.getFactory
                (FactoryFinder.FACES_CONTEXT_FACTORY);
        } catch (FacesException e) {
            ResourceBundle rb = LOGGER.getResourceBundle();
            String msg = rb.getString("severe.webapp.facesservlet.init_failed");
            Throwable rootCause = (e.getCause() != null) ? e.getCause() : e;
            LOGGER.log(Level.SEVERE, msg, rootCause);
            throw new UnavailableException(msg);
        }

        // Acquire our Lifecycle instance
        try {
            LifecycleFactory lifecycleFactory = (LifecycleFactory)
                FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            String lifecycleId ;

            // First look in the servlet init-param set
            if (null == (lifecycleId = servletConfig.getInitParameter(LIFECYCLE_ID_ATTR))) {
                // If not found, look in the context-param set 
                lifecycleId = servletConfig.getServletContext().getInitParameter
                    (LIFECYCLE_ID_ATTR);
            }

            if (lifecycleId == null) {
                lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
            lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
            initHttpMethodValidityVerification();
        } catch (FacesException e) {
            Throwable rootCause = e.getCause();
            if (rootCause == null) {
                throw e;
            } else {
                throw new ServletException(e.getMessage(), rootCause);
            }
        }

    }

    private void initHttpMethodValidityVerification() {

        assert (null == allowedUnknownHttpMethods);
        assert (null != defaultAllowedHttpMethods);
        assert (null == allHttpMethods);
        allHttpMethods = EnumSet.allOf(HttpMethod.class);

        // Configure our permitted HTTP methods

        allowedUnknownHttpMethods = Collections.emptySet();
        allowedKnownHttpMethods = defaultAllowedHttpMethods;
        
        String[] methods;
        String allowedHttpMethodsString = servletConfig.getServletContext().getInitParameter(ALLOWED_HTTP_METHODS_ATTR);
        if (null != allowedHttpMethodsString) {
            methods = allowedHttpMethodsString.split("\\s+");
            assert (null != methods); // assuming split always returns a non-null array result
            allowedUnknownHttpMethods = new HashSet(methods.length);
            List<String> allowedKnownHttpMethodsStringList = new ArrayList<String>();
            // validate input against allHttpMethods data structure
            for (String cur : methods) {
                if (cur.equals("*")) {
                    allowAllMethods = true;
                    allowedUnknownHttpMethods = Collections.emptySet();
                    return;
                }
                boolean isKnownHttpMethod;
                try {
                    HttpMethod.valueOf(cur);
                    isKnownHttpMethod = true;
                } catch (IllegalArgumentException e) {
                    isKnownHttpMethod = false;
                }
                
                if (!isKnownHttpMethod) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        HttpMethod [] values = HttpMethod.values();
                        Object [] arg = new Object[values.length + 1];
                        arg[0] = cur;
                        System.arraycopy(values, HttpMethod.OPTIONS.ordinal(), 
                                         arg, 1, values.length);
                        LOGGER.log(Level.WARNING,
                                "warning.webapp.facesservlet.init_invalid_http_method",
                                arg);
                    }
                    // prevent duplicates
                    if (!allowedUnknownHttpMethods.contains(cur)) {
                        allowedUnknownHttpMethods.add(cur);
                    }
                } else {
                    // prevent duplicates
                    if (!allowedKnownHttpMethodsStringList.contains(cur)) {
                        allowedKnownHttpMethodsStringList.add(cur);
                    }
                }
            }
            // Optimally initialize allowedKnownHttpMethods
            if (5 == allowedKnownHttpMethodsStringList.size()) {
                allowedKnownHttpMethods = EnumSet.of(
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(0)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(1)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(2)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(3)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(4))
                        );
            } else if (4 == allowedKnownHttpMethodsStringList.size()) {
                allowedKnownHttpMethods = EnumSet.of(
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(0)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(1)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(2)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(3))
                        );
                
            } else if (3 == allowedKnownHttpMethodsStringList.size()) {
                allowedKnownHttpMethods = EnumSet.of(
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(0)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(1)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(2))
                        );
                
            } else if (2 == allowedKnownHttpMethodsStringList.size()) {
                allowedKnownHttpMethods = EnumSet.of(
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(0)),
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(1))
                        );
                
            } else if (1 == allowedKnownHttpMethodsStringList.size()) {
                allowedKnownHttpMethods = EnumSet.of(
                        HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(0))
                        );
                
            } else {
                List<HttpMethod> restList = 
                        new ArrayList<HttpMethod>(allowedKnownHttpMethodsStringList.size() - 1);
                for (int i = 1; i < allowedKnownHttpMethodsStringList.size() - 1; i++) {
                    restList.add(HttpMethod.valueOf(
                            allowedKnownHttpMethodsStringList.get(i)
                            ));
                }
                HttpMethod first = HttpMethod.valueOf(allowedKnownHttpMethodsStringList.get(0));
                HttpMethod [] rest = new HttpMethod[restList.size()];
                restList.toArray(rest);
                allowedKnownHttpMethods = EnumSet.of(first, rest);
                
            } 
        }
    }

    private void uninitHttpMethodValidityVerification() {
        assert (null != allowedUnknownHttpMethods);
        assert (null != defaultAllowedHttpMethods);
        assert (null != allHttpMethods);

        allowedUnknownHttpMethods.clear();
        allowedUnknownHttpMethods = null;
        allowedKnownHttpMethods.clear();
        allowedKnownHttpMethods = null;
        allHttpMethods.clear();
        allHttpMethods = null;

    }


    /**
     * <p class="changed_modified_2_0"><span
     * class="changed_modified_2_2">Process</span> an incoming request,
     * and create the corresponding response according to the following
     * specification.</p>
     * 
     * <div class="changed_modified_2_0">
     *
     * <p>If the <code>request</code> and <code>response</code>
     * arguments to this method are not instances of
     * <code>HttpServletRequest</code> and
     * <code>HttpServletResponse</code>, respectively, the results of
     * invoking this method are undefined.</p>
     *
     * <p>This method must respond to requests that <span
     * class="changed_modified_2_2">contain</span> the following
     * strings by invoking the <code>sendError</code> method on the
     * response argument (cast to <code>HttpServletResponse</code>),
     * passing the code <code>HttpServletResponse.SC_NOT_FOUND</code> as
     * the argument. </p>
     *
     * <ul>
     *
<pre><code>
/WEB-INF/
/WEB-INF
/META-INF/
/META-INF
</code></pre>
     *
     * </ul>
     *
     
     * <p>If none of the cases described above in the specification for
     * this method apply to the servicing of this request, the following
     * action must be taken to service the request.</p>

     * <p>Acquire a {@link FacesContext} instance for this request.</p>

     * <p>Acquire the <code>ResourceHandler</code> for this request by
     * calling {@link
     * javax.faces.application.Application#getResourceHandler}.  Call
     * {@link
     * javax.faces.application.ResourceHandler#isResourceRequest}.  If
     * this returns <code>true</code> call {@link
     * javax.faces.application.ResourceHandler#handleResourceRequest}.
     * If this returns <code>false</code>, <span
     * class="changed_added_2_2">call {@link
     * javax.faces.lifecycle.Lifecycle#attachWindow} followed by </span>
     * {@link javax.faces.lifecycle.Lifecycle#execute} followed by
     * {@link javax.faces.lifecycle.Lifecycle#render}.  If a {@link
     * javax.faces.FacesException} is thrown in either case, extract the
     * cause from the <code>FacesException</code>.  If the cause is
     * <code>null</code> extract the message from the
     * <code>FacesException</code>, put it inside of a new
     * <code>ServletException</code> instance, and pass the
     * <code>FacesException</code> instance as the root cause, then
     * rethrow the <code>ServletException</code> instance.  If the cause
     * is an instance of <code>ServletException</code>, rethrow the
     * cause.  If the cause is an instance of <code>IOException</code>,
     * rethrow the cause.  Otherwise, create a new
     * <code>ServletException</code> instance, passing the message from
     * the cause, as the first argument, and the cause itself as the
     * second argument.</p>

     * <p class="changed_modified_2_0_rev_a">The implementation must
     * make it so {@link javax.faces.context.FacesContext#release} is
     * called within a finally block as late as possible in the
     * processing for the JSF related portion of this request.</p>

     * </div>
     *
     * @param req The servlet request we are processing
     * @param resp The servlet response we are creating
     *
     * @throws IOException if an input/output error occurs during processing
     * @throws ServletException if a servlet error occurs during processing

     */


    private boolean isHttpMethodValid(HttpServletRequest request) {
        boolean result = false;
        if (allowAllMethods) {
            result = true;
        } else {
            String requestMethodString = request.getMethod();
            HttpMethod requestMethod = null;
            boolean isKnownHttpMethod;
            try {
                requestMethod = HttpMethod.valueOf(requestMethodString);
                isKnownHttpMethod = true;
            } catch (IllegalArgumentException e) {
                isKnownHttpMethod = false;
            }
            if (isKnownHttpMethod) {
                result = allowedKnownHttpMethods.contains(requestMethod);
            } else {
                result = allowedUnknownHttpMethods.contains(requestMethodString);
            }
            
        }

        return result;
    }


	@Override
	public void service(ServletRequest req, ServletResponse resp)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
        if (!isHttpMethodValid(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (Thread.currentThread().isInterrupted()) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINE, "Thread {0} given to FacesServlet.service() in interrupted state", 
                        Thread.currentThread().getName());
            }
        }

        // If prefix mapped, then ensure requests for /WEB-INF are
        // not processed.
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            pathInfo = pathInfo.toUpperCase();
            if (pathInfo.contains("/WEB-INF/")
                || pathInfo.contains("/WEB-INF")
                || pathInfo.contains("/META-INF/")
                || pathInfo.contains("/META-INF")) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        if (!initFacesContextReleased) {
            FacesContext initFacesContext = FacesContext.getCurrentInstance();
            if (null != initFacesContext) {
                initFacesContext.release();
            }
            // Bug 20458755: ensure the special factory is removed, so as not
            // to incur an additional performance penalty at request processing
            // time.
            FactoryFinder.getFactory("com.sun.faces.ServletContextFacesContextFactory_Removal");
            initFacesContextReleased = true;
        }
        
        // Acquire the FacesContext instance for this request
        FacesContext context = facesContextFactory.getFacesContext
              (servletConfig.getServletContext(), request, response, lifecycle);

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "teste", "TESTE");

        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish("/notifications",message);
        FacesContext.getCurrentInstance().addMessage(null, message);
        //FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        
        // Execute the request processing lifecycle for this request
        try {
            ResourceHandler handler =
                  context.getApplication().getResourceHandler();
            if (handler.isResourceRequest(context)) {
                handler.handleResourceRequest(context);
            } else {
                lifecycle.attachWindow(context);
                lifecycle.execute(context);
                lifecycle.render(context);
            }
        } catch (FacesException e) {
            Throwable t = e.getCause();
            if (t == null) {
                throw new ServletException(e.getMessage(), e);
            } else {
                if (t instanceof ServletException) {
                    throw ((ServletException) t);
                } else if (t instanceof IOException) {
                    throw ((IOException) t);
                } else {
                    throw new ServletException(t.getMessage(), t);
                }
            }
        }
        finally {
            // Release the FacesContext instance for this request
            context.release();
        }
		
	}


}
