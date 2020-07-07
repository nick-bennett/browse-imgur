package edu.cnm.deepdive.browseimgur.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.browseimgur.R;
import edu.cnm.deepdive.browseimgur.model.Gallery;
import edu.cnm.deepdive.browseimgur.model.Image;
import edu.cnm.deepdive.browseimgur.view.GalleryListAdapter;
import edu.cnm.deepdive.browseimgur.viewmodel.ListViewModel;

public class GallerySearchFragment extends Fragment implements
    GalleryListAdapter.OnItemSelectedHelper {

  private ListViewModel viewModel;
  private GalleryListAdapter galleryListAdapter;
  private ProgressBar loadingView;
  private TextView listError;
  private RecyclerView galleryArray;

  private Gallery[] galleries;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.gallery_list, container, false);
    galleryArray = view.findViewById(R.id.recycler_view);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity())
        .get(ListViewModel.class);
    viewModel.getGalleries().observe(getViewLifecycleOwner(), (galleries) -> {
      if (galleries != null) {
        galleryArray.setVisibility(View.VISIBLE);
        galleryArray.setAdapter(new GalleryListAdapter(getContext(), galleries, this));
      }
    });
    viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
      loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
      if (loading) {
        listError.setVisibility(View.GONE);
        galleryArray.setVisibility(View.GONE);
      }
    });

    viewModel.getError().observe(getViewLifecycleOwner(), error -> {
      listError.setVisibility(error ? View.VISIBLE : View.GONE);
    });


  }

  @Override
  public void onSelected(Gallery gallery, Image image) {
    ImageDetailDialogFragment fragment = ImageDetailDialogFragment.newInstance();
    fragment.show(getChildFragmentManager(), fragment.getClass().getName());
  }

}
