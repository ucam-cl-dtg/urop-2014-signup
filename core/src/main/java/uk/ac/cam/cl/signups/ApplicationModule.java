package uk.ac.cam.cl.signups;

import uk.ac.cam.cl.dtg.teaching.exceptions.ExceptionHandler;
import uk.ac.cam.cl.dtg.teaching.exceptions.RemoteFailureHandler;

import com.google.inject.Binder;
import com.google.inject.Module;

public class ApplicationModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(RemoteFailureHandler.class);
		binder.bind(ExceptionHandler.class);
	}

}
