/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.signups;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/rest/*" },
initParams = {
        @WebInitParam(name = "javax.ws.rs.Application", value = "uk.ac.cam.cl.signups.ApplicationRegister"),
        @WebInitParam(name = "resteasy.servlet.mapping.prefix", value="/rest/")
})
public class HttpServletDispatcherV3 extends HttpServletDispatcher {
}
