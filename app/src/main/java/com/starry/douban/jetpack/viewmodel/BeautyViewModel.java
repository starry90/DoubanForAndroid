package com.starry.douban.jetpack.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.starry.douban.constant.Apis;
import com.starry.douban.jetpack.httpstatuslivedata.HttpStatusLiveData;
import com.starry.douban.model.BeautyModel;
import com.starry.douban.model.GankBaseModel;
import com.starry.douban.util.StringUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.FinalCallback;
import com.starry.http.error.ErrorModel;

/**
 * @author Starry Jerry
 * @since 2021/3/25.
 */
public class BeautyViewModel extends ViewModel {

    public final HttpStatusLiveData<GankBaseModel<BeautyModel>> beautyLiveData = new HttpStatusLiveData<>();

    public void loadBeauty(int pageSize, int pageNo) {
        String url = StringUtils.format(Apis.GANK_BEAUTY, pageSize, pageNo);
        HttpManager.get(url)
                .tag(beautyLiveData)
                .build()
                .enqueue(new FinalCallback<GankBaseModel<BeautyModel>>() {

                    @Override
                    public void onSuccess(GankBaseModel<BeautyModel> response, Object... obj) {
                        beautyLiveData.postSuccess(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                        beautyLiveData.postFailure(errorModel);
                    }

                });
    }
}
