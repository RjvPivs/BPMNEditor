package com.bpmn.editor.view;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.bpmn.editor.editor.ThreeDViewer;

import games.rednblack.gdxar.GdxARConfiguration;
import games.rednblack.gdxar.GdxArApplicationListener;
import games.rednblack.gdxar.GdxLightEstimationMode;
import games.rednblack.gdxar.android.ARFragmentApplication;
import games.rednblack.gdxar.android.ARSupportFragment;

public class Launcher3D extends FragmentActivity implements AndroidFragmentApplication.Callbacks {
    private GdxArApplicationListener applicationListener;
    private static final String TAG = "GDX AR Application";
    ActivityResultLauncher<String[]> appPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useGL30 = true;
        configuration.a = 8;
        configuration.depth = 16;
        configuration.stencil = 8;
        configuration.numSamples = 2;

        if (cameraGranted == null || !cameraGranted) {
            Toast.makeText(this, "Camera permission not granted!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            launchAR(configuration);
        }
    });
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationListener = new ThreeDViewer(getIntent().getStringExtra("scheme"), this);
        appPermissionRequest.launch(new String[]{
                Manifest.permission.CAMERA
        });
    }

    @Override
    public void exit() {

    }
    private void launchAR(AndroidApplicationConfiguration configuration) {
        ARSupportFragment supportFragment = new ARSupportFragment();

        getSupportFragmentManager().beginTransaction().add(
                supportFragment, ARSupportFragment.TAG).commitAllowingStateLoss();
        supportFragment.getArSupported().thenAccept(useAR -> {
            removeSupportFragment();
            if (useAR) {
                ARFragmentApplication fragment = new ARFragmentApplication();
                fragment.setConfiguration(configuration);
                GdxARConfiguration gdxARConfiguration = new GdxARConfiguration();
                gdxARConfiguration.enableOnScreenInstructions = false;
                gdxARConfiguration.enableDepth = false;
                gdxARConfiguration.lightEstimationMode = GdxLightEstimationMode.ENVIRONMENTAL_HDR;
                fragment.setArApplication(applicationListener, gdxARConfiguration);

                setFragment(fragment);
            } else {
                Toast.makeText(this, "ARCore is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).exceptionally(ex -> {
            removeSupportFragment();
            Toast.makeText(this, "Failed to load ARCore check errors", Toast.LENGTH_SHORT).show();
            finish();
            return null;
        });
    }
    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment)
                .commitAllowingStateLoss();
    }

    private void removeSupportFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ARSupportFragment.TAG);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }
}