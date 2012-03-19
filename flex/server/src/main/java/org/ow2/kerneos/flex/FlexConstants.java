package org.ow2.kerneos.flex;

public final class FlexConstants {

    /**
     * Constructor.
     * Avoid class instantiation.
     */
    private FlexConstants() {

    }

    /*
    * Suffix used for the factory of an service
    */
    public static final String FACTORY_SUFFIX = "_FaCtOry";

    /*
     *  Topics
     */
    public static final String KERNEOS_FLEX_TOPIC_SUFFIX = "/flex";

    /*
     *  Granite Configuration
     */
    public static final String GRANITE_SERVICE = "granite-service";
    public static final String GRANITE_CHANNEL = "kerneos-graniteamf-";
    public static final String GRANITE_CHANNEL_URI = "/granite/amf";

    /*
     *  Gravity Configuration
     */
    public static final String GRAVITY_SERVICE = "messaging-service";
    public static final String GRAVITY_CHANNEL = "kerneos-gravityamf-";
    public static final String GRAVITY_CHANNEL_URI = "/gravity/amf";


    /*
     * Kerneos Services
     */
    public static final String KERNEOS_SERVICE = "kerneos-";
    public static final String KERNEOS_SERVICE_CONFIGURATION = KERNEOS_SERVICE + "configuration";
    public static final String KERNEOS_SERVICE_ASYNC_CONFIGURATION = KERNEOS_SERVICE + "async-configuration";
    public static final String KERNEOS_SERVICE_SECURITY = KERNEOS_SERVICE + "security";
    public static final String KERNEOS_SERVICE_ASYNC_SECURITY = KERNEOS_SERVICE + "async-security";
}
