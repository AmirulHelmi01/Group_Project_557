package com.example.groupproject.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL = "https://blackholecompany.000webhostapp.com/prestige/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    // return ConsultService instance
    public static ConsultService getConsultService() {
        return RetrofitClient.getClient(BASE_URL).create(ConsultService.class);
    }
}