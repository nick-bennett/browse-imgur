package edu.cnm.deepdive.browseimgur.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.browseimgur.BuildConfig;
import edu.cnm.deepdive.browseimgur.model.Gallery;
import edu.cnm.deepdive.browseimgur.service.ImgurService;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class ListViewModel extends AndroidViewModel {

  private MutableLiveData<List<Gallery>> galleries;
  private MutableLiveData<Boolean> loadError = new MutableLiveData<Boolean>();
  private MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();
  private MutableLiveData<Throwable> throwable;
  private CompositeDisposable pending;
  ImgurService imgurService;

  public ListViewModel(@NonNull Application application) {
    super(application);
    imgurService = ImgurService.getInstance();
    galleries = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    loadError = new MutableLiveData<>();
    loading = new MutableLiveData<>();
    pending = new CompositeDisposable();
    loadData();
  }

  public LiveData<List<Gallery>> getGalleries() {
    return galleries;
  }

  public LiveData<Boolean> getLoading() {
    return loading;
  }

  public LiveData<Boolean> getError() {
    return loadError;
  }

  public MutableLiveData<Throwable> getThrowable() {
    return throwable;
  }


  @SuppressLint("CheckResult")
  public void loadData() {
    pending.add(
        imgurService.getSearchResult(BuildConfig.CLIENT_ID,
            "fish AND sharks")
            .subscribeOn(Schedulers.io())
            .map((result) -> {
              List<Gallery> galleries = result.getData();
              galleries.removeIf((gallery) ->
                  gallery.getImages() == null || gallery.getImages().isEmpty());
              return galleries;
            })
            .subscribe(
                galleries::postValue,
                throwable::postValue
            )
    );
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    pending.clear();
  }

}
