package engine.multiplatform.gpu;

/**
 * This is a class designed to create an Object representation of data structures used in rendering
 * that would often be represented using a numerical ID or address.
 * Its function is to allow easier integration of external APIs and Frameworks
 * into Java's and VQ's Object-Oriented design and system.
 *
 * No class should ever directly implement this interface; instead it is an interface extended by other interfaces that represent specific kinds of API-bound structures.
 *
 * Since only one render API should ever be active, this class can ALWAYS be safely cast to the internal classes within the active Render implementation.
 * if more than one render API is being used in a given runtime, then someone has made a seriously fatal mistake and should be fired immediately.
 */
public interface GPUObject {

    /**
     * tells what render backend this came from.
     * supported render APIs:
     * 0:unknown (This should absolutely under no circumstances ever happen. Not in all time and space should this value ever be returned by this function)
     * 1:GL33
     *
     *
     * @return the render backend ID
     */
    int getRenderType();
}
