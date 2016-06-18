/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino.app.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.app.service.PasteService;
import timber.log.Timber;

public final class MainFragment extends Fragment {

  @Nullable @BindView(R.id.service_enabled_text) Button enabled;
  @Nullable private Unbinder unbinder;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_main, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    assert unbinder != null;
    unbinder.unbind();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    assert enabled != null;
    enabled.setOnClickListener(v -> {
      Timber.d("Stop Paste Service");
      PasteService.getInstance().stopPasteService();
    });
  }
}
