package winlytics.io.survey;

/**
 * Created by Umur KAYA on 2/20/18.
 */

enum WinlyticsError{

    /**
     * Everything OK
     */
    OK,
    /**
     * Winlytics Webservices Unavailable
     */
    SERVICE_UNAVAILABLE,
    /**
     * Response corrupted
     */
    MALFORMED_RESPONSE,
    /**
     * HTTP and/or SSL erros
     */
    PROTOCOL_ERROR,
    /**
     * Configuration error caused by user
     */
    SETUP_ERROR,
    /**
     * Trying to call multiple instances of Winlytics
     */
    MULTIPLE_INSTANCE,
    /**
     * Unknown error logging
     */
    UNKNOWN_ERROR
}
