package com.example.groupproject.remote;

import com.example.groupproject.model.Consultation;
import com.example.groupproject.model.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConsultService {
    @GET("api/consultation")
    Call<List<Consultation>> getAllConsult(@Header("api-key") String api_key);

    @POST("api/consultation")
    Call<Consultation> requestConsult(@Header ("api-key") String apiKey, @Body Consultation consultation);

    @POST("api/consultation/update")
    Call<Consultation> updateConsult(@Header ("api-key") String apiKey,  @Body Consultation consultation);

    /* Delete consultation based on the id
     * @return DeleteResponse object
     */
    @POST("api/consultation/delete/{consultation_id}")
    Call<DeleteResponse> deleteConsult(@Header ("api-key") String apiKey, @Path("consultation_id") int consultation_id);
}