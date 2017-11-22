package com.android.unideal.rtp;

public class RunTimePermission {
    private boolean requestForPermission = false;
    private boolean allPermissionGranted = false;

    public boolean isRequestForPermission() {
        return requestForPermission;
    }

    public RunTimePermission setRequestForPermission(boolean requestForPermission) {
        this.requestForPermission = requestForPermission;
        return this;
    }

    public boolean isAllPermissionGranted() {
        return allPermissionGranted;
    }

    public RunTimePermission setAllPermissionGranted(boolean allPermissionGranted) {
        this.allPermissionGranted = allPermissionGranted;
        return this;
    }
}
