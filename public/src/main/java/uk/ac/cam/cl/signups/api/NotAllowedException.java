package uk.ac.cam.cl.signups.api;

public class NotAllowedException extends Exception {

    private static final long serialVersionUID = 2446965527052329160L; // generated
    
    public NotAllowedException(String message) {
        super(message);
    }

}
