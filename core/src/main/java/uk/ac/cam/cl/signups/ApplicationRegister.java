/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.signups;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import uk.ac.cam.cl.dtg.teaching.exceptions.RemoteFailureHandler;
import uk.ac.cam.cl.dtg.teaching.exceptions.ExceptionHandler;

/**
 * This class registers the resteasy handlers. The name is important since it is
 * used as a String in HttpServletDispatcherV3
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 *
 */
public class ApplicationRegister extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(SignupService.class);
        result.add(RemoteFailureHandler.class);
        result.add(ExceptionHandler.class);
        return result;
    }

}
