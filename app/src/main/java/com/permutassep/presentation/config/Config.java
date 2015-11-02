package com.permutassep.presentation.config;

public class Config {

    /**
     * The URL of the web service for the users registration 
     */
	public static final String APP_PREFERENCES_NAME = "PERMUTAS_SEP_PREFERENCES";	

    /**
     * Endpoint of the InegiFacil REST service
     */
    public static final String INEGI_FACIL_REST_BASE_URL = "http://inegifacil.com/";

    /**
     * Date format used in the API REST Service
     */
    public static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * Date format used in the entire app
	 */
	public static final String APP_DATE_FORMAT = "HH:mm' - 'dd MMM yy";

	/**
	 * Number of publications per page in the news feed screen (fragment).
	 */
	public static final int NEWS_FEED_ITEMS_PER_PAGE = 12;

	/**
	 * Password min length
	 */
	public static final int PASSWORD_MIN_LENGTH = 8;

	/**
	 * Password max length
	 */
	public static final int PASSWORD_MAX_LENGTH = 32;

	/**
	 * Key of the token in the query string parameter for the password reset URL
	 */
	public static final String PWD_RESET_TOKEY_KEY = "token";

	/**
	 * Key of the email in the query string parameter for the password reset URL
	 */
	public static final String PWD_RESET_EMAIL_KEY = "email";
}