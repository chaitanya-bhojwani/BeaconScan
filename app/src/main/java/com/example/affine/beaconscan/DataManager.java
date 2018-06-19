package com.example.affine.beaconscan;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;

public class DataManager {

    private ApiService apiService;

    DataManager(ApiService apiService) {
        this.apiService = apiService;
    }

    public Observable<ResponseItem> sendPost(ScanDataModel scanDataModel) {
        return apiService.savePost(scanDataModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
